package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML ScrollPane parentScrollPane;

    @FXML Button video1;

    @FXML private Button buttonVideo1;

    @FXML private Button buttonVideo2;

    @FXML private Button buttonVideo3;

    @FXML private Button buttonVideo4;

    @FXML private ImageView thumbnail1;

    @FXML private ImageView thumbnail2;

    @FXML private ImageView thumbnail3;

    @FXML private ImageView thumbnail4;

    @FXML private Button buttonPlaylist;

    @FXML private Button buttonVideos;

    @FXML private GridPane thumbnailHolder;

    @FXML private TextArea playlistField;

    @FXML private GridPane playlistGrid;

    @FXML private TextField searchField;

    @FXML private GridPane searchGrid;

    public static ArrayList<String> videoPath = new ArrayList<>(); //This Arraylist is used to hold the videopath

    /**
     * This method runs when the application is started.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonActionListeners();
        setThumbnailActionListeners();
        checkIfVideosAreAlreadyAddedToPlaylist();
        setSearchFieldOnKeyPressed();
        parentScrollPane.setFitToWidth(true);
        videoPath = fillPlaylistArray("fldMediaPath");
    }

    /**
     * This method set action listeners on the thumbnails(ImageViews) shown when you click the button "All Videos".
     */
    private void setThumbnailActionListeners() {

        thumbnail1.setOnMouseClicked(event -> {
            try {
                startVideo("src/presentation/ressources/videofiles/onceUpon.mp4 ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thumbnail2.setOnMouseClicked(event -> {
            try {
                startVideo( "src/presentation/ressources/videofiles/joker.mp4 ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thumbnail3.setOnMouseClicked(event -> {
            try {
                startVideo("src/presentation/ressources/videofiles/thewolf.mp4 ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thumbnail4.setOnMouseClicked(event -> {
            try {
                startVideo("src/presentation/ressources/videofiles/irishman.mp4 ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method set button actions listeners for the buttons "Add Video to Playlist".
     */

    private void setButtonActionListeners() {
        buttonVideo1.setOnAction(event -> addVideoToPlaylist(buttonVideo1, 3));
        buttonVideo2.setOnAction(event -> addVideoToPlaylist(buttonVideo2, 2));
        buttonVideo3.setOnAction(event -> addVideoToPlaylist(buttonVideo3, 5));
        buttonVideo4.setOnAction(event -> addVideoToPlaylist(buttonVideo4, 1));
    }

    /**
     * This method checks if a video is already added to your playlist.
     */

    private void checkIfVideosAreAlreadyAddedToPlaylist() {
        checkAddedVideosPlaylist(buttonVideo1, 3);
        checkAddedVideosPlaylist(buttonVideo2, 2);
        checkAddedVideosPlaylist(buttonVideo3, 5);
        checkAddedVideosPlaylist(buttonVideo4, 1);
    }

    /**
     * This method sets action listener for the searchField and run searchVideo() if the key ENTER is pressed.
     */
    private void setSearchFieldOnKeyPressed() {
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchVideos();
            }
        });
    }

    /**
     * This method shows "Your Playlist". It creates thumbnails and set action listeners, it fetches data
     * from the database.
     */
    @FXML private void showYourPlaylist() {
        videoPath.clear();
        playlistGrid.getChildren().clear();

        thumbnailHolder.setVisible(false);
        buttonPlaylist.setUnderline(true);
        buttonVideos.setUnderline(false);
        playlistGrid.setVisible(true);
        searchGrid.setVisible(false);

        videoPath = fillPlaylistArray("fldMediaPath");
        ArrayList<String> imagePath = fillPlaylistArray("fldThumbnailPath");
        ArrayList<String> videoInfo = fillPlaylistArray("fldVideoName, fldVideoDuration, fldReleaseYear, fldVideoInfo");

        for (int i = 0; i <imagePath.size() ; i++) {
            String path = imagePath.get(i);
            String videoP = videoPath.get(i);
            ImageView thumbnail = createThumbnail(path, videoP);
            playlistGrid.addRow(i+1);
            playlistGrid.add(thumbnail, 0, i);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            playlistGrid.setVgap(40);

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
            info.setText("Title: " + videoInfo.get(rowIndex) + "\n" +
                    "Duration: " + videoInfo.get(rowIndex+1) + "\n" +
                    "Release Year: " + videoInfo.get(rowIndex+2) +"\n" +
                    "Info: " + videoInfo.get(rowIndex+3));
            rowIndex = rowIndex + 4;
        }
    }

    /**
     * This method shows all available videos when the button "All videos" are pressed.
     */

    @FXML private void showAllVideos() {
        thumbnailHolder.setVisible(true);
        buttonPlaylist.setUnderline(false);
        playlistField.setVisible(false);
        playlistGrid.setVisible(false);
        searchGrid.setVisible(false);
        buttonVideos.setUnderline(true);
    }


    /**
     * This method creates a new stage
     * @param path
     * @throws Exception
     */
    private void startVideo(String path) throws Exception {
        Stage stage = (Stage) parentScrollPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mediaplayer.fxml"));
        MediaPlayerController mediaPlay = new MediaPlayerController(path);
        loader.setController(mediaPlay);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1440,1000));
        stage.setFullScreen(true);

    }

    /**
     * This method check the database if a video is already added to "Your Playlist" and changes the buttons.
     * @param button
     * @param videoID
     */
    @FXML private void checkAddedVideosPlaylist(Button button, int videoID) {
        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = " + videoID + ""));
        if (!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            button.setText("Remove from Playlist");
            button.setStyle("-fx-background-color: #ffbf00;");
            button.setOnAction(event -> deleteVideoFromPlaylist(button, videoID));
        }
    }

    /**
     * this method add a video to "Your Playlist" and insert it into the database.
     * @param button
     * @param videoID
     */
    @FXML private void addVideoToPlaylist(Button button, int videoID) {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES('"+videoID +"','1')");
        button.setText("Remove From Playlist");
        button.setStyle("-fx-background-color: #ffbf00;");
        button.setOnAction(event -> deleteVideoFromPlaylist(button, videoID));
    }

    /**
     * This method deletes a video from "Your Playlist" and deletes it from the database.
     * @param button
     * @param videoID
     */
    @FXML private void deleteVideoFromPlaylist(Button button, int videoID) {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where fldVideoID = "+ videoID + "");
        button.setText("Add To Playlist");
        button.setStyle("-fx-background-color: #B20000;");
        button.setOnAction(event -> addVideoToPlaylist(button, videoID));

    }

    /**
     * This method is the search function of the application.
     */

    @FXML private void searchVideos() {
        videoPath.clear();
        searchGrid.getChildren().clear();
        thumbnailHolder.setVisible(false);
        playlistGrid.setVisible(false);
        searchGrid.setVisible(true);

        String userSearchQuery = searchField.getText();
        videoPath = fillPlaylistArray("fldMediaPath");
        ArrayList<String> imagePath = fillSearchArray("fldThumbnailPath", userSearchQuery);
        ArrayList<String> videoPathSearch = fillSearchArray("fldMediaPath", userSearchQuery);


        for (int i = 0; i <imagePath.size() ; i++) {
            ImageView thumbnail = createThumbnail(imagePath.get(i), videoPathSearch.get(i));
            searchGrid.addRow(i+1);
            searchGrid.add(thumbnail, 0, i);
            GridPane.setHalignment(thumbnail, HPos.CENTER);
            searchGrid.setVgap(40);
        }

    }

    /**
     * This method creates a thumbnail(Imageview), sets the properties and adds a action listener.
     * @param imagePath
     * @param videoPath
     * @return
     */

    public ImageView createThumbnail(String imagePath, String videoPath) {
        System.out.println(imagePath);
        System.out.println(videoPath);
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(201);
        thumbnail.setFitHeight(280);
        thumbnail.setPreserveRatio(true);
        thumbnail.setImage(new Image(imagePath));
        thumbnail.setOnMouseClicked(event -> {
            try {
                startVideo(videoPath);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        return thumbnail;
    }

    /**
     * This method is used to fill an array with String data fetched from the database.
     * @param sqlQuery
     * @param userSearchQuery
     * @return
     */
    private ArrayList<String> fillSearchArray(String sqlQuery, String userSearchQuery) {
        ArrayList<String> arrayList = new ArrayList<>();
        DB.selectSQL("SELECT " + sqlQuery + " FROM tblVideo WHERE MATCH(fldVideoName) AGAINST('" + userSearchQuery + "');");
        do {
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                arrayList.add(data);
            }

        } while(true);

        return arrayList;
    }

    /**
     * This method returns an array with playlist data from the database.
     * @param sqlQuery
     * @return
     */

    public static ArrayList<String> fillPlaylistArray(String sqlQuery) {
        ArrayList<String> arrayList = new ArrayList<>();
        DB.selectSQL("select "+ sqlQuery + " from tblVideo\n" +
                "join tblVideoPlaylist on tblVideo.fldVideoID = tblVideoPlaylist.fldVideoID\n" +
                "join tblPlaylist on tblVideoPlaylist.fldPlaylistID = tblPlaylist.fldPlaylistID;");
        do {
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                arrayList.add(data);
            }

        } while(true);

        return arrayList;
    }

}