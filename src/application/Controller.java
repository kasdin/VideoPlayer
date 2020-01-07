package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button video1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        video1.setOnAction(event -> {
            try {
                startVideo((Stage)video1.getScene().getWindow());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void startVideo(Stage stage1) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mediaplayer.fxml"));
        stage1.setTitle("Hello World");
        stage1.setScene(new Scene(root, 1000, 1000));


    }
}