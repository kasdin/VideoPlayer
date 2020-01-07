package application;

import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.*;

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
        //String path = new File("/Users/kasperdinsen/Downloads/VideoPlayer/src/presentation/ressources/videofiles/joker.mp4").getAbsolutePath();

        String path1 = new File("/Users/kasperdinsen/Downloads/VideoPlayer/src/presentation/ressources/videofiles/irishman.mp4").getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path1).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);

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

}