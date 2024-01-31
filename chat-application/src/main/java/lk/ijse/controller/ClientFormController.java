package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ClientFormController implements Runnable, Initializable {

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblClientName;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vBox;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName = "Client";

    public void btnSendOnAction(ActionEvent actionEvent) {
        String msg = txtMsg.getText();
        if (msg!=null){
            try {
                dataOutputStream.writeUTF("/01");
                dataOutputStream.flush();
                dataOutputStream.writeUTF(clientName + " : " + msg);
                dataOutputStream.flush();
                System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
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
            //socket = new Socket("localhost", 4000);
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String msg;

            while ((msg=dataInputStream.readUTF())!=null){
                if (msg.equals("/01")){
                    msg = dataInputStream.readUTF();
                    HBox hBox = new HBox();
                    Label label = new Label(msg);
                    label.setFont(Font.font(20));
                    hBox.getChildren().add(label);

                    if (msg.startsWith(clientName)){
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                    }else {
                        hBox.setAlignment(Pos.CENTER_LEFT);
                    }

                    Platform.runLater(()->{
                        vBox.getChildren().add(hBox);
                    });
                } else if (msg.equals("/02")) {
                    String name = dataInputStream.readUTF();
                    byte[] bytes = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(bytes);

                    HBox hBox = new HBox();
                    Label label = new Label(name + " : ");
                    label.setFont(Font.font(20));
                    ByteArrayInputStream imageArray = new ByteArrayInputStream(bytes);
                    Image image = new Image(imageArray);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(250);
                    imageView.setFitHeight(250);


                    if (name.equals(clientName)){
                        hBox.getChildren().add(imageView);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                    }else {
                        hBox.getChildren().add(label);
                        hBox.getChildren().add(imageView);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                    }

                    Platform.runLater(()->{
                        vBox.getChildren().add(hBox);
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClientName.setText(clientName);

        new Thread(()->{
            this.run();
        }).start();
    }

    public void btnEmojiOnAction(ActionEvent actionEvent) {
    }

    public void btnImageOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(txtMsg.getScene().getWindow());
        if (file != null){
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                sendImage(clientName, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImage(String name, byte[] bytes) {
        try {
            dataOutputStream.writeUTF("/02");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.flush();
            dataOutputStream.write(bytes);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
