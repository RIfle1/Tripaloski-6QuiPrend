package project;

import javafx.application.Application;
import javafx.stage.Stage;

import static project.controllers.MainMenuController.mainMenuScene;


public class GuiLauncherMain extends Application {
    @Override
    public void start(Stage stage){
        mainMenuScene(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
