package project;

import javafx.application.Application;
import javafx.stage.Stage;

import static project.controllers.MainMenuController.mainMenuScene;

/**
 * This is the subclass to launch the GUI
 */
public class GuiLauncherMain extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        mainMenuScene(stage);
//        sendToServerScene(stage);
    }
}
