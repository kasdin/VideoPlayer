package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public static ArrayList<String> videoPath = new ArrayList<>();

    @FXML
    Button video1;

    @FXML private Button buttonVideo;

    @FXML private Button buttonVideo1;

    @FXML private Button buttonVideo2;

    @FXML private Button buttonVideo3;

    @FXML private Button buttonVideo4;

    @FXML private Button buttonPlaylist;

    @FXML private Button buttonVideos;

    @FXML private GridPane thumbnailHolder;

    @FXML private TextArea playlistField;

    @FXML private GridPane playlistGrid;

    @FXML private ImageView playlist1Thumbnail;

    @FXML private ImageView playlist2Thumbnail;

    @FXML private TextField searchField;

    @FXML private GridPane searchGrid;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*video1.setOnAction(event -> {
            try {
                startVideo((Stage)video1.getScene().getWindow());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });*/

        searchField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
               searchVideos();
            }
        });
    }

    @FXML private void showYourPlaylist() {
        ArrayList<String> imagePath = new ArrayList<>();
        ArrayList<String> videoInfo = new ArrayList<>();


        thumbnailHolder.setVisible(false);
        buttonPlaylist.setUnderline(true);
        buttonVideos.setUnderline(false);
        //playlistField.setVisible(true);
        //showPlaylist();
        playlistGrid.setVisible(true);
        searchGrid.setVisible(false);

        DB.selectSQL("Select fldThumbnailPath from tblVideo;");
        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                imagePath.add(data);

            }
        } while(true);

        DB.selectSQL("select fldMediaPath from tblVideo\n" +
                "join tblVideoPlaylist on tblVideo.fldVideoID = tblVideoPlaylist.fldVideoID\n" +
                "join tblPlaylist on tblVideoPlaylist.fldPlaylistID = tblPlaylist.fldPlaylistID;");
        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                videoPath.add(data);

            }
        } while(true);

        DB.selectSQL("select fldVideoName, fldVideoDuration, fldReleaseYear, fldVideoInfo from tblVideo\n" +
                "join tblVideoPlaylist on tblVideo.fldVideoID = tblVideoPlaylist.fldVideoID\n" +
                "join tblPlaylist on tblVideoPlaylist.fldPlaylistID = tblPlaylist.fldPlaylistID;");
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                videoInfo.add(data);

            }
        } while(true);

        for (int i = 0; i <imagePath.size() ; i++) {
            //System.out.println(imagePath.get(i));
            String path = imagePath.get(i);
            String videoP = videoPath.get(i);
            //System.out.println(videoPath.get(i));
            ImageView thumbnail = new ImageView();
            String uniqueID = "Video" + i;
            //System.out.println(uniqueID);
            thumbnail.setId(uniqueID);
            thumbnail.setFitWidth(201);
            thumbnail.setFitHeight(280);
            thumbnail.setPreserveRatio(true);
            thumbnail.setImage(new Image(path));
            playlistGrid.addRow(i+1);
            playlistGrid.add(thumbnail, 0, i);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            playlistGrid.setVgap(40);
            thumbnail.setOnMouseClicked(event -> {
                try {
                    startVideo((Stage)thumbnail.getScene().getWindow(), videoP);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });

        }
        int rowIndex = 0;

        for (int j = 0; j < 3; j++) {
            TextArea info = new TextArea();
            info.setStyle("-fx-font-size: 16;");
            info.setMaxHeight(250.0);
            info.setMaxWidth(300.0);
            info.setWrapText(true);
            playlistGrid.add(info, 1, j);
            GridPane.setHalignment(info, HPos.CENTER);
            //System.out.println(videoInfo.get(j));
            info.setText("Title: " + videoInfo.get(rowIndex) + "\n" +
                    "Duration: " + videoInfo.get(rowIndex+1) + "\n" +
                    "Release Year: " + videoInfo.get(rowIndex+2) +"\n" +
                    "Info: " + videoInfo.get(rowIndex+3));
            rowIndex = rowIndex + 4;

        }


    }

    @FXML private void showAllVideos() {
        thumbnailHolder.setVisible(true);
        buttonPlaylist.setUnderline(false);
        playlistField.setVisible(false);
        playlistGrid.setVisible(false);
        searchGrid.setVisible(false);
        buttonVideos.setUnderline(true);


    }

    private void startVideo(Stage stage1, String path) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mediaplayer.fxml"));
        MediaPlayerController mediaPlay = new MediaPlayerController(path);
        loader.setController(mediaPlay);
        Parent root = loader.load();
        stage1.setScene(new Scene(root, 1000,1000));
        //Parent root = FXMLLoader.load(getClass().getResource("mediaplayer.fxml"));
        //stage1.setTitle("Hello World");
        //stage1.setScene(new Scene(root, 1000, 1000));

    }

    @FXML public void addVideo1ToPlaylist() {
        //B.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 1 +","+ 1 +")");
        DB.selectSQL("Select fldThumbnailPath from tblVideo where fldVideoID ='3';");
        String path = DB.getData();
        playlist1Thumbnail.setImage(new Image(path));



    }

    @FXML public void addVideo2ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 2 +","+ 1 +")");

    }

    @FXML public void addVideo3ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 3 +","+ 1 +")");

    }

    @FXML public void addVideo4ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 4 +","+ 1 +")");
    }

    @FXML public void showPlaylist() {
        /*
        DB.selectSQL("select fldVideoName, fldVideoDuration, fldReleaseYear from tblVideo\n" +
                "join tblVideoPlaylist on tblVideo.fldVideoID = tblVideoPlaylist.fldVideoID\n" +
                "join tblPlaylist on tblVideoPlaylist.fldPlaylistID = tblPlaylist.fldPlaylistID;");

        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                playlistField.appendText(data);

            }
        } while(true);
*/
        //DB.selectSQL("Select fldThumbnailPath from tblVideo where fldVideoID ='1'");
       //String path = DB.getDisplayData();
       //Image thumbnail = new Image("/Users/kasperdinsen/Downloads/VideoPlayer/src/presentation/ressources/images/featured-movie.jpg");
        //Image thumbnail = new Image(getClass().getResourceAsStream("featured-movie.jpg"));
        playlist1Thumbnail.setImage(new Image("file:src/presentation/ressources/images/featured-movie.jpg"));


    }

    @FXML public void searchVideos() {
        searchGrid.getChildren().clear();
        ArrayList<String> imagePath = new ArrayList<>();

        thumbnailHolder.setVisible(false);
        playlistGrid.setVisible(false);
        searchGrid.setVisible(true);

        String userSearchQuery = searchField.getText();
        DB.selectSQL("SELECT fldThumbnailPath FROM tblVideo WHERE MATCH(fldVideoName) AGAINST('"+userSearchQuery+"');");
        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                //System.out.println(data);
                imagePath.add(data);

            }
        } while(true);

        for (int i = 0; i <imagePath.size() ; i++) {
            //System.out.println(imagePath.get(i));
            String path = imagePath.get(i);
            ImageView thumbnail = new ImageView();
            thumbnail.setFitWidth(201);
            thumbnail.setFitHeight(280);
            thumbnail.setPreserveRatio(true);
            thumbnail.setImage(new Image(path));
            searchGrid.addRow(i+1);
            searchGrid.add(thumbnail, 0, i);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            searchGrid.setVgap(40);
           /* thumbnail.setOnMouseClicked(event -> {
                try {
                    startVideo((Stage)video1.getScene().getWindow());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });*/

        }

    }






}