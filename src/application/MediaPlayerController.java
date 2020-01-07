package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.media.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {
    @FXML
    private MediaView mediaV;

    @FXML
    private Button play;

    @FXML private Button pause;

    @FXML private Button stop;

    @FXML private Button mute;

    @FXML private Button back;

    private MediaPlayer mp;
    private Media me;


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        //String path = new File("src/presentation/ressources/videofiles/joker.mp4").getAbsolutePath()";

        String path1 = new File("src/presentation/ressources/videofiles/irishman.mp4").getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path1).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);

        back.setOnAction(event -> {
            try {
                handleBack((Stage)back.getScene().getWindow());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

    }

    @FXML
    /**
     * Handler for the play button
     */
    private void handlePlay()
    {
        // Play the mediaPlayer with the attached media
        mp.play();
    }

    @FXML private void handleStop()
    {
        mp.stop();
    }

    @FXML private void handlePause()
    {
        mp.pause();
    }

    @FXML private void handleMute()
    {
        mp.setMute(true);
    }

    @FXML private void handleBack(Stage stage1) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage1.setTitle("Video Player");
        stage1.setScene(new Scene(root, 1000, 1000));
        mp.stop();

    }

}