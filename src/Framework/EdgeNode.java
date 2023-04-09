package Framework;

import Handler.*;
import RPC.RPCFrame;
import utils.Constants;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EdgeNode extends RPCFrame implements Runnable {
    public ArrayList<Integer> edgeDevices;
    public Map<ArrayList<?>, List<UnitInNode>> unitResultInfo; //primly used for pruning
    public Map<ArrayList<?>, UnitInNode> unitsStatusMap; // used to maintain the status of the unit in a node
    public Handler handler;

    public EdgeNode() {
        this.port = new Random().nextInt(50000) + 10000;
        this.unitsStatusMap = Collections.synchronizedMap(new HashMap<>());
        this.unitResultInfo = Collections.synchronizedMap(new HashMap<>());
        this.count = new AtomicInteger(0);
        if (Objects.equals(Constants.methodToGenerateFingerprint, "NETS")) {
            this.handler = new NETSHandler(this);
        } else if (Objects.equals(Constants.methodToGenerateFingerprint, "MCOD")) {
            this.handler = new MCODHandler(this);
        }
    }

    /* this two fields are used for judge whether the node has complete uploading*/
    public AtomicInteger count;
    volatile Boolean flag = false;

    public void receiveAndProcessFP(HashMap<ArrayList<?>, Integer> fingerprints, Integer edgeDeviceHashCode) throws Throwable {
        this.flag = false;
        for (ArrayList<?> id : fingerprints.keySet()) {
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
        boolean flag = count.compareAndSet(edgeDevices.size(), 0);
        if (flag) {
            // node has finished collecting data, entering into the N-N phase, only one thread go into this loop
            this.flag = true; //indicate to other nodes I am ready
            for (UnitInNode unitInNode : unitsStatusMap.values()) {
                unitInNode.updateSafeness();
            }
            // �����unSafeUnits��������ЩunSafeUnits, ������Ҫ�ӱ���豸��Ѱ���ھ�
            List<ArrayList<?>> unSafeUnits =
                    unitsStatusMap.keySet().stream().filter(key -> unitsStatusMap.get(key).isSafe != 2).toList();

            // ��һ�׶�: ������ͬһ��node�µ�����device�����ھ�, ���������
            for (ArrayList<?> unsafeUnit : unSafeUnits) {
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
                            unitResultInfo.get(unsafeUnit).add(unitInNode);//TODO: check��ǳ����������
                        }
                );
            }
            pruning(1);
            unSafeUnits = unitsStatusMap.keySet().stream().filter(key -> unitsStatusMap.get(key).isSafe != 2).toList();

            //��ʼnode - node ͨ��
            for (EdgeNode node : EdgeNodeNetwork.nodeHashMap.values()) {
                if (node == this)
                    continue;
                while (!node.flag) {
                }
                List<ArrayList<?>> finalUnSafeUnits = unSafeUnits;
                Thread t = new Thread(() -> {
                    try {
                        Object[] parameters = new Object[]{finalUnSafeUnits, this.hashCode()};
                        invoke("localhost", node.port, EdgeNode.class.getMethod("provideNeighborsResult", List.class, int.class), parameters);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
                threads.add(t);
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }
            //�Ѿ������������е�node��������ݣ���ʼ�����ݷ�����device
            //Pruning Phase
            pruning(2);
            //send result back to the belonged device;
            sendDeviceResult();
        }
    }

    public void pruning(int stage) {
        //update UnitInNode update
        for (ArrayList<?> UnitID : unitResultInfo.keySet()) {
            //add up all point count
            List<UnitInNode> list = unitResultInfo.get(UnitID);
            //ͬһ��cell�ĵ�������k����ô���cell����safe��
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
    public void provideNeighborsResult(List<ArrayList<Short>> unSateUnits, int edgeNodeHash) {
        ArrayList<Thread> threads = new ArrayList<>();
        //����ÿ��unsafeUnit,����������Ϸ�����Ϣ���Դﵽpipeline��Ŀ��
        for (ArrayList<?> unit : unSateUnits) {
            Thread t = new Thread(() -> {
                List<UnitInNode> unitInNodeList = unitsStatusMap.values().stream()
                        .filter(x -> x.isUpdated.get(edgeNodeHash) == 1)
                        .filter(x -> this.handler.neighboringSet(unit, x.unitID)).toList();
                unitInNodeList.forEach(x -> x.isUpdated.put(edgeNodeHash, 0));
                Object[] parameters = new Object[]{unit, unitInNodeList};
                try {
                    invoke("localhost", EdgeNodeNetwork.nodeHashMap.get(edgeNodeHash).port,
                            EdgeNode.class.getMethod("processResult", ArrayList.class, List.class), parameters);
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

    public void processResult(ArrayList<?> unitID, List<UnitInNode> unitInNodeList) {
        if (!unitResultInfo.containsKey(unitID)) {
            unitResultInfo.put(unitID, unitInNodeList); // rpc streaming��������һ���µ�list�����Բ��õ�����ǳ����������
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
        for (Integer edgeDeviceCode : this.edgeDevices) {
            Thread t = new Thread(() -> {
                //Ϊÿ���豸������
                // 1 ��ȫ״̬
                List<UnitInNode> list = unitsStatusMap.values().stream().filter(
                        x -> x.belongedDevices.contains(edgeDeviceCode)).toList(); // ���device��ǰ�е�����unit
                HashMap<ArrayList<?>, Integer> status = new HashMap<>();
                for (UnitInNode i : list) {
                    status.put(i.unitID, i.isSafe);
                }

                // 2 �����ĸ�deviceҪʲô����
                list = unitsStatusMap.values().stream().filter(
                        x -> (x.belongedDevices.contains(edgeDeviceCode) && (x.isSafe == 1))).toList();
                //ֻ�в���ȫ��unit����Ҫ������deviceҪ����
                HashMap<Integer, HashSet<ArrayList<?>>> result = new HashMap<>();
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
                Object[] parameters = new Object[]{status, result};
                try {
                    invoke("localhost", EdgeNodeNetwork.deviceHashMap.get(edgeDeviceCode).port, Device.class.getMethod
                            ("getExternalData", HashMap.class, HashMap.class), parameters);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }


    public void setEdgeDevices(ArrayList<Integer> edgeDevices) {
        this.edgeDevices = edgeDevices;
    }
}