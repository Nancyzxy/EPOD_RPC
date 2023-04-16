package Framework;

import RPC.DeviceService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Random;

public class Device {
    int port;
    public DeviceImpl handler;
    public DeviceService.Processor<DeviceImpl> processor;
    public Integer nearestNodeCode;
    public TServer server;
    public ArrayList<TTransport> transports;

    public Device(int deviceId){
        port = new Random().nextInt(50000) + 10000;
        handler = new DeviceImpl(deviceId);
        processor = new DeviceService.Processor<>(handler);
        transports = new ArrayList<>();
    }

    public void start(){
        Runnable simple = () -> simple(processor);
        new Thread(simple).start();
    }

    public void stop(){
        for (TTransport transport : transports) {
            transport.close();
        }
        server.stop();
    }

    public void simple(DeviceService.Processor<DeviceImpl> processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            // Use this for a multithreaded server
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the device "+ handler.deviceId+" ...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNearestNode(int nearestNodeCode) {
        this.nearestNodeCode = nearestNodeCode;
    }
}
