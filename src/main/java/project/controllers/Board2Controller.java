package project.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Board2Controller {
    private Stage stage;
    private Scene scene;

    @FXML private FlowPane cardFlowPane;
    @FXML private Label playerTurnText;


    public void initialize() {
        int numRows = 4;
        int numColumns = 6;
        double rectangleWidth = 100.0;
        double rectangleHeight = 150.0;
        double arcWidth = 20.0;
        double arcHeight = 20.0;

        cardFlowPane.setPadding(new Insets(10));
        cardFlowPane.setHgap(10);
        cardFlowPane.setVgap(10);

        Rectangle rectangle = new Rectangle(rectangleWidth, rectangleHeight);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcWidth(arcWidth);
        rectangle.setArcHeight(arcHeight);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {

                cardFlowPane.getChildren().add(rectangle);
            }
        }

        playerTurnText.setText("Tour du joueur"+ " " + playerName); //playerName à implémenter

    }

}
