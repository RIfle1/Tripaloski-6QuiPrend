package project.functions;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import project.GuiLauncherMain;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static project.functions.GeneralFunctions.chooseRandomDouble;


public class JavaFxFunctions {

    public static void sendToScene(ActionEvent event, FXMLLoader FXMLLoader) {
        Scene scene = getScene(FXMLLoader);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void sendToScene(Stage stage, FXMLLoader FXMLLoader) {
        Scene scene = getScene(FXMLLoader);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private static Scene getScene(FXMLLoader FXMLLoader) {
        Scene scene;

        try {
            scene = new Scene(FXMLLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scene;
    }

    public static Optional<ButtonType> createPopup(ActionEvent event, Alert.AlertType alertType, String popUpMsg) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert alert = new Alert(alertType, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);

        alert.getDialogPane().setContentText(popUpMsg);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> createPopup(Stage stage, Alert.AlertType alertType, String popUpMsg) {
        Alert alert = new Alert(alertType, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);

        alert.getDialogPane().setContentText(popUpMsg);
        return alert.showAndWait();
    }

    public static ImageView returnObjectImageView(String objectName, double height, double width, double opacity) {
        Image spellImage = returnObjectImage(objectName);
        ImageView spellImageView = new ImageView(spellImage);
        spellImageView.setFitHeight(height);
        spellImageView.setFitWidth(width);
        spellImageView.setOpacity(opacity);


        return spellImageView;
    }

    public static Image returnObjectImage(String objectName) {
        String imgPath;
        try {
            imgPath = returnImagePath(objectName);
        } catch (Exception e) {
            System.out.println("Image not found");
            imgPath = returnImagePath("unknown");
        }
        return new Image(imgPath);
    }

    public static String returnImagePath(String objectName) {
        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/images/"))  + objectName;
    }

    public static URL returnFXMLURL(String fxmlName) {
        try {
            return new URL(Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/fxml/")) + fxmlName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String checkPlayerNameLength(String playerName, int maxLength) {

        StringBuilder stringBuilder = new StringBuilder();
        playerName = playerName.toLowerCase();

        try {
            Arrays.stream(playerName.split(" ")).toList().forEach(word ->
                    stringBuilder.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1))
                            .append(" ")
            );
        } catch (Exception e) {
            System.out.println("Error while checking player name length: " + e.getMessage());
        }

        playerName = stringBuilder.toString();

        if(playerName.length() > maxLength) {
            playerName = playerName.substring(0, maxLength) + "...";
        }
        return playerName;
    }

    public static void disableGridPaneButton(GridPane parentGridPane, Node node) {
        parentGridPane.getChildren().stream()
                .filter(n -> n.equals(node))
                .forEach(n -> n.setDisable(true));
    }

    public static void disableAllGridPaneButtons(GridPane parentGridPane) {
        parentGridPane.getChildren()
                .forEach(node -> node.setDisable(true));
    }

    public static void disableAllGridPaneButtons(GridPane mainGridPane, String parentGridPaneFxID) {
        GridPane parentGridPane = (GridPane) mainGridPane.lookup("#" + parentGridPaneFxID);
        parentGridPane.getChildren().forEach(node -> node.setDisable(true));
    }


    public static void enableGridPaneButton(GridPane parentGridPane, Node node) {
        parentGridPane.getChildren().stream()
                .filter(n -> n.equals(node))
                .forEach(n -> n.setDisable(false));
    }

    public static void enableAllGridPaneButtons(GridPane parentGridPane) {
        parentGridPane.getChildren().forEach(node -> node.setDisable(false));
    }

    public static void enableAllGridPaneButtons(GridPane mainGridPane, String parentGridPaneFxID) {
        GridPane parentGridPane = (GridPane) mainGridPane.lookup("#" + parentGridPaneFxID);
        parentGridPane.getChildren().forEach(node -> node.setDisable(false));
    }

    public static void selectSubGridPane(GridPane mainGridPane, GridPane selectedGridPane) {
        mainGridPane.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                ((GridPane) node).getStyleClass().remove("clickableNodePressed");
            }
        });
        selectedGridPane.getStyleClass().add("clickableNodePressed");
    }

    public static void selectSubGridPane(GridPane mainGridPane, String selectedGridPaneFxID) {
        Objects.requireNonNull(mainGridPane.getChildren().stream()
                        .filter(node -> node.getId().equals(selectedGridPaneFxID))
                        .findFirst()
                        .orElse(null))
                .getStyleClass().add("clickableNodePressed");
    }

    public static void deselectAllSubGridPanes(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                ((GridPane) node).getStyleClass().remove("clickableNodePressed");
            }
        });
    }

    public static void deselectAllSubGridPanes(GridPane parentGridPane, String fxID) {
        GridPane gridPane = (GridPane) parentGridPane.lookup("#" + fxID);

        gridPane.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                ((GridPane) node).getStyleClass().remove("clickableNodePressed");
            }
        });
    }

    public static List<Object> returnSelectedNodes(GridPane parentGridPane, String selectedObjectClass) {
        return returnSelectedNodesSub(selectedObjectClass, parentGridPane);
    }

    public static List<Object> returnSelectedNodes(GridPane mainAnchorPane, String parentGridPaneFxID, String selectedObjectClass) {
        GridPane parentGridPane = (GridPane) mainAnchorPane.lookup("#" + parentGridPaneFxID);
        return returnSelectedNodesSub(selectedObjectClass, parentGridPane);
    }

    @NotNull
    public static List<Object> returnSelectedNodesSub(String selectedObjectClass, GridPane parentGridPane) {

        try {
            GridPane selectedNode = (GridPane) returnNodeByClass(parentGridPane, selectedObjectClass);
            String nodeId = selectedNode.getId();

            return Arrays.asList(nodeId, selectedNode);
        }
        catch (NullPointerException e) {
            System.out.println("returnSelectedNodesSub *FUNCTION* -> No node found");
            return new ArrayList<>();
        }

    }

    public static Node returnNodeByClass(GridPane parentGridPane, String objectClass) {
        return parentGridPane.getChildren().stream()
                .filter(node -> node.getStyleClass().stream().anyMatch(styleClass -> styleClass.equals(objectClass)))
                .findFirst()
                .orElse(null);
    }

    public static Node returnChildNodeById(GridPane parentGridPane, String childGridPaneFxId) {
        return parentGridPane.getChildren().stream().filter(node -> node.getId().equals(childGridPaneFxId))
                .map(node -> (GridPane) node)
                .findFirst()
                .orElse(null);
    }


    @NotNull
    public static Timeline translationEffect(boolean isTranslated, Node node,
                                             double boundsXDiff, double boundsYDiff,
                                             double timeStamp, Runnable beforeFinishRunnable, double beforeFinishTimeStampPercent,
                                             int cycleCount, boolean autoReverse) {

        Timeline nodeTimelineX = new Timeline();
        Timeline nodeTimelineY = new Timeline();

        KeyFrame nodeKeyFrameX = new KeyFrame(Duration.seconds(timeStamp * beforeFinishTimeStampPercent), event -> beforeFinishRunnable.run());

        KeyValue nodeKeyValueX2 = new KeyValue(node.translateXProperty(), boundsXDiff, Interpolator.EASE_IN);
        KeyFrame nodeKeyFrameX2 = new KeyFrame(Duration.seconds(timeStamp), nodeKeyValueX2);

        nodeTimelineX.getKeyFrames().addAll(nodeKeyFrameX, nodeKeyFrameX2);

        if(isTranslated) {
            double endY = chooseRandomDouble(new double[]{-1, 1});
            double power = 0.006;

            for (int x = 0; x < Math.abs(boundsXDiff); x++) {
                double y = endY * Math.exp(x * power);
                KeyValue nodeKeyValueY = new KeyValue(node.translateYProperty(), y, Interpolator.EASE_IN);
                KeyFrame nodeKeyFrameY = new KeyFrame(Duration.seconds((timeStamp / Math.abs(boundsXDiff)) * x), nodeKeyValueY);

                nodeTimelineY.getKeyFrames().add(nodeKeyFrameY);
            }

        }
        else {
            KeyValue nodeKeyValueY = new KeyValue(node.translateYProperty(), boundsYDiff, Interpolator.EASE_IN);
            KeyFrame nodeKeyFrameY = new KeyFrame(Duration.seconds(timeStamp), nodeKeyValueY);
            nodeTimelineY.getKeyFrames().add(nodeKeyFrameY);
        }




        nodeTimelineX.setCycleCount(cycleCount);
        nodeTimelineY.setCycleCount(cycleCount);

        nodeTimelineX.setAutoReverse(autoReverse);
        nodeTimelineY.setAutoReverse(autoReverse);

        Thread threadX = new Thread(nodeTimelineX::play);
        Thread threadY = new Thread(nodeTimelineY::play);

        threadX.start();
        threadY.start();
        return nodeTimelineX;
    }


}
