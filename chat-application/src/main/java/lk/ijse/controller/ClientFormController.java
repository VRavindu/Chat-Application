package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane emojiPane;

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
                dataOutputStream.writeUTF("/text");
                dataOutputStream.flush();
                dataOutputStream.writeUTF(clientName + " : " + msg);
                dataOutputStream.flush();
                txtMsg.clear();
                emojiPane.setVisible(false);
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
            //socket = new Socket("192.168.8.106", 5000);
            socket = new Socket("localhost", 5000);
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String msg;

            while ((msg=dataInputStream.readUTF())!=null){
                if (msg.equals("/text")){
                    msg = dataInputStream.readUTF();
                    String[] split = msg.split(" : ");
                    HBox hBox = new HBox();
                    Label label = new Label();
                    Label name = new Label();
                    label.setFont(Font.font(20));
                    label.setStyle("-fx-font-weight: bold;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-background-color: #525050;" +
                            "-fx-padding: 10px;" +
                            "-fx-alignment: center;" +
                            "-fx-background-radius: 25;");
                    hBox.setPadding(new Insets(5, 5, 5, 10));
                    name.setFont(Font.font(15));
                    name.setStyle("-fx-font-weight: bold;" +
                            "-fx-text-fill: black;");

                    if (msg.startsWith(clientName)){
                        label.setText(split[1]);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                    }else {
                        label.setStyle("-fx-font-weight: bold;" +
                                "-fx-text-fill: #525050;" +
                                "-fx-background-color: #ffffff;" +
                                "-fx-padding: 10px;" +
                                "-fx-alignment: center;" +
                                "-fx-background-radius: 25;");
                        name.setText(split[0] + " : ");
                        label.setText(split[1]);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                    }
                    hBox.getChildren().add(name);
                    hBox.getChildren().add(label);

                    Platform.runLater(()->{
                        vBox.getChildren().add(hBox);
                    });
                } else if (msg.equals("/image")) {
                    String name = dataInputStream.readUTF();
                    byte[] bytes = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(bytes);

                    HBox hBox = new HBox();
                    Label label = new Label(name + " : ");
                    label.setStyle("-fx-font-size: 15;" +
                            "-fx-font-weight: bold;");
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


    public void btnImageOnAction(MouseEvent mouseEvent) {
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
            dataOutputStream.writeUTF("/image");
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

    public void btnEmojiOnAction(MouseEvent mouseEvent) {
        if (!emojiPane.isVisible()) {
            emojiPane.setVisible(true);
        }else {
            emojiPane.setVisible(false);
        }
    }
    public void txtMsgOnAction(ActionEvent actionEvent) {
        btnSendOnAction(actionEvent);
    }

    public void btnEmoji1Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE42");
    }
    public void btnEmoji2Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE02");
    }
    public void btnEmoji3Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83E\uDD70");
    }
    public void btnEmoji4Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE0D");
    }
    public void btnEmoji5Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE18");
    }
    public void btnEmoji6Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE2E");
    }
    public void btnEmoji7Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE12");
    }
    public void btnEmoji8Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE44");
    }
    public void btnEmoji9Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\u2764");
    }
    public void btnEmoji10Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE20");
    }
    public void btnEmoji11Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE2D");
    }
    public void btnEmoji12Clicked(MouseEvent mouseEvent) {
        txtMsg.appendText("\uD83D\uDE34");
    }
}
