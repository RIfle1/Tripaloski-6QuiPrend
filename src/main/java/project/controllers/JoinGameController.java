package project.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static project.functions.JavaFxFunctions.returnFXMLURL;
import static project.functions.JavaFxFunctions.sendToScene;
import static project.network.Address.returnAddresses;

public class JoinGameController implements Initializable {
    private static GridPane serversGridPane;
    @FXML
    private GridPane serversInfoGridPane;
    @FXML
    private Text serverMessageT;
    @FXML
    private Button refreshButton;

    public static void sendToServerScene(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("JoinGame.fxml"));
        Scene scene = sendToScene(stage, fxmlLoader);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeServersGridPane();
        displayServersGridPane();
    }

    @FXML
    void joinOnClick(MouseEvent event) {

    }

    @FXML
    void refreshOnClick(MouseEvent event) {
        displayServersGridPane();
    }

    public void initializeServersGridPane() {
        serversGridPane = new GridPane();
        serversGridPane.setVgap(10);
        serversGridPane.setAlignment(Pos.TOP_CENTER);

        serversInfoGridPane.add(serversGridPane, 0, 0);
    }

    public void displayServersGridPane() {
        serverMessageT.setText("Scanning for Servers...");
        refreshButton.setDisable(true);
        serversGridPane.getChildren().clear();

        List<GridPane> serverGirdPlaneList = new ArrayList<>();

        Thread serverDisplayThread = new Thread(() -> {
            List<InetAddress> inetAddresses = returnAddresses(6666);
            System.out.println(inetAddresses);

            if(inetAddresses.size() > 0) {
                for(InetAddress inetAddress : inetAddresses) {
                    GridPane serverGridPane = new GridPane();
                    serverGridPane.setAlignment(Pos.CENTER);

                    RowConstraints serverGridPaneRow = new RowConstraints();

                    serverGridPaneRow.setPrefHeight(40);
                    serverGridPaneRow.setMaxHeight(Region.USE_PREF_SIZE);
                    serverGridPaneRow.setMinHeight(Region.USE_PREF_SIZE);

                    ColumnConstraints serverGridPaneColumn1 = new ColumnConstraints();
                    ColumnConstraints serverGridPaneColumn2 = new ColumnConstraints();

                    serverGridPaneColumn1.setPrefWidth(300);
                    serverGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
                    serverGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);
                    serverGridPaneColumn1.setHalignment(HPos.CENTER);

                    serverGridPaneColumn2.setPrefWidth(300);
                    serverGridPaneColumn2.setMaxWidth(Region.USE_PREF_SIZE);
                    serverGridPaneColumn2.setMinWidth(Region.USE_PREF_SIZE);
                    serverGridPaneColumn2.setHalignment(HPos.CENTER);

                    serverGridPane.getRowConstraints().add(serverGridPaneRow);
                    serverGridPane.getColumnConstraints().addAll(serverGridPaneColumn1, serverGridPaneColumn2);

                    Text serverNameText = new Text(inetAddress.getHostName());
                    serverNameText.getStyleClass().add("serverInfoText");

                    Text serverAddressText = new Text(inetAddress.getHostAddress());
                    serverAddressText.getStyleClass().add("serverInfoText");

                    BackgroundFill horizontalGridPaneBackgroundFill = new BackgroundFill(new Color(1, 1, 1, 0.5), new CornerRadii(15), Insets.EMPTY);
                    Background horizontalGridPaneBackground = new Background(horizontalGridPaneBackgroundFill);
                    serverGridPane.setBackground(horizontalGridPaneBackground);

                    serverGridPane.add(serverNameText, 0, 0);
                    serverGridPane.add(serverAddressText, 1, 0);

                    serverGridPane.getStyleClass().add("clickableNode");

                    serverGirdPlaneList.add(serverGridPane);
                }
            }
            String text = "Done Scanning, we found " + inetAddresses.size() + " server";
            if(inetAddresses.size() != 1) text += "s";

            serverMessageT.setText(text);

            Platform.runLater(() -> {
                for (GridPane serverGridPane : serverGirdPlaneList) {
                    serversGridPane.add(serverGridPane, 0, serverGirdPlaneList.indexOf(serverGridPane));
                }
            });

            refreshButton.setDisable(false);
        });

        serverDisplayThread.start();
    }
}
