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
import javafx.scene.layout.Pane;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    ListIterator<String> it = Controller.videoPath.listIterator();


    public MediaPlayerController(String path) {
        this.mediaPath = path;

    }

    @FXML
    GridPane mediaPlaylistGrid;

    @FXML
    Slider sliderSeek;

    @FXML
    Label labelCurrentTime;

    @FXML Label labelTotalDuration;

    @FXML Slider sliderSound;

    @FXML Label labelCurrentSoundVolume;

    @FXML Button buttonFullscreen;

    @FXML private ImageView imagePlay;

    @FXML
    AnchorPane mediaplayerMenu;


    private String mediaPath;

    @FXML
    private MediaView mediaV;

    @FXML
    private Button play;

    @FXML private Button pause;

    @FXML private Button stop;

    @FXML private Button mute;

    @FXML private Button back;

    @FXML private Button buttonNext;


    private MediaPlayer mp;
    private Media me;

    @FXML public void handleShowPlaylistGrid() {
        mediaPlaylistGrid.setVisible(true);
    }

    @FXML public void handleDisablePlaylistGrid() {
        mediaPlaylistGrid.setVisible(false);
    }




    InvalidationListener sliderChangeListener = o-> {
        Duration seekTo = Duration.seconds((int)sliderSeek.getValue());
        System.out.println(mp.getTotalDuration().toSeconds());
        mp.seek(seekTo);
    };

    InvalidationListener currentTimeChangeListener = o-> {
        if((int)mp.getCurrentTime().toSeconds() == (int)mp.getTotalDuration().toSeconds()) {
            handleStop();
        }
        sliderSeek.setMax(mp.getTotalDuration().toSeconds());
        labelCurrentTime.setText(String.valueOf((int)mp.getCurrentTime().toSeconds()));
        sliderSeek.valueProperty().removeListener(sliderChangeListener);
        sliderSeek.setValue((int)mp.getCurrentTime().toSeconds());
        sliderSeek.valueProperty().addListener(sliderChangeListener);


    };

    InvalidationListener sliderSoundListener = o-> {
        mp.setVolume(sliderSound.getValue());
        labelCurrentSoundVolume.setText(String.valueOf((int)(sliderSound.getValue()*100)));


    };

    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        //String path = new File("src/presentation/ressources/videofiles/joker.mp4").getAbsolutePath()";
        //sliderSeek.setMax((int)mp.getTotalDuration().toSeconds());
        ArrayList<String> imagePath = new ArrayList<>();

        DB.selectSQL("select fldThumbnailPath from tblVideo\n" +
                "join tblVideoPlaylist on tblVideo.fldVideoID = tblVideoPlaylist.fldVideoID\n" +
                "join tblPlaylist on tblVideoPlaylist.fldPlaylistID = tblPlaylist.fldPlaylistID;");
        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                imagePath.add(data);

            }
        } while(true);

        for (int i = 0; i <imagePath.size() ; i++) {
            //System.out.println(imagePath.get(i));
            String path = imagePath.get(i);
            String videoP = Controller.videoPath.get(i);
            //System.out.println(videoPath.get(i));
            ImageView thumbnail = new ImageView();
            thumbnail.setFitWidth(201);
            thumbnail.setFitHeight(250);
            thumbnail.setPreserveRatio(true);
            thumbnail.setImage(new Image(path));
            //mediaPlaylistGrid.addRow(i + 1);
            mediaPlaylistGrid.addColumn(i+1, thumbnail);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            mediaPlaylistGrid.setHgap(40);
            thumbnail.setOnMouseClicked(event -> handleThumbnailPlay(videoP));
        }

        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMax() );




        System.out.println(Controller.videoPath.get(0));

        String path1 = mediaPath;
        System.out.println(path1);
        path1 = path1.replaceAll("\\s+","");
        System.out.println(path1);
        // Create new Media object (the actual media content)
        String tester = "";


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


        sliderSeek.setMin(1);

        sliderSeek.setBlockIncrement(1.0);
        sliderSeek.valueProperty().addListener(sliderChangeListener);

        mp.currentTimeProperty().addListener(currentTimeChangeListener);

        sliderSound.valueProperty().addListener(sliderSoundListener);






    }

    @FXML
    /**
     * Handler for the play button
     */
    private void handlePlay()
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
        System.out.println(path1);
        // Create new Media object (the actual media content)
        me = new Media(new File(path1).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);

        mp.currentTimeProperty().addListener(currentTimeChangeListener);



    }

    @FXML private void handleNext() {

        mp.stop();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));

        String path1 = it.next();
        path1 = path1.replaceAll("\\s+","");
        System.out.println(path1);
        // Create new Media object (the actual media content)
        me = new Media(new File(path1).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);

        mp.currentTimeProperty().addListener(currentTimeChangeListener);



    }

    @FXML private void handlePrevious() {

        mp.stop();
        imagePlay.setImage(new Image("presentation/ressources/icons/play.png"));

        String path1 = it.previous();
        path1 = path1.replaceAll("\\s+","");
        System.out.println(path1);
        // Create new Media object (the actual media content)
        me = new Media(new File(path1).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);

        mp.currentTimeProperty().addListener(currentTimeChangeListener);



    }

    @FXML private void handleBack(Stage stage1) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage1.setTitle("Video Player");
        stage1.setScene(new Scene(root, 800, 1000));
        mp.stop();

    }

    @FXML private void handleSliderSeek() {
        System.out.println((int)sliderSeek.getValue());
        mp.seek(Duration.seconds((int)sliderSeek.getValue()));
    }


    @FXML private void handleFullscreenMax() {
        mediaPlaylistGrid.setVisible(false);
        DoubleProperty bindWidth = mediaV.fitWidthProperty();
        DoubleProperty bindHeight = mediaV.fitHeightProperty();
        bindWidth.bind(Bindings.selectDouble(mediaV.sceneProperty(), "width"));
        bindHeight.bind(Bindings.selectDouble(mediaV.sceneProperty(), "height"));
        buttonFullscreen.setOnMouseClicked(event -> handleFullscreenMin());

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