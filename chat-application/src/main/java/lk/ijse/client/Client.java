package lk.ijse.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class Client {
    private Socket socket;
    private List<Client> clients;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String msg = "";

    public Client(Socket socket, List<Client> clients) {
    }
}
