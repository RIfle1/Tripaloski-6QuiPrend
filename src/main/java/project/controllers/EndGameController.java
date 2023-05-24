package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.classes.CharacterList;
import project.classes.Deck;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static project.functions.JavaFxFunctions.*;

public class EndGameController implements Initializable {

    private static Stage stage;
    @FXML
    private Text endGameTextT;

    @FXML
    private GridPane scoreBoardInfoGridPane;

    @FXML
    void onExitClicked(MouseEvent event) {
        stage.close();
    }

    @FXML
    void onPlayAgainClicked(MouseEvent event) {

    }


    public static void endGameScene(Stage stageParam) {
        stage = stageParam;
        Image icon = new Image(returnImagePath("icons/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Game Ended");
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("EndGame.fxml"));
        Scene scene = sendToScene(stage, fxmlLoader);
//        scene.setOnKeyPressed(e -> onKeyPressed(e, scene));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
