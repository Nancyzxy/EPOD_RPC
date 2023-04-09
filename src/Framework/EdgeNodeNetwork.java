package Framework;

import DataStructure.Vector;
import utils.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EdgeNodeNetwork {
    public static HashMap<Integer, Device> deviceHashMap = new HashMap<>();
    public static HashMap<Integer, EdgeNode> nodeHashMap = new HashMap<>();
    public static ArrayList<Thread> threads=new ArrayList<>();

    public static EdgeNode createEdgeNode(){
        EdgeNode edgeNode = new EdgeNode();
        nodeHashMap.put(edgeNode.hashCode(),edgeNode);
        return edgeNode;
    }

    public static void createNetwork(int nn , int dn, DeviceFactory edgeDeviceFactory) throws Throwable {
        for (int i=0;i<nn;i++){
            EdgeNode node = createEdgeNode();
            ArrayList<Integer> devices = new ArrayList<>();
            for (int j=0;j<dn;j++){
                Device device = edgeDeviceFactory.createEdgeDevice(i*dn+j);
                deviceHashMap.put(device.hashCode(),device);
                device.setNearestNode(node);
                devices.add(device.hashCode());
            }
            node.setEdgeDevices(devices);
        }
    }

    public static void startNetwork() throws Throwable {
        //Print Logs
        System.out.println("# Dataset: "+Constants.dataset);
        System.out.println("Method: "+Constants.methodToGenerateFingerprint);
        System.out.println("Dim: "+Constants.dim);
        System.out.println("dn/nn: "+Constants.dn+"/"+Constants.nn);
        System.out.println("R/K/W/S: "+Constants.R+"/"+Constants.K+"/"+Constants.W+"/"+Constants.S);
        System.out.println("# of windows: "+(Constants.nW));
        for (EdgeNode node : nodeHashMap.values()) {
            node.active = true;
            Thread t1 = new Thread(node);
            threads.add(t1);
            t1.start();
        }
        for (Device device : deviceHashMap.values()) {
            device.active = true;
            Thread t2 = new Thread(device);
            threads.add(t2);
            t2.start();
        }

        int itr=0;
        long time = 0;
        while (itr < Constants.nS+Constants.nW-1) {
            //per slide
            System.out.println("===============================");
            System.out.println("This is the "+itr + " slides.");
            if(itr>=Constants.nS-1) {
                System.out.println("Window " + (itr - Constants.nS + 1));
            }
            ArrayList<Thread> arrayList = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (Device device : deviceHashMap.values()) {
                int finalItr = itr;
                Thread t = new Thread(() -> {
                    try {
                        Set<? extends Vector> outlier = device.detectOutlier(finalItr);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                arrayList.add(t);
            }
            for (Thread t : arrayList) {
                t.join();
            }
            time += System.currentTimeMillis()-start;
            System.out.println("Time cost for this slide is : "+(System.currentTimeMillis()-start));
            itr++;
        }
        stopNetwork();
        System.out.println("Average time cost is: "+time*1.0/(Constants.nS+Constants.nW-1));
    }

    public static void stopNetwork() throws IOException, InterruptedException {
        for (EdgeNode node : nodeHashMap.values()) {
            node.close();
        }
        for (Device device : deviceHashMap.values()) {
            device.close();
        }
        System.out.println("Ended!");
    }
}
