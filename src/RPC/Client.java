//package RPC;
///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements. See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership. The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License. You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied. See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//import org.apache.thrift.TException;
//import org.apache.thrift.protocol.TBinaryProtocol;
//import org.apache.thrift.protocol.TProtocol;
//import org.apache.thrift.transport.TSocket;
//import org.apache.thrift.transport.TTransport;
//
//import java.util.*;
//
//public class Client {
//    public static void main(String[] args) {
//
//        try {
//            TTransport transport = new TSocket("localhost", 9090);
//            transport.open();
//            TProtocol protocol = new TBinaryProtocol(transport);
//            EPODService.Client client = new EPODService.Client(protocol);
//            perform(client);
//            transport.close();
//        } catch (TException x) {
//            x.printStackTrace();
//        }
//    }
//
//    private static void perform(EPODService.Client client) throws TException {
////        HashMap<List<Double>, Integer> hashMap = new HashMap<>();
////        List<Double> arrayList = new ArrayList<>();
////        arrayList.add(1.0);
////        arrayList.add(2.0);
////        hashMap.put(arrayList, 1);
////        client.receiveAndProcessFP(hashMap, 1);
//
//        //void processResult(List<Double> unitID, List<UnitInNode> unitInNodeList)
////        List<Double> unitID = new ArrayList<>();
////        unitID.add(1.0);
////        unitID.add(2.0);
////        List<UnitInNode> unitInNodeList = new ArrayList<>();
////        UnitInNode unitInNode = new UnitInNode();
////        unitInNode.deltaCnt = 1;
////        unitInNode.isSafe = 1;
////        unitInNode.pointCnt = 2;
////        HashSet<Integer> n1 = new HashSet<>();
////        unitInNode.belongedDevices = n1;
////        Map<Integer,Integer> n2 = new HashMap<>();
////        n2.put(1,2);
////        unitInNode.isUpdated = n2;
////        List<Double> n3 = new ArrayList<>();
////        n3.add(1.0);
////        unitInNode.unitID = n3;
////        unitInNodeList.add(unitInNode);
////        client.processResult(unitID, unitInNodeList);
//
//        //Map<List<Double>, List<Vector>> sendData(Set<List<Double>> bucketIds, int deviceHashCode)
//        Set<List<Double>> bucketIds = new HashSet<>();
//        List<Double> arrayList = new ArrayList<>();
//        arrayList.add(3.76);
//        arrayList.add(4.0);
//        bucketIds.add(arrayList);
//        Map<List<Double>, List<Vector>> res = client.sendData(bucketIds, 10);
//        for (Map.Entry<List<Double>, List<Vector>> entry : res.entrySet()) {
//            for (Double d : entry.getKey()) {
//                System.out.print(d+" ");
//            }
//            System.out.println();
//            for (Vector v : entry.getValue()) {
//                System.out.println(v.values.size());
//                System.out.println(v.arrivalTime);
//                System.out.println(v.slideID);
//            }
//        }
//
//    }
//}