package project.network;

import javafx.application.Application;
import javafx.stage.Stage;

import static project.controllers.MainMenuController.mainMenuScene;
import static project.controllers.NetworkTestsController.networkTestsScene;

public class Server1 extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        networkTestsScene(stage, "Server 1", 6666);
    }
}
