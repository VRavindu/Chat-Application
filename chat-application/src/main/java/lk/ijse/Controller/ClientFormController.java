package lk.ijse.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientFormController {
    @FXML
    private TextArea txtAreaC;

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblClientName;

    private String clientName = "Client";


    public void initialize(){
        lblClientName.setText(clientName);
    }
    public void btnSendOnAction(ActionEvent actionEvent) {
    }

    public void setClientName(String name) {
        clientName = name;
    }
}
