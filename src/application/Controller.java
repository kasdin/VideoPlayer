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

    @FXML private Button buttonPlaylist;

    @FXML private Button buttonVideos;

    @FXML private GridPane thumbnailHolder;

    @FXML private TextArea playlistField;

    @FXML private GridPane playlistGrid;

    @FXML private TextField searchField;

    @FXML private GridPane searchGrid;

    public static ArrayList<String> videoPath = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonActionListeners();
        checkIfVideosAreAlreadyAddedToPlaylist();
        setSearchFieldOnKeyPressed();
    }

    private void setButtonActionListeners() {
        buttonVideo1.setOnAction(event -> addVideoToPlaylist(buttonVideo1, 3));
        buttonVideo2.setOnAction(event -> addVideoToPlaylist(buttonVideo2, 2));
        buttonVideo3.setOnAction(event -> addVideoToPlaylist(buttonVideo3, 5));
        buttonVideo4.setOnAction(event -> addVideoToPlaylist(buttonVideo4, 4));
    }

    private void checkIfVideosAreAlreadyAddedToPlaylist() {
        checkAddedVideosPlaylist(buttonVideo1, 3);
        checkAddedVideosPlaylist(buttonVideo2, 2);
        checkAddedVideosPlaylist(buttonVideo3, 5);
        checkAddedVideosPlaylist(buttonVideo4, 4);
    }

    private void setSearchFieldOnKeyPressed() {
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchVideos();
            }
        });
    }

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
        stage1.setScene(new Scene(root, 1000,1300));

    }

    @FXML private void checkAddedVideosPlaylist(Button button, int videoID) {
        DB.selectSQL(("SELECT fldVideoID FROM tblVideoPlaylist WHERE fldVideoID = " + videoID + ""));
        if (!DB.getData().equals("|ND|")) {
            DB.getData().trim();
            button.setText("Remove from Playlist");
            button.setStyle("-fx-background-color: #ffbf00;");
            button.setOnAction(event -> deleteVideoFromPlaylist(button, videoID));
        }
    }

    @FXML private void addVideoToPlaylist(Button button, int videoID) {
        DB.insertSQL("INSERT INTO tblVideoPlaylist (fldVideoID, fldPlaylistID) VALUES('"+videoID +"','1')");
        button.setText("Remove From Playlist");
        button.setStyle("-fx-background-color: #ffbf00;");
        button.setOnAction(event -> deleteVideoFromPlaylist(button, videoID));
    }

    @FXML private void deleteVideoFromPlaylist(Button button, int videoID) {
        DB.deleteSQL("DELETE FROM tblVideoPlaylist Where fldVideoID = "+ videoID + "");
        button.setText("Add To Playlist");
        button.setStyle("-fx-background-color: #B20000;");
        button.setOnAction(event -> addVideoToPlaylist(button, videoID));

    }

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

    public ImageView createThumbnail(String imagePath, String videoPath) {
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(201);
        thumbnail.setFitHeight(280);
        thumbnail.setPreserveRatio(true);
        thumbnail.setImage(new Image(imagePath));
        thumbnail.setOnMouseClicked(event -> {
            try {
                startVideo((Stage)parentScrollPane.getScene().getWindow(), videoPath);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        return thumbnail;
    }

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