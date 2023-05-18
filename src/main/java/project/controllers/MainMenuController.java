package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static project.controllers.BoardController.boardScene;
import static project.functions.JavaFxFunctions.*;

public class MainMenuController implements Initializable {

    @FXML
    private ChoiceBox<String> npcNumberCb;

    @FXML
    private ChoiceBox<String> playersNumberCb;

    @FXML
    private ChoiceBox<String> variantNumberCb;

    private final static int maxPlayersNumber = 10;
    private final static int minPlayersNumber = 1;
    private final static int maxNpcNumber = 10;
    private final static int minNpcNumber = 1;

    @FXML
    void playButtonOnClicked(MouseEvent event) {
        int playersNumber = Integer.parseInt(playersNumberCb.getValue());
        int npcNumber = Integer.parseInt(npcNumberCb.getValue());
        int variantNumber = returnVariantsList().indexOf(variantNumberCb.getValue());

        boardScene(event, playersNumber, npcNumber, variantNumber, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlayersNumberCb();
        setNpcNumberCb();
        setVariantNumberCb();
    }

    public static void mainMenuScene(Stage stage) {
        Image icon = new Image(returnImagePath("logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("6 Qui Prend!");
        stage.setResizable(false);

        FXMLLoader mainMenuFXMLLoader = new FXMLLoader(returnFXMLURL("MainMenu.fxml"));
        sendToScene(stage, mainMenuFXMLLoader);
    }

    private void setPlayersNumberCb() {
        List<String> playersNumberCbList = returnCbList(minPlayersNumber, maxPlayersNumber);

        playersNumberCb.getItems().addAll(playersNumberCbList);
        playersNumberCb.setValue(playersNumberCbList.get(0));
    }

    private void setNpcNumberCb() {
        List<String> npcNumberCbList = returnCbList(minNpcNumber, maxNpcNumber);

        npcNumberCb.getItems().addAll(npcNumberCbList);
        npcNumberCb.setValue(npcNumberCbList.get(0));
    }

    @NotNull
    private static List<String> returnCbList(int minNumber, int maxNumber) {
        List<String> numberCbList = new ArrayList<>();

        for (int i = minNumber; i <= maxNumber; i++) {
            numberCbList.add(String.valueOf(i));
        }
        return numberCbList;
    }

    private void setVariantNumberCb() {
        List<String> variantsList = returnVariantsList();

        variantNumberCb.getItems().addAll(variantsList);
        variantNumberCb.setValue(variantsList.get(0));
    }

    private List<String> returnVariantsList() {
        List<String> variantNumberCbList = new ArrayList<>();
        variantNumberCbList.add("No Variant");
        variantNumberCbList.add("Variant 1");
        variantNumberCbList.add("Variant 2");
        variantNumberCbList.add("Variant 1 And 2");

        return variantNumberCbList;
    }
}
