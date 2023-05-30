package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.abstractClasses.AbstractCharacter;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static project.controllers.BoardController.characters;
import static project.controllers.MainMenuController.mainMenuScene;
import static project.functions.JavaFxFunctions.*;

public class EndGameController implements Initializable {

    private static Stage stage;
    private static Stage mainGameStage;
    private static GridPane scoreBoardGridPane;
    @FXML
    private Text endGameTextT;
    @FXML
    private GridPane scoreBoardInfoGridPane;

    /**
     * This method is used to send the user to the EndGame scene
     *
     * @param mainGameStageParam The stage of the main game
     */
    public static void endGameScene(Stage mainGameStageParam) {
        mainGameStage = mainGameStageParam;
        stage = new Stage();
        Image icon = new Image(returnImagePath("game/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Game Ended");
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("EndGame.fxml"));
        Scene scene = sendToScene(stage, fxmlLoader);
        scene.setOnKeyPressed(event -> onExitKeyPressed(event, EndGameController::exit));
    }

    /**
     * Exit method
     */
    private static void exit() {
        if (checkConfirmationPopUp(stage, "Are you want to exit?")) {
            mainGameStage.close();
            stage.close();
        }
    }

    /**
     * This method is used to initialize the scene
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortCharacterListByPoints();
        displayWinners();

        initializeScoreBoard();
        displayScoreBoard(scoreBoardInfoGridPane, scoreBoardGridPane, characters, 0, 0, 5);
    }

    /**
     * On Exit Button Clicked
     *
     * @param event event
     */
    @FXML
    private void onExitClicked(MouseEvent event) {
        exit();
    }

    /**
     * On Play Again Button Clicked
     *
     * @param event event
     */
    @FXML
    private void onPlayAgainClicked(MouseEvent event) {
        if (checkConfirmationPopUp(stage, "Are you sure you want to return to the Game Menu?")) {
            stage.close();
            mainMenuScene(mainGameStage);
        }
    }

    /**
     * Method to sort the character list by the points they have in descending order
     */
    private void sortCharacterListByPoints() {
        characters.getCharactersList().sort(Comparator.comparingInt(character -> -character.getPoints()));
    }

    /**
     * Method to return the winners
     *
     * @return List of winners
     */
    private List<AbstractCharacter> returnWinners() {
        int winningPoints = characters.getCharactersList().get(0).getPoints();
        return characters.getCharactersList().stream().filter(character -> character.getPoints() == winningPoints).toList();
    }

    /**
     * Method to display the winners
     */
    private void displayWinners() {
        List<AbstractCharacter> winners = returnWinners();
        if (winners.size() == 1) {
            endGameTextT.setText("The winner is " + winners.get(0).getCharacterName() + "!");
        } else {
            StringBuilder winnersString = new StringBuilder("The winners are ");
            for (int i = 0; i < winners.size(); i++) {
                if (i == winners.size() - 1) {
                    winnersString.append("and ").append(winners.get(i).getCharacterName()).append("!");
                } else {
                    winnersString.append(winners.get(i).getCharacterName()).append(", ");
                }
            }
            endGameTextT.setText(winnersString.toString());
        }
    }

    /**
     * Method to initialize the score board
     */
    private void initializeScoreBoard() {
        scoreBoardGridPane = new GridPane();

        scoreBoardGridPane.setAlignment(Pos.CENTER);
        scoreBoardGridPane.setVgap(10);
        scoreBoardGridPane.setHgap(20);
    }

}
