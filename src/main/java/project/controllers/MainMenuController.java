package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static project.functions.JavaFxFunctions.returnFXMLURL;
import static project.functions.JavaFxFunctions.returnImagePath;

public class MainMenuController {

    @FXML
    void playButtonOnClicked(MouseEvent event) {

    }

    public static void mainMenuScene(Stage stage) {
        try {
            Parent root = FXMLLoader.load(returnFXMLURL("MainMenu.fxml"));
            Scene mainMenuScene = new Scene(root);

            stage.setScene(mainMenuScene);

            Image icon = new Image(returnImagePath("logo.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Harry Potter: THE GUI");

            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
