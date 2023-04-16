package Framework;

import Handler.*;
import RPC.*;
import org.apache.thrift.TException;
import utils.Constants;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EdgeNodeImpl implements EdgeNodeService.Iface {
    public Map<List<Double>, List<UnitInNode>> unitResultInfo; //primly used for pruning
    public Map<List<Double>, UnitInNode> unitsStatusMap; // used to maintain the status of the unit in a node
    public Handler handler;
    public Map<Integer, EdgeNodeService.Client> clientsForEdgeNodes;
    public Map<Integer, DeviceService.Client> clientsForDevices;

    public EdgeNodeImpl() {
        this.unitsStatusMap = Collections.synchronizedMap(new HashMap<>());
        this.unitResultInfo = Collections.synchronizedMap(new HashMap<>());
        this.count = new AtomicInteger(0);
        if (Objects.equals(Constants.methodToGenerateFingerprint, "NETS")) {
            this.handler = new NETSHandler(this);
        } else if (Objects.equals(Constants.methodToGenerateFingerprint, "MCOD")) {
            this.handler = new MCODHandler(this);
        }
    }

    public void setClients(Map<Integer, EdgeNodeService.Client> clientsForEdgeNodes, Map<Integer, DeviceService.Client> clientsForDevices) {
        this.clientsForDevices = clientsForDevices;
        this.clientsForEdgeNodes = clientsForEdgeNodes;
    }

    /* this two fields are used for judge whether the node has complete uploading*/
    public AtomicInteger count;
    volatile Boolean flag = false;

    public void receiveAndProcessFP(Map<List<Double>, Integer> fingerprints, int edgeDeviceHashCode) {
        this.flag = false;
        for (List<Double> id : fingerprints.keySet()) {
            if (fingerprints.get(id) == Integer.MIN_VALUE) {
                unitsStatusMap.get(id).belongedDevices.remove(edgeDeviceHashCode);
                if (unitsStatusMap.get(id).belongedDevices.isEmpty()) {
                    unitsStatusMap.remove(id);
                }
            }
            if (!unitsStatusMap.containsKey(id)) {
                unitsStatusMap.put(id, new UnitInNode(id, 0));
            }
            unitsStatusMap.get(id).updateCount(fingerprints.get(id));
            unitsStatusMap.get(id).updateDeltaCount(fingerprints.get(id));
            unitsStatusMap.get(id).update();
            unitsStatusMap.get(id).belongedDevices.add(edgeDeviceHashCode);
        }
        ArrayList<Thread> threads = new ArrayList<>();
        count.incrementAndGet();
        boolean flag = count.compareAndSet(this.clientsForDevices.size(), 0);
        if (flag) {
            // node has finished collecting data, entering into the N-N phase, only one thread go into this loop
            this.flag = true; //indicate to other nodes I am ready
            for (UnitInNode unitInNode : unitsStatusMap.values()) {
                unitInNode.updateSafeness();
            }
            // 计算出unSafeUnits，对于这些unSafeUnits, 我们需要从别的设备中寻找邻居
            List<List<Double>> unSafeUnits =
                    unitsStatusMap.keySet().stream().filter(key -> unitsStatusMap.get(key).isSafe != 2).toList();

            // 第一阶段: 从属于同一个node下的其他device里找邻居, 会包括本身
            for (List<Double> unsafeUnit : unSafeUnits) {
                List<UnitInNode> unitInNodeList = unitsStatusMap.values().stream().filter(x -> x.isUpdated.get(this.hashCode()) == 1)
                        .filter(x -> this.handler.neighboringSet(unsafeUnit, x.unitID)).toList();
                unitInNodeList.forEach(x -> x.isUpdated.put(this.hashCode(), 0));
                if (!unitResultInfo.containsKey(unsafeUnit)) {
                    unitResultInfo.put(unsafeUnit, new ArrayList<>());
                }
                unitInNodeList.forEach(
                        x -> {
                            for (UnitInNode unitInNode : unitResultInfo.get(unsafeUnit)) {
                                if (unitInNode.equals(x)) {
                                    unitInNode.updateCount(x.deltaCnt);
                                    unitInNode.belongedDevices.addAll(x.belongedDevices);
                                    return;
                                }
                            }
                            UnitInNode unitInNode = new UnitInNode(x);
                            unitResultInfo.get(unsafeUnit).add(unitInNode);//TODO: check深浅拷贝的问题
                        }
                );
            }
            pruning(1);
            unSafeUnits = unitsStatusMap.keySet().stream().filter(key -> unitsStatusMap.get(key).isSafe != 2).toList();

            //开始node - node 通信
            for (int edgeNodeCode : this.clientsForEdgeNodes.keySet()) {

                while (!EdgeNodeNetwork.nodeHashMap.get(edgeNodeCode).handler.flag) {
                }
                List<List<Double>> finalUnSafeUnits = unSafeUnits;
                Thread t = new Thread(() -> {
                    try {
                        this.clientsForEdgeNodes.get(edgeNodeCode).provideNeighborsResult(finalUnSafeUnits, this.hashCode());
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
                threads.add(t);
                t.start();
            }
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //已经向网络中所有的node请求过数据，开始把数据发还给device
            //Pruning Phase
            pruning(2);
            //send result back to the belonged device;
            sendDeviceResult();
        }
    }

    public void pruning(int stage) {
        //update UnitInNode update
        for (List<Double> UnitID : unitResultInfo.keySet()) {
            //add up all point count
            List<UnitInNode> list = unitResultInfo.get(UnitID);
            //同一个cell的点数大于k，那么这个cell就是safe的
            Optional<UnitInNode> exist = list.stream().filter(x -> x.unitID.equals(UnitID) && (x.pointCnt > Constants.K)).findAny();
            if (exist.isPresent()) {
                unitsStatusMap.get(UnitID).isSafe = 2;
            }
            if (stage == 2) {
                int totalCnt = list.stream().mapToInt(x -> x.pointCnt).sum();
                if (totalCnt <= Constants.K) {
                    unitsStatusMap.get(UnitID).isSafe = 0;
                }
            }

        }
    }

    /**
     * @param unSateUnits:  units need to find neighbors in this node
     * @param edgeNodeHash: from which node
     * @description find whether there are neighbor unit in local node
     */
    public void provideNeighborsResult(List<List<Double>> unSateUnits, int edgeNodeHash) {
        ArrayList<Thread> threads = new ArrayList<>();
        //对于每个unsafeUnit,计算完后马上发还消息，以达到pipeline的目标
        for (List<Double> unit : unSateUnits) {
            Thread t = new Thread(() -> {
                List<UnitInNode> unitInNodeList = unitsStatusMap.values().stream()
                        .filter(x -> x.isUpdated.get(edgeNodeHash) == 1)
                        .filter(x -> this.handler.neighboringSet(unit, x.unitID)).toList();
                unitInNodeList.forEach(x -> x.isUpdated.put(edgeNodeHash, 0));
                try {
                    this.clientsForEdgeNodes.get(edgeNodeHash).processResult(unit, unitInNodeList);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processResult(List<Double> unitID, List<UnitInNode> unitInNodeList) {
        if (!unitResultInfo.containsKey(unitID)) {
            unitResultInfo.put(unitID, unitInNodeList); // rpc streaming过来的是一个新的list，所以不用担心深浅拷贝的问题
            return;
        }
        unitInNodeList.forEach(
                x -> {
                    for (UnitInNode unitInNode : unitResultInfo.get(unitID)) {
                        if (unitInNode.equals(x)) {
                            unitInNode.updateCount(x.deltaCnt);
                            unitInNode.belongedDevices.addAll(x.belongedDevices);
                            return;
                        }
                    }
                    UnitInNode unitInNode = new UnitInNode(x);
                    unitResultInfo.get(unitID).add(unitInNode);
                }
        );
    }


    public void sendDeviceResult() {
        for (Integer edgeDeviceCode : this.clientsForDevices.keySet()) {
            Thread t = new Thread(() -> {
                //为每个设备产生答案
                // 1 安全状态
                List<UnitInNode> list = unitsStatusMap.values().stream().filter(
                        x -> x.belongedDevices.contains(edgeDeviceCode)).toList(); // 这个device当前有的所有unit
                HashMap<List<Double>, Integer> status = new HashMap<>();
                for (UnitInNode i : list) {
                    status.put(i.unitID, i.isSafe);
                }

                // 2 该向哪个device要什么数据
                list = unitsStatusMap.values().stream().filter(
                        x -> (x.belongedDevices.contains(edgeDeviceCode) && (x.isSafe == 1))).toList();
                //只有不安全的unit才需要向其他device要数据
                HashMap<Integer, Set<List<Double>>> result = new HashMap<>();
                //deviceHashCode: unitID
                for (UnitInNode unitInNode : list) {
                    unitResultInfo.get(unitInNode.unitID).forEach(
                            x -> {
                                Set<Integer> deviceList = x.belongedDevices;
                                for (Integer y : deviceList) {
                                    if (!result.containsKey(y)) {
                                        result.put(y, new HashSet<>());
                                    }
                                    result.get(y).add(x.unitID);
                                }
                            }
                    );
                }
                try {
                    this.clientsForDevices.get(edgeDeviceCode).getExternalData(status, result);
                } catch (TException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

}
