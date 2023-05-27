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
import java.util.ArrayList;
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
     * Initialize the Main Menu
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

    @FXML
    void npcNumberOnChange(MouseEvent event) {
        updateSliderNumber(npcNumberS, botNumberT);
    }

    @FXML
    void playerNumberOnChange(MouseEvent event) {
        updateSliderNumber(playerNumberS, playerNumberT);
    }

    @FXML
    void pointNumberOnChange(MouseEvent event) {
        updateSliderNumber(pointNumberS, pointNumberT);
    }

    @FXML
    void roundNumberOnChange(MouseEvent event) {
        updateSliderNumber(roundNumberS, roundNumberT);
    }

    @FXML
    void variantNumberOnAction(ActionEvent event) {
        updateVariantInfo();
    }

    @FXML
    void difficultyOnAction(ActionEvent event) {
        updateDifficultyInfo();
    }

    private void updateDifficultyInfo() {
        String difficultyString = difficultyCb.getValue();
        Difficulty difficulty = Difficulty.setDifficulty(difficultyString);

        difficultyInfoL.setText(difficultyString);
        difficultyInfoT.setText(readFileAsString(difficulty + ".txt"));
    }

    private void updateVariantInfo() {
        String variantString = variantNumberCb.getValue();
        Variant variant = Variant.setVariant(variantString);

        variantInfoL.setText(variantString);
        variantInfoT.setText(readFileAsString(variant + ".txt"));
    }

    private void setSliderOnKeyPressed(Slider slider, Text text) {
        slider.setOnKeyPressed(keyEvent -> {
            updateSliderNumber(slider, text);
        });
    }

    private void updateSliderNumber(Slider slider, Text text) {
        text.setText(String.valueOf((int) slider.getValue()));
    }

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
        setVariantNumberCb();
        setDifficultyCb();
        initializeSliders();

        updateVariantInfo();
        updateDifficultyInfo();
    }

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


    private boolean isAllowedCharacterAmount() {
        int playersNumber = (int) playerNumberS.getValue();
        int npcNumber = (int) npcNumberS.getValue();

        boolean isMax = playersNumber + npcNumber <= maxPlayersNumber;
        boolean isMin = playersNumber + npcNumber >= 2;

        return isMax && isMin;
    }

    /**
     * Set the variant number choice box
     */
    private void setVariantNumberCb() {
        List<String> variantsList = getVariantList();

        variantNumberCb.getItems().addAll(variantsList);
        variantNumberCb.setValue(variantsList.get(0));
    }

    private void setDifficultyCb() {
        List<String> difficultyList = getDifficultyList();

        difficultyCb.getItems().addAll(difficultyList);
        difficultyCb.setValue(difficultyList.get(0));
    }

}
