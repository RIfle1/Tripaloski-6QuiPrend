package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.enums.Difficulty;
import project.enums.Variant;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static project.controllers.BoardController.boardScene;
import static project.controllers.ChooseCardsController.chooseCardsScene;
import static project.enums.Difficulty.getDifficultyList;
import static project.enums.Variant.getVariantList;
import static project.enums.Variant.setVariant;
import static project.functions.GeneralFunctions.readFileAsString;
import static project.functions.JavaFxFunctions.*;

public class MainMenuController implements Initializable {

    public final static int maxCards = 104;
    private final static int maxPlayersNumber = 10;
    @FXML
    private Text botNumberT;

    @FXML
    private ComboBox<String> difficultyCb;

    @FXML
    private Text errorT;

    @FXML
    private Slider npcNumberS;

    @FXML
    private Slider playerNumberS;

    @FXML
    private Text playerNumberT;

    @FXML
    private Slider pointNumberS;

    @FXML
    private Text pointNumberT;

    @FXML
    private Slider roundNumberS;

    @FXML
    private Text roundNumberT;

    @FXML
    private ComboBox<String> variantNumberCb;
    @FXML
    private Text variantInfoT;

    @FXML
    private Label variantInfoL;

    @FXML
    private Label difficultyInfoL;

    @FXML
    private Text difficultyInfoT;

    /**
     * Method to send to the main menu scene
     *
     * @param stage Stage
     */
    public static void mainMenuScene(Stage stage) {
        Image icon = new Image(returnImagePath("game/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("6 Qui Prend!");
        stage.setResizable(false);

        FXMLLoader mainMenuFXMLLoader = new FXMLLoader(returnFXMLURL("MainMenu.fxml"));
        sendToScene(stage, mainMenuFXMLLoader);
    }

    /**
     * Runs when the play button is clicked
     *
     * @param event MouseEvent
     */
    @FXML
    void playButtonOnClicked(MouseEvent event) {
        int playersNumber = (int) playerNumberS.getValue();
        int npcNumber = (int) npcNumberS.getValue();
        int roundNumber = (int) roundNumberS.getValue();
        int pointNumber = (int) pointNumberS.getValue();
        Variant variant = setVariant(variantNumberCb.getValue());
        Difficulty difficulty = Difficulty.setDifficulty(difficultyCb.getValue());

        if (!isAllowedCharacterAmount()) {
            errorT.setText("The sum of Players and NPCs must be between 2 and " + maxPlayersNumber);
            errorT.setVisible(true);
            return;
        }

        if (variant.equals(Variant.VARIANT_2) || variant.equals(Variant.VARIANT_3)) {
            chooseCardsScene(event, playersNumber, npcNumber, variant, roundNumber, pointNumber, difficulty);
        } else {
            boardScene(event, playersNumber, npcNumber, variant, roundNumber, pointNumber, difficulty);
        }

    }

    /**
     * Update the Npc Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void npcNumberOnChange(MouseEvent event) {
        updateSliderNumber(npcNumberS, botNumberT);
    }

    /**
     * Update the Player Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void playerNumberOnChange(MouseEvent event) {
        updateSliderNumber(playerNumberS, playerNumberT);
    }

    /**
     * Update the Point Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void pointNumberOnChange(MouseEvent event) {
        updateSliderNumber(pointNumberS, pointNumberT);
    }

    /**
     * Update the Round Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void roundNumberOnChange(MouseEvent event) {
        updateSliderNumber(roundNumberS, roundNumberT);
    }

    /**
     * Update the Variant Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void variantNumberOnAction(ActionEvent event) {
        updateVariantInfo();
    }

    /**
     * Update the Difficulty Number Text when the slider is moved
     *
     * @param event event
     */
    @FXML
    void difficultyOnAction(ActionEvent event) {
        updateDifficultyInfo();
    }

    /**
     * Update the Difficulty Combo Box
     */
    private void updateDifficultyInfo() {
        String difficultyString = difficultyCb.getValue();
        Difficulty difficulty = Difficulty.setDifficulty(difficultyString);

        difficultyInfoL.setText(difficultyString);
        difficultyInfoT.setText(readFileAsString(difficulty + ".txt"));
    }

    /**
     * Update the Variant Combo Box
     */
    private void updateVariantInfo() {
        String variantString = variantNumberCb.getValue();
        Variant variant = Variant.setVariant(variantString);

        variantInfoL.setText(variantString);
        variantInfoT.setText(readFileAsString(variant + ".txt"));
    }

    /**
     * Set's the respective slider text to the slider's value on key pressed
     *
     * @param slider Slider
     * @param text   Text
     */
    private void setSliderOnKeyPressed(Slider slider, Text text) {
        slider.setOnKeyPressed(keyEvent -> {
            updateSliderNumber(slider, text);
        });
    }

    /**
     * General method to update each slider's respective text
     *
     * @param slider slider
     * @param text   text
     */
    private void updateSliderNumber(Slider slider, Text text) {
        text.setText(String.valueOf((int) slider.getValue()));
    }

    /**
     * Sets a slider's default value
     *
     * @param slider slider
     * @param text text
     */
    private void setSliderDefault(Slider slider, Text text) {
        text.setText(String.valueOf((int) slider.getValue()));
    }

    /**
     * Initialize the Main Menu
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeVariantNumberCb();
        initializeDifficultyCb();
        initializeSliders();

        updateVariantInfo();
        updateDifficultyInfo();
    }

    /**
     * Initialize the sliders
     */
    private void initializeSliders() {
        HashMap<Slider, Text> slidersMap = new HashMap<>();

        slidersMap.put(npcNumberS, botNumberT);
        slidersMap.put(playerNumberS, playerNumberT);
        slidersMap.put(pointNumberS, pointNumberT);
        slidersMap.put(roundNumberS, roundNumberT);

        slidersMap.forEach((slider, text) -> {
            setSliderOnKeyPressed(slider, text);
            setSliderDefault(slider, text);
        });
    }

    /**
     * Check if the sum of players and npcs is between 2 and 10
     * @return true if the sum is between 2 and 10, false otherwise
     */
    private boolean isAllowedCharacterAmount() {
        int playersNumber = (int) playerNumberS.getValue();
        int npcNumber = (int) npcNumberS.getValue();

        boolean isMax = playersNumber + npcNumber <= maxPlayersNumber;
        boolean isMin = playersNumber + npcNumber >= 2;

        return isMax && isMin;
    }

    /**
     * Initializes the variant number choice box
     */
    private void initializeVariantNumberCb() {
        List<String> variantsList = getVariantList();

        variantNumberCb.getItems().addAll(variantsList);
        variantNumberCb.setValue(variantsList.get(0));
    }

    /**
     * Initializes the difficulty choice box
     */
    private void initializeDifficultyCb() {
        List<String> difficultyList = getDifficultyList();

        difficultyCb.getItems().addAll(difficultyList);
        difficultyCb.setValue(difficultyList.get(0));
    }

}
