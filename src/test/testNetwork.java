package test;

import Framework.DeviceFactory;
import Framework.EdgeNodeNetwork;
import utils.Constants;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class testNetwork {

    public static void main(String[] args) throws Throwable {
        PrintStream ps;
        DeviceFactory edgeDeviceFactory;
        ps = new PrintStream(new FileOutputStream("src/Result/" + Constants.dataset + "_" +
                Constants.methodToGenerateFingerprint + ".txt"));
        edgeDeviceFactory = new DeviceFactory();

        System.setOut(ps);
        EdgeNodeNetwork.createNetwork(Constants.nn,Constants.dn, edgeDeviceFactory);
        System.out.println("started!");
        EdgeNodeNetwork.startNetwork();
    }
}

