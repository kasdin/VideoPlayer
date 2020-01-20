package application;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    public MediaPlayerController(String path) {
        this.mediaPath = path;
    }


    @FXML private GridPane mediaPlaylistGrid;

    @FXML private Slider sliderSeek;

    @FXML private Label labelCurrentTime;

    @FXML private Label labelTotalDuration;

    @FXML private Slider sliderSound;

    @FXML private Label labelCurrentSoundVolume;

    @FXML private Button buttonFullscreen;

    @FXML private ImageView imagePlay;

    @FXML private AnchorPane mediaplayerMenu;

    @FXML private MediaView mediaV;

    @FXML private Button play;

    @FXML private Button pause;

    @FXML private Button stop;

    @FXML private Button mute;

    @FXML private Button back;

    private String mediaPath;
    private MediaPlayer mediaPlayer;
    private Media media;

    private ArrayList<String> imagePath = new ArrayList<>();


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        createPlaylistThumbnails();
        createMediaPlayer();
        setActionListeners();
    }

    /**
     * This method creates the video thumbnails for the playlist.
     */
    private void createPlaylistThumbnails() {
        imagePath = Controller.fillPlaylistArray("fldThumbnailPath");

        for (int i = 0; i <imagePath.size() ; i++) {
            Controller controller = new Controller();
            String path = imagePath.get(i);
            String videoP = Controller.videoPath.get(i);
            ImageView thumbnail = controller.createThumbnail(path,videoP);
            mediaPlaylistGrid.addColumn(i+1, thumbnail);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            mediaPlaylistGrid.setHgap(30);
            thumbnail.setOnMouseClicked(event -> handleThumbnailPlay(videoP));
        }

    }

    /**
     * This method sets all action listeners.
     */
    private void setActionListeners() {
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMax() );

        back.setOnAction(event -> {
            try {
                handleBack((Stage)back.getScene().getWindow());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        sliderSeek.valueProperty().addListener(sliderChangeListener);
        mediaPlayer.currentTimeProperty().addListener(currentTimeChangeListener);
        sliderSound.valueProperty().addListener(sliderSoundListener);
        sliderSeek.setMin(1);
        sliderSeek.setBlockIncrement(1.0);
    }

    /**
     * this method creates the MediaPlayer.
     */
    private void createMediaPlayer() {
        String path = mediaPath;
        path = path.replaceAll("\\s+","");
        media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaV.setMediaPlayer(mediaPlayer);
    }

    /**
     * This method shows the playlist.
     */
    private void handleShowPlaylistGrid() {
        mediaPlaylistGrid.setVisible(true);
    }


    /**
     * This method disables the playlist.
     */
    private void handleDisablePlaylistGrid() {
        mediaPlaylistGrid.setVisible(false);
    }

    /**
     * This method listens for changes to the slider who controls the seek functions.
     */
    private InvalidationListener sliderChangeListener = o-> {
        Duration seekTo = Duration.seconds((int)sliderSeek.getValue());
        mediaPlayer.seek(seekTo);
    };

    /**
     * This method controls the sliders and updates our labels.
     */
    private InvalidationListener currentTimeChangeListener = o-> {
        if((int) mediaPlayer.getCurrentTime().toSeconds() == (int) mediaPlayer.getTotalDuration().toSeconds()) {
            handleStop();
        }
        sliderSeek.setMax(mediaPlayer.getTotalDuration().toSeconds());
        labelCurrentTime.setText(String.valueOf((int) mediaPlayer.getCurrentTime().toSeconds()));
        sliderSeek.valueProperty().removeListener(sliderChangeListener);
        sliderSeek.setValue((int) mediaPlayer.getCurrentTime().toSeconds());
        sliderSeek.valueProperty().addListener(sliderChangeListener);
    };

    /**
     * This method listen for changes to the sound slider and updates the values.
     */
    private InvalidationListener sliderSoundListener = o-> {
        mediaPlayer.setVolume(sliderSound.getValue());
        labelCurrentSoundVolume.setText(String.valueOf((int)(sliderSound.getValue()*100)));
    };

    /**
     * This method controls the play function.
     */
    @FXML private void handlePlay() {
        mediaPlayer.play();
        labelTotalDuration.setText(String.valueOf((int) mediaPlayer.getTotalDuration().toSeconds()));
        sliderSound.setValue(1.0);
        imagePlay.setImage(new Image("presentation/ressources/icons/pause.png"));
        play.setOnAction(event -> handlePause());
    }

    /**
     * This method stops a video.
     */
    @FXML private void handleStop() {
        mediaPlayer.stop();
        sliderSeek.setValue(0);
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));
        play.setOnAction(event -> handlePlay());
    }

    /**
     * This method pauses a video.
     */
    @FXML private void handlePause() {
        mediaPlayer.pause();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));
        play.setOnAction(event -> handlePlay());
    }

    /**
     * This is used to mute the video.
     */
    @FXML private void handleMute() {
        mediaPlayer.setMute(true);
        mute.setOnAction(event -> handleUnmute());
        sliderSound.setValue(0);
    }

    /**
     * This method is used for unmuting a video.
     */
    @FXML private void handleUnmute() {
        mediaPlayer.setMute(false);
        sliderSound.setValue(100);
        mute.setOnAction(event -> handleMute());
    }

    /**
     * This method handles your playlist.
     * @param videoPath
     */
    @FXML private void handleThumbnailPlay(String videoPath) {
        mediaPlayer.stop();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));

        String path1 = videoPath;
        path1 = path1.replaceAll("\\s+","");
        media = new Media(new File(path1).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaV.setMediaPlayer(mediaPlayer);
        mediaPlayer.currentTimeProperty().addListener(currentTimeChangeListener);

    }

    /**
     * This method is invoked when the back buttion is pressed.
     * @param stage1
     * @throws IOException
     */
    @FXML private void handleBack(Stage stage1) throws IOException {
        mediaPlayer.stop();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage1.setTitle("Video Player");
        stage1.setScene(new Scene(root, 1440, 900));
        stage1.setFullScreen(true);
    }

    /**
     * This is used for controlling the time slider.
     */
    @FXML private void handleSliderSeek() {
        mediaPlayer.seek(Duration.seconds((int)sliderSeek.getValue()));
    }

    /**
     * This method changes the size of the mediaview
     */
    @FXML private void handleFullscreenMax() {
        mediaPlaylistGrid.setVisible(false);
        mediaV.fitHeightProperty().setValue(1700);
        mediaV.fitWidthProperty().setValue(1700);
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMin());
    }

    /**
     * This method sets the MediaView size to small again.
     */
    @FXML private void handleFullscreenMin() {
        mediaV.fitHeightProperty().setValue(900);
        mediaV.fitWidthProperty().setValue(900);
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMax() );
        mediaPlaylistGrid.setVisible(true);
    }

}