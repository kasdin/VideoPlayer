package application;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
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

    private MediaPlayer mp;
    private Media me;


    @FXML public void handleShowPlaylistGrid() {
        mediaPlaylistGrid.setVisible(true);
    }

    @FXML public void handleDisablePlaylistGrid() {
        mediaPlaylistGrid.setVisible(false);
    }


    private InvalidationListener sliderChangeListener = o-> {
        Duration seekTo = Duration.seconds((int)sliderSeek.getValue());
        mp.seek(seekTo);
    };

    private InvalidationListener currentTimeChangeListener = o-> {
        if((int)mp.getCurrentTime().toSeconds() == (int)mp.getTotalDuration().toSeconds()) {
            handleStop();
        }
        sliderSeek.setMax(mp.getTotalDuration().toSeconds());
        labelCurrentTime.setText(String.valueOf((int)mp.getCurrentTime().toSeconds()));
        sliderSeek.valueProperty().removeListener(sliderChangeListener);
        sliderSeek.setValue((int)mp.getCurrentTime().toSeconds());
        sliderSeek.valueProperty().addListener(sliderChangeListener);
    };

    private InvalidationListener sliderSoundListener = o-> {
        mp.setVolume(sliderSound.getValue());
        labelCurrentSoundVolume.setText(String.valueOf((int)(sliderSound.getValue()*100)));
    };

    ArrayList<String> imagePath = new ArrayList<>();

    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){

        imagePath = Controller.fillPlaylistArray("fldThumbnailPath");

        for (int i = 0; i <imagePath.size() ; i++) {
            Controller controller = new Controller();
            String path = imagePath.get(i);
            String videoP = Controller.videoPath.get(i);
            ImageView thumbnail = controller.createThumbnail(path,videoP);
            mediaPlaylistGrid.addColumn(i+1, thumbnail);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            mediaPlaylistGrid.setHgap(40);
            thumbnail.setOnMouseClicked(event -> handleThumbnailPlay(videoP));
        }

        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMax() );

        String path1 = mediaPath;
        path1 = path1.replaceAll("\\s+","");
        me = new Media(new File(path1).toURI().toString());
        mp = new MediaPlayer(me);
        mediaV.setMediaPlayer(mp);
        back.setOnAction(event -> {
            try {
                handleBack((Stage)back.getScene().getWindow());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });


        sliderSeek.setMin(1);

        sliderSeek.setBlockIncrement(1.0);
        sliderSeek.valueProperty().addListener(sliderChangeListener);

        mp.currentTimeProperty().addListener(currentTimeChangeListener);

        sliderSound.valueProperty().addListener(sliderSoundListener);

    }

    @FXML private void handlePlay()
    {
        mp.play();
        labelTotalDuration.setText(String.valueOf((int)mp.getTotalDuration().toSeconds()));
        sliderSound.setValue(1.0);
        imagePlay.setImage(new Image("presentation/ressources/icons/pause.png"));
        play.setOnAction(event -> handlePause());
    }

    @FXML private void handleStop()
    {
        mp.stop();
        sliderSeek.setValue(0);
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));
        play.setOnAction(event -> handlePlay());
    }

    @FXML private void handlePause()
    {
        mp.pause();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));
        play.setOnAction(event -> handlePlay());
    }

    @FXML private void handleMute()
    {
        mp.setMute(true);
        mute.setOnAction(event -> handleUnmute());
        sliderSound.setValue(0);
    }


    @FXML private void handleUnmute()
    {
        mp.setMute(false);
        sliderSound.setValue(100);
        mute.setOnAction(event -> handleMute());
    }

    @FXML private void handleThumbnailPlay(String videoPath) {

        mp.stop();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));

        String path1 = videoPath;
        path1 = path1.replaceAll("\\s+","");
        me = new Media(new File(path1).toURI().toString());
        mp = new MediaPlayer(me);
        mediaV.setMediaPlayer(mp);
        mp.currentTimeProperty().addListener(currentTimeChangeListener);

    }

    @FXML private void handleBack(Stage stage1) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage1.setTitle("Video Player");
        stage1.setScene(new Scene(root, 800, 1000));
        mp.stop();

    }

    @FXML private void handleSliderSeek() {
        mp.seek(Duration.seconds((int)sliderSeek.getValue()));
    }


    @FXML private void handleFullscreenMax() {
        mediaPlaylistGrid.setVisible(false);

        DoubleProperty bindWidth = mediaV.fitWidthProperty();
        DoubleProperty bindHeight = mediaV.fitHeightProperty();
        bindWidth.bind(Bindings.selectDouble(mediaV.sceneProperty(), "width"));
        bindHeight.bind(Bindings.selectDouble(mediaV.sceneProperty(), "height"));
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMin());
        System.out.println(mediaV.getScene().getHeight());
        System.out.println(bindHeight.getValue());;
    }

    @FXML private void handleFullscreenMin() {
        mediaV.fitWidthProperty().unbind();
        mediaV.fitHeightProperty().unbind();
        mediaV.fitHeightProperty().setValue(900);
        mediaV.fitWidthProperty().setValue(900);
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMax() );
        mediaPlaylistGrid.setVisible(true);

    }

}