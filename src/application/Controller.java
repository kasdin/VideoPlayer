package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
    ScrollPane parentScrollPane;


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
        checkAddedVideosPlaylist();


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
        videoPath.clear();
        playlistGrid.getChildren().clear();


        thumbnailHolder.setVisible(false);
        buttonPlaylist.setUnderline(true);
        buttonVideos.setUnderline(false);
        //playlistField.setVisible(true);
        //showPlaylist();
        playlistGrid.setVisible(true);
        searchGrid.setVisible(false);

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
                    startVideo((Stage)parentScrollPane.getScene().getWindow(), videoP);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });

        }
        int rowIndex = 0;

        for (int j = 0; j < (videoInfo.size()/4); j++) {
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
        System.out.println(path);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mediaplayer.fxml"));
        MediaPlayerController mediaPlay = new MediaPlayerController(path);
        loader.setController(mediaPlay);
        Parent root = loader.load();
        stage1.setScene(new Scene(root, 1000,1000));
        //stage1.setFullScreen(true);
        //Parent root = FXMLLoader.load(getClass().getResource("mediaplayer.fxml"));
        //stage1.setTitle("Hello World");
        //stage1.setScene(new Scene(root, 1000, 1000));

    }

    @FXML public void checkAddedVideosPlaylist() {
        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = 3"));
        if(!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            buttonVideo1.setText("Remove from Playlist");
            buttonVideo1.setStyle("-fx-background-color: #ffbf00;");
            buttonVideo1.setOnAction(event -> deleteVideo1FromPlaylist());
        }

        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = 2"));
        if(!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            buttonVideo2.setText("Remove from Playlist");
            buttonVideo2.setStyle("-fx-background-color: #ffbf00;");
            buttonVideo2.setOnAction(event -> deleteVideo2FromPlaylist());
        }


        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = 5"));
        if(!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            buttonVideo3.setText("Remove from Playlist");
            buttonVideo3.setStyle("-fx-background-color: #ffbf00;");
            buttonVideo3.setOnAction(event -> deleteVideo3FromPlaylist());
        }

        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = 4"));
        if(!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            buttonVideo4.setText("Remove from Playlist");
            buttonVideo4.setStyle("-fx-background-color: #ffbf00;");
            buttonVideo4.setOnAction(event -> deleteVideo4FromPlaylist());
        }


    }

    @FXML public void addVideo1ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES('3','1')");
        buttonVideo1.setText("Remove From Playlist");
        buttonVideo1.setStyle("-fx-background-color: #ffbf00;");
        buttonVideo1.setOnAction(event -> deleteVideo1FromPlaylist());

    }

    @FXML public void deleteVideo1FromPlaylist() {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where fldVideoID = 3");
        buttonVideo1.setText("Add To Playlist");
        buttonVideo1.setStyle("-fx-background-color: #B20000;");
        buttonVideo1.setOnAction(event -> addVideo1ToPlaylist());

    }

    @FXML public void addVideo2ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES('2' ,'1')");
        buttonVideo2.setText("Remove From Playlist");
        buttonVideo2.setStyle("-fx-background-color: #ffbf00;");
        buttonVideo2.setOnAction(event -> deleteVideo2FromPlaylist());

    }

    @FXML public void deleteVideo2FromPlaylist() {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where (fldVideoID) = '2'");
        buttonVideo2.setText("Add To Playlist");
        buttonVideo2.setStyle("-fx-background-color: #B20000;");
        buttonVideo2.setOnAction(event -> addVideo2ToPlaylist());

    }

    @FXML public void addVideo3ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 5 +","+ 1 +")");
        buttonVideo3.setText("Remove From Playlist");
        buttonVideo3.setStyle("-fx-background-color: #ffbf00;");
        buttonVideo3.setOnAction(event -> deleteVideo3FromPlaylist());
    }

    @FXML public void deleteVideo3FromPlaylist() {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where (fldVideoID) = '5'");
        buttonVideo3.setText("Add To Playlist");
        buttonVideo3.setStyle("-fx-background-color: #B20000;");
        buttonVideo3.setOnAction(event -> addVideo3ToPlaylist());

    }

    @FXML public void addVideo4ToPlaylist() {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES("+ 4 +","+ 1 +")");
        buttonVideo4.setText("Remove From Playlist");
        buttonVideo4.setStyle("-fx-background-color: #ffbf00;");
        buttonVideo4.setOnAction(event -> deleteVideo4FromPlaylist());
    }

    @FXML public void deleteVideo4FromPlaylist() {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where (fldVideoID) = '4'");
        buttonVideo4.setText("Add To Playlist");
        buttonVideo4.setStyle("-fx-background-color: #B20000;");
        buttonVideo4.setOnAction(event -> addVideo4ToPlaylist());

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
        ArrayList<String> imagePath = new ArrayList<>();
        ArrayList<String> videoPathSearch = new ArrayList<>();
        searchGrid.getChildren().clear();


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

        DB.selectSQL("SELECT fldMediaPath FROM tblVideo WHERE MATCH(fldVideoName) AGAINST('"+userSearchQuery+"');");
        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                videoPathSearch.add(data);

            }
        } while(true);

        for (int i = 0; i <imagePath.size() ; i++) {
            //System.out.println(imagePath.get(i));
            String path = imagePath.get(i);
            String videoPath1 = videoPathSearch.get(i);
            System.out.println(videoPath1);
            ImageView thumbnail = new ImageView();
            thumbnail.setFitWidth(201);
            thumbnail.setFitHeight(280);
            thumbnail.setPreserveRatio(true);
            thumbnail.setImage(new Image(path));
            searchGrid.addRow(i+1);
            searchGrid.add(thumbnail, 0, i);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            searchGrid.setVgap(40);
            System.out.println(videoPath1);
            thumbnail.setOnMouseClicked(event -> {
                try {
                    startVideo((Stage)parentScrollPane.getScene().getWindow(), videoPath1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });

        }

    }






}