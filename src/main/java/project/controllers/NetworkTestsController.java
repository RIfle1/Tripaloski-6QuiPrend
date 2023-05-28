package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project.network.MyServer;

import java.net.URL;
import java.util.ResourceBundle;

import static project.functions.JavaFxFunctions.*;

public class NetworkTestsController implements Initializable {
    private static Stage stage;
    private static MyServer server;
    private static boolean isRetrieve;
    private Thread retrieveThread;

    @FXML
    private TextArea messageTa;

    @FXML
    private ComboBox<String> modeCb;

    @FXML
    private TextField otherServerPortTf;

    public static void networkTestsScene(Stage stageParam, String title, int port) {
        stage = stageParam;
//        Image icon = new Image(returnImagePath("game/logo.png"));
//        stage.getIcons().add(icon);
        stage.setTitle(title + "on port " + port);
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("NetworkTests.fxml"));
        Scene scene = sendToScene(stage, fxmlLoader);

        server = new MyServer(port);
        server.startServer();
    }

    @FXML
    void modeOnAction(ActionEvent event) {
        if (modeCb.getValue().equals("Receiver")) {
            isRetrieve = true;
            retrieveThread = retrieveThread(messageTa);
            retrieveThread.start();
        }
        else if(modeCb.getValue().equals("Sender")) {
            try {
                isRetrieve = false;
                server.getSocket().close();
                retrieveThread.interrupt();
                System.out.printf("Thread is alive" + retrieveThread.isAlive());
            }
            catch (Exception e) {
                System.out.println("No thread to interrupt");
            }

        }

        System.out.println("Server set to: " + modeCb.getValue());
    }

    @FXML
    void sendOnClick(MouseEvent event) {
        String port = otherServerPortTf.getText();
        String message = messageTa.getText();

        System.out.println("Sending: " + message + " to port: " + port);

        if(port.length() > 0) {
            server.send(message, Integer.parseInt(port));
        }
        else {
            createPopup(stage, Alert.AlertType.ERROR, "Need a port pls");
        }
    }

    @FXML
    void openServerOnClick(MouseEvent event) {
        server.startServer();
    }

    public Thread retrieveThread(TextArea nodeToDisplayMessage) {
        return new Thread(() -> {
            while(isRetrieve) {
                nodeToDisplayMessage.setText(server.retrieve());
            }
        });
    }

    @FXML
    void stopServerOnClick(MouseEvent event) {
        server.stopServer();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modeCb.getItems().addAll("Receiver", "Sender");
        otherServerPortTf.setText("6666");
    }
}
