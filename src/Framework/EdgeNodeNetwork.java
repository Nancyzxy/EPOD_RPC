package Framework;

import RPC.DeviceService;
import RPC.EdgeNodeService;
import RPC.Vector;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EdgeNodeNetwork {
    public static HashMap<Integer, Device> deviceHashMap = new HashMap<>();
    public static HashMap<Integer, EdgeNode> nodeHashMap = new HashMap<>();
    public static ArrayList<Thread> threads = new ArrayList<>();

    public static EdgeNode createEdgeNode() {
        EdgeNode edgeNode = new EdgeNode();
        nodeHashMap.put(edgeNode.hashCode(), edgeNode);
        return edgeNode;
    }

    public static void createNetwork(int nn, int dn, DeviceFactory edgeDeviceFactory) throws Throwable {
        for (int i = 0; i < nn; i++) {
            EdgeNode node = createEdgeNode();
            ArrayList<Integer> devicesCodes = new ArrayList<>();
            for (int j = 0; j < dn; j++) {
                Device device = edgeDeviceFactory.createEdgeDevice(i * dn + j);
                deviceHashMap.put(device.hashCode(), device);
                device.setNearestNode(node.hashCode());
                devicesCodes.add(device.hashCode());
            }
            node.setDevices(devicesCodes);
        }
        //set clients for nodes and devices
        for (EdgeNode node : nodeHashMap.values()) {
            setClientsForEdgeNodes(node);
        }
        for (Device device : deviceHashMap.values()) {
            setClientsForDevices(device);
        }
    }

    public static void startNetwork() throws Throwable {
        //Print Logs
        System.out.println("# Dataset: " + Constants.dataset);
        System.out.println("Method: " + Constants.methodToGenerateFingerprint);
        System.out.println("Dim: " + Constants.dim);
        System.out.println("dn/nn: " + Constants.dn + "/" + Constants.nn);
        System.out.println("R/K/W/S: " + Constants.R + "/" + Constants.K + "/" + Constants.W + "/" + Constants.S);
        System.out.println("# of windows: " + (Constants.nW));
        for (EdgeNode node : nodeHashMap.values()) {
            node.start();
        }
        for (Device device : deviceHashMap.values()) {
            device.start();
        }

        int itr = 0;
        long time = 0;
        while (itr < Constants.nS + Constants.nW - 1) {
            //per slide
            System.out.println("===============================");
            System.out.println("This is the " + itr + " slides.");
            if (itr >= Constants.nS - 1) {
                System.out.println("Window " + (itr - Constants.nS + 1));
            }
            ArrayList<Thread> arrayList = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (Device device : deviceHashMap.values()) {
                int finalItr = itr;
                Thread t = new Thread(() -> {
                    try {
                        Set<? extends Vector> outlier = device.handler.detectOutlier(finalItr);
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
            time += System.currentTimeMillis() - start;
            System.out.println("Time cost for this slide is : " + (System.currentTimeMillis() - start));
            itr++;
        }
        stopNetwork();
        System.out.println("Average time cost is: " + time * 1.0 / (Constants.nS + Constants.nW - 1));
    }

    public static void stopNetwork() {
        for (EdgeNode node : nodeHashMap.values()) {
            node.stop();
        }
        for (Device device : deviceHashMap.values()) {
            device.stop();
        }
        System.out.println("Ended!");
    }

    public static void setClientsForEdgeNodes(EdgeNode node) throws TTransportException {
        Map<Integer, EdgeNodeService.Client> clientsForEdgeNodes = new HashMap<>();
        Map<Integer, DeviceService.Client> clientsForDevices = new HashMap<>();

        for (Integer nodeHashCode : nodeHashMap.keySet()) {
            TTransport transport = new TSocket("localhost", nodeHashMap.get(nodeHashCode).port);
            transport.open();
            node.transports.add(transport);
            TProtocol protocol = new TBinaryProtocol(transport);
            EdgeNodeService.Client client = new EdgeNodeService.Client(protocol);
            clientsForEdgeNodes.put(nodeHashCode, client);
        }

        for (Integer deviceCode : node.devicesCodes) {
            TTransport transport = new TSocket("localhost", deviceHashMap.get(deviceCode).port);
            transport.open();
            node.transports.add(transport);
            TProtocol protocol = new TBinaryProtocol(transport);
            EdgeNodeService.Client client = new EdgeNodeService.Client(protocol);
            clientsForEdgeNodes.put(deviceCode, client);
        }
        node.handler.setClients(clientsForEdgeNodes, clientsForDevices);
    }


    public static void setClientsForDevices(Device device) throws TTransportException {
        TTransport transport = new TSocket("localhost", nodeHashMap.get(device.nearestNodeCode).port);
        transport.open();
        device.transports.add(transport);
        TProtocol protocol = new TBinaryProtocol(transport);
        EdgeNodeService.Client clientForNearestNode = new EdgeNodeService.Client(protocol);

        Map<Integer, DeviceService.Client> clientsForDevices = new HashMap<>();
        for (Integer deviceHashCode : deviceHashMap.keySet()) {
            if (deviceHashCode == device.hashCode()) continue;
            TTransport transport1 = new TSocket("localhost", deviceHashMap.get(deviceHashCode).port);
            transport1.open();
            device.transports.add(transport1);
            TProtocol protocol1 = new TBinaryProtocol(transport1);
            DeviceService.Client client = new DeviceService.Client(protocol1);
            clientsForDevices.put(deviceHashCode, client);
        }
        device.handler.setClients(clientForNearestNode, clientsForDevices);
    }
}
