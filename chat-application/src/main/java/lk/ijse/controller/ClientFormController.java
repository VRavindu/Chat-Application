package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientFormController implements Runnable, Initializable {
    @FXML
    private TextArea txtAreaC;

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblClientName;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName = "Client";

    public void btnSendOnAction(ActionEvent actionEvent) {
        String msg = txtMsg.getText();
        if (msg!=null){
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
                System.out.println(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setClientName(String name) {
        clientName = name;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("192.168.8.114", 5000);
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String msg;

            while ((msg=dataInputStream.readUTF())!=null){
                txtAreaC.appendText(msg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClientName.setText(clientName);

        new Thread(()->{
            this.run();
        }).start();
    }
}
