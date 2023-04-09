package RPC;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RPCFrame implements Runnable {
    public ServerSocket server;
    public int port;

    public boolean active = false;
    public void publish(int port) throws IOException  {
        this.server = new ServerSocket(port);
        while (active) {
            try {
                final Socket socket = server.accept();
                if (!active){
                    break;
                }
                new Thread(() -> {
                    try {
                        try {
                            try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                String methodName = input.readUTF();
                                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                                Object[] arguments = (Object[]) input.readObject();
                                try {
                                    Method method = this.getClass().getMethod(methodName, parameterTypes);
                                    Object result = method.invoke(this, arguments);
                                    output.writeObject(result);
                                } catch (Throwable t) {
                                    output.writeObject(t);
                                } finally {
                                    output.close();
                                }
                            }
                        } finally {
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.server.close();
    }

    public Object invoke(String host, int port, Method method, Object[] arguments) throws Throwable {
        try (Socket socket = new Socket(host, port)) {
            try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                output.writeUTF(method.getName());
                output.writeObject(method.getParameterTypes());
                output.writeObject(arguments);
                try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                    Object result = input.readObject();
                    if (result instanceof Throwable) {
                        throw (Throwable) result;
                    }
                    return result;
                }catch (EOFException ignored){
                    return null;
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            this.publish(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException, InterruptedException {
        this.active = false;
        Socket socket = new Socket("localhost", this.port);
        socket.close();
        Thread.sleep(10);
    }

}
