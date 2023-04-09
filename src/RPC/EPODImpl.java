package RPC;

import org.apache.thrift.TException;

import java.util.*;

public class EPODImpl implements EPODService.Iface {
    @Override
    public void receiveAndProcessFP(Map<List<Double>, Integer> fingerprints, int edgeDeviceHashCode) throws InvalidException, TException {
        for (Map.Entry<List<Double>, Integer> entry : fingerprints.entrySet()) {
            for (Double d : entry.getKey()) {
                System.out.print(d+" ");
            }
            System.out.println();
            System.out.println(entry.getValue());
        }
    }

    @Override
    public void processResult(List<Double> unitID, List<UnitInNode> unitInNodeList) throws TException {
        for (Double d: unitID) {
            System.out.println(d);
        }
        for (UnitInNode u : unitInNodeList) {
            System.out.println(u.deltaCnt);
            System.out.println("isSafe: "+u.isSafe);
            System.out.println("pcnt: "+u.pointCnt);
            for (Integer i: u.belongedDevices) {
                System.out.println(i);
            }
            for (Map.Entry<Integer, Integer> entry : u.isUpdated.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
            for (Double d: u.unitID) {
                System.out.println(d);
            }
        }
    }

    @Override
    public void provideNeighborsResult(List<List<Double>> unSateUnits, int edgeNodeHash) throws TException {
        for (List<Double> LD: unSateUnits) {
            for (Double D: LD) {
                System.out.println(D);
            }
        }
        System.out.println(edgeNodeHash);
    }

    @Override
    public Map<List<Double>, List<Vector>> sendData(Set<List<Double>> bucketIds, int deviceHashCode) throws TException {
        for (List<Double> LD: bucketIds) {
            for (Double D: LD) {
                System.out.println(D);
            }
        }
        System.out.println(deviceHashCode);

        List<Double> list = new LinkedList<>();
        list.add(0.0);
        List<Vector> vc = new LinkedList<>();
        List<Double> list1 = new LinkedList<>();
        list1.add(1.0);
        vc.add(new Vector(list1,2,3));
        Map<List<Double>, List<Vector>> map = new HashMap<>();
        map.put(list,vc);
        return map;
    }

    @Override
    public void getExternalData(Map<List<Double>, Integer> status, Map<Integer, Set<List<Double>>> result) throws InvalidException, TException {
        for (Map.Entry<List<Double>, Integer> entry : status.entrySet()) {
            List<Double> key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("key is: ");
            for (Double k: key) {
                System.out.print(k+" ");
            }
            System.out.println("\nvalue is: "+value);
        }
        System.out.println("===========================");
        for (Map.Entry<Integer,Set<List<Double>>>entry: result.entrySet()){
            System.out.println("key is: "+entry.getKey());
            System.out.println("value is: ");
            for (List<Double> l: entry.getValue()) {
                for (Double d: l) {
                    System.out.print(d+" ");
                }
                System.out.println();
            }
        }
    }
}
