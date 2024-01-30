package lk.ijse.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private static Server server;
    private ArrayList<LocalSocket> localSockets;

    private ExecutorService pool;

    private Server() {
        localSockets = new ArrayList<>();
    }

    public void broadcast(String msg){
        for (LocalSocket localSocket : localSockets){
            if (localSocket != null){
                localSocket.sendMsg(msg);
                System.out.println(msg);
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5000);
            pool = Executors.newCachedThreadPool();

            while (true) {
                Socket accept = serverSocket.accept();
                LocalSocket localSocket = new LocalSocket(accept);

                localSockets.add(localSocket);
                pool.execute(localSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class LocalSocket implements Runnable{

        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        @Override
        public void run() {
            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                String msg;

                while ((msg=dataInputStream.readUTF())!=null){
                    broadcast(msg);
                    System.out.println(msg);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendMsg(String msg){
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public LocalSocket(Socket socket) {
            this.socket = socket;
        }

    }

    public static void main(String[] args) {
        Server server1 = new Server();
        server1.run();
    }
}
