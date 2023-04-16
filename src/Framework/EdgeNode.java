package Framework;

import RPC.EdgeNodeService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Random;

public class EdgeNode {

    public EdgeNodeImpl handler;
    public int port;
    public EdgeNodeService.Processor<EdgeNodeImpl> processor;
    public ArrayList<Integer> devicesCodes;
    public TServer server;
    public ArrayList<TTransport> transports;

    public EdgeNode() {
        handler = new EdgeNodeImpl();
        processor = new EdgeNodeService.Processor<>(handler);
        port = new Random().nextInt(50000) + 10000;
        transports = new ArrayList<>();
    }

    public void start() {
        Runnable simple = () -> simple(processor);
        new Thread(simple).start();
    }
    public void stop() {
        for (TTransport transport : transports) {
            transport.close();
        }
        server.stop();
    }

    public void simple(EdgeNodeService.Processor<EdgeNodeImpl> processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
//            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            // Use this for a multithreaded server
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.printf("Starting the EdgeNode at port %d...%n", port);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDevices(ArrayList<Integer> devicesCodes) {
        this.devicesCodes = devicesCodes;
    }
}
