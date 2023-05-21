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
    private final static int minPlayersNumber = 2;
    private final static int maxNpcNumber = 8;
    private final static int minNpcNumber = 0;
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
     *
     * @param stage Stage
     */
    public static void mainMenuScene(Stage stage) {
        Image icon = new Image(returnImagePath("icons/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("6 Qui Prend!");
        stage.setResizable(false);

        FXMLLoader mainMenuFXMLLoader = new FXMLLoader(returnFXMLURL("MainMenu.fxml"));
        sendToScene(stage, mainMenuFXMLLoader);
    }

    /**
     * Return a list of numbers from minNumber to maxNumber
     *
     * @param minNumber Minimum number
     * @param maxNumber Maximum number
     * @return List of numbers from minNumber to maxNumber
     */
    @NotNull
    private static List<String> returnCbList(int minNumber, int maxNumber) {
        List<String> numberCbList = new ArrayList<>();

        if (maxNumber < minNumber) {
            maxNumber = minNumber;
        }


        for (int i = minNumber; i <= maxNumber; i++) {
            numberCbList.add(String.valueOf(i));
        }


        return numberCbList;
    }

    /**
     * Runs when the play button is clicked
     *
     * @param event MouseEvent
     */
    @FXML
    void playButtonOnClicked(MouseEvent event) {
        int playersNumber = Integer.parseInt(playersNumberCb.getValue());
        int npcNumber = Integer.parseInt(npcNumberCb.getValue());
        int variantNumber = returnVariantsList().indexOf(variantNumberCb.getValue());

        int roundNumber;
        int startingPoints;

        if (checkPositiveInt(roundNumberTf.getText())) {
            roundNumber = Integer.parseInt(roundNumberTf.getText());
        } else {
            errorT.setText("Round Number must be a positive integer");
            errorT.setVisible(true);
            return;
        }

        if (checkPositiveInt(startingPointsTf.getText())) {
            startingPoints = Integer.parseInt(startingPointsTf.getText());
        } else {
            errorT.setText("Starting Points must be a positive integer");
            errorT.setVisible(true);
            return;
        }

        if (!isAllowedCharacterAmount()) {
            errorT.setText("The sum of Players and NPCs must be less than or equals to " + maxPlayersNumber);
            errorT.setVisible(true);
            return;
        }

        boardScene(event, playersNumber, npcNumber, variantNumber, roundNumber, startingPoints, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Initialize the Main Menu
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCharacterNumberCb(playersNumberCb, minPlayersNumber, maxPlayersNumber);
        setCharacterNumberCb(npcNumberCb, minNpcNumber, maxNpcNumber);

        setVariantNumberCb();
        setGameParams();
    }

    private void setCharacterNumberCb(ChoiceBox<String> characterNumberCb, int min, int max) {
        characterNumberCb.getItems().clear();
        List<String> characterNumberCbList = returnCbList(min, max);

        characterNumberCb.getItems().addAll(characterNumberCbList);
        characterNumberCb.setValue(characterNumberCbList.get(0));
    }

    private boolean isAllowedCharacterAmount() {
        int playersNumber = Integer.parseInt(playersNumberCb.getValue());
        int npcNumber = Integer.parseInt(npcNumberCb.getValue());

        return playersNumber + npcNumber <= maxPlayersNumber;
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
     *
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
