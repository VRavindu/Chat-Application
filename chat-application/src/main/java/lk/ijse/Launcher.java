package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/view/LoginForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChatApp Login");
        stage.getIcons().add(new Image("/img/chat-64.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}