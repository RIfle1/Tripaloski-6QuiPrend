package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static project.controllers.BoardController.boardScene;
import static project.functions.GeneralFunctions.checkPositiveInt;
import static project.functions.JavaFxFunctions.*;

public class MainMenuController implements Initializable {

    private final static int maxPlayersNumber = 10;
    private final static int minPlayersNumber = 1;
    private final static int maxNpcNumber = 10;
    private final static int minNpcNumber = 1;
    @FXML
    private ChoiceBox<String> npcNumberCb;
    @FXML
    private ChoiceBox<String> playersNumberCb;
    @FXML
    private ChoiceBox<String> variantNumberCb;
    @FXML
    private TextField roundNumberTf;
    @FXML
    private TextField startingPointsTf;
    @FXML
    private Text errorT;

    /**
     * Initialize the Main Menu
     * @param stage Stage
     */
    public static void mainMenuScene(Stage stage) {
        Image icon = new Image(returnImagePath("logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("6 Qui Prend!");
        stage.setResizable(false);

        FXMLLoader mainMenuFXMLLoader = new FXMLLoader(returnFXMLURL("MainMenu.fxml"));
        sendToScene(stage, mainMenuFXMLLoader);
    }

    /**
     * Return a list of numbers from minNumber to maxNumber
     * @param minNumber Minimum number
     * @param maxNumber Maximum number
     * @return List of numbers from minNumber to maxNumber
     */
    @NotNull
    private static List<String> returnCbList(int minNumber, int maxNumber) {
        List<String> numberCbList = new ArrayList<>();

        for (int i = minNumber; i <= maxNumber; i++) {
            numberCbList.add(String.valueOf(i));
        }
        return numberCbList;
    }

    /**
     * Runs when the play button is clicked
     * @param event MouseEvent
     */
    @FXML
    void playButtonOnClicked(MouseEvent event) {
        int playersNumber = Integer.parseInt(playersNumberCb.getValue());
        int npcNumber = Integer.parseInt(npcNumberCb.getValue());
        int variantNumber = returnVariantsList().indexOf(variantNumberCb.getValue());

        int roundNumber;
        int startingPoints;

        if(checkPositiveInt(roundNumberTf.getText())) {
            roundNumber = Integer.parseInt(roundNumberTf.getText());
        } else {
            errorT.setText("Round Number must be a positive integer");
            errorT.setVisible(true);
            return;
        }

        if(checkPositiveInt(startingPointsTf.getText())) {
            startingPoints = Integer.parseInt(startingPointsTf.getText());
        } else {
            errorT.setText("Starting Points must be a positive integer");
            errorT.setVisible(true);
            return;
        }

        boardScene(event, playersNumber, npcNumber, variantNumber, roundNumber, startingPoints, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Initialize the Main Menu
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlayersNumberCb();
        setNpcNumberCb();
        setVariantNumberCb();
        setGameParams();
    }

    /**
     * Set the players number choice box
     */
    private void setPlayersNumberCb() {
        List<String> playersNumberCbList = returnCbList(minPlayersNumber, maxPlayersNumber);

        playersNumberCb.getItems().addAll(playersNumberCbList);
        playersNumberCb.setValue(playersNumberCbList.get(0));
    }

    /**
     * Set the npc number choice box
     */
    private void setNpcNumberCb() {
        List<String> npcNumberCbList = returnCbList(minNpcNumber, maxNpcNumber);

        npcNumberCb.getItems().addAll(npcNumberCbList);
        npcNumberCb.setValue(npcNumberCbList.get(0));
    }

    /**
     * Set the variant number choice box
     */
    private void setVariantNumberCb() {
        List<String> variantsList = returnVariantsList();

        variantNumberCb.getItems().addAll(variantsList);
        variantNumberCb.setValue(variantsList.get(0));
    }

    /**
     * Return a list of variants
     * @return List of variants
     */
    private List<String> returnVariantsList() {
        List<String> variantNumberCbList = new ArrayList<>();
        variantNumberCbList.add("No Variant");
        variantNumberCbList.add("Variant 1");
        variantNumberCbList.add("Variant 2");
        variantNumberCbList.add("Variant 1 And 2");

        return variantNumberCbList;
    }

    /**
     * Sets default game params
     */
    private void setGameParams() {
        startingPointsTf.setText("66");
        roundNumberTf.setText("10");
    }
}
