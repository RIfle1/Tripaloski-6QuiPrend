package project.functions;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import project.GuiLauncherMain;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class JavaFxFunctions {
    /**
     * Sends the user to the scene of the given FXMLLoader
     *
     * @param event      ActionEvent of the button clicked
     * @param FXMLLoader FXMLLoader of the scene to send the user to
     */
    public static Scene sendToScene(ActionEvent event, FXMLLoader FXMLLoader) {
        Scene scene = getScene(FXMLLoader);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    /**
     * Sends the user to the scene of the given FXMLLoader
     *
     * @param stage      Stage to send the user to
     * @param FXMLLoader FXMLLoader of the scene to send the user to
     */
    public static void sendToScene(Stage stage, FXMLLoader FXMLLoader) {
        Scene scene = getScene(FXMLLoader);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns the scene of the given FXML file
     *
     * @param FXMLLoader FXMLLoader of the scene to send the user to
     * @return Scene of the given FXML file
     */
    private static Scene getScene(FXMLLoader FXMLLoader) {
        Scene scene;

        try {
            scene = new Scene(FXMLLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scene;
    }

    /**
     * Creates a customized popup message
     *
     * @param event     ActionEvent of the button clicked
     * @param alertType Type of the popup
     * @param popUpMsg  Message to display in the popup
     * @return ButtonType of the button clicked to assign a function to it
     */
    public static Optional<ButtonType> createPopup(ActionEvent event, Alert.AlertType alertType, String popUpMsg) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert alert = new Alert(alertType, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);

        alert.getDialogPane().setContentText(popUpMsg);
        return alert.showAndWait();
    }

    /**
     * Creates a customized popup message
     *
     * @param stage     Stage to display the popup on
     * @param alertType Type of the popup
     * @param popUpMsg  Message to display in the popup
     * @return ButtonType of the button clicked to assign a function to it
     */
    public static Optional<ButtonType> createPopup(Stage stage, Alert.AlertType alertType, String popUpMsg) {
        Alert alert = new Alert(alertType, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);

        alert.getDialogPane().setContentText(popUpMsg);
        return alert.showAndWait();
    }

    /**
     * Returns an Image View of a given image string
     *
     * @param objectName Name of the image
     * @param height     Height of the image
     * @param width      Width of the image
     * @param opacity    Opacity of the image
     * @return Image View Object of the given image string
     */
    public static ImageView returnObjectImageView(String objectName, double height, double width, double opacity) {
        Image spellImage = returnObjectImage(objectName);
        ImageView spellImageView = new ImageView(spellImage);
        spellImageView.setFitHeight(height);
        spellImageView.setFitWidth(width);
        spellImageView.setOpacity(opacity);


        return spellImageView;
    }

    public static Rectangle returnImageRectangle(int width, int height,
                                                 int arcWidth, int arcHeight,
                                                 String imageName) {
        Rectangle imageRectangle = new Rectangle(width, height);
        imageRectangle.setArcHeight(arcHeight);
        imageRectangle.setArcWidth(arcWidth);
        setRectangleImage(imageRectangle, imageName);

        return imageRectangle;
    }

    public static void setRectangleImage(Rectangle rectangle, String imageName) {
        ImagePattern imagePattern = new ImagePattern(returnObjectImage(imageName));
        rectangle.setFill(imagePattern);
    }


    /**
     * Returns an Image of a given image string
     *
     * @param objectName Name of the image
     * @return Image Object of the given image string
     */
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

    /**
     * Returns the path of a given image string
     *
     * @param objectName Name of the image
     * @return Path of the given image string
     */
    public static String returnImagePath(String objectName) {
        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/images/")) + objectName;
    }

    public static String returnPath(String objectName) {
        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource(objectName)).toString() ;
    }

    /**
     * Returns a URL of a given FXML file
     *
     * @param fxmlName Name of the FXML file
     * @return URL of the given FXML file
     */
    public static URL returnFXMLURL(String fxmlName) {
        try {
            return new URL(Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/fxml/")) + fxmlName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checks the length of a given string and returns it with a maximum length
     *
     * @param string    String to check the length of
     * @param maxLength Maximum length of the string
     * @return String with a maximum length
     */
    public static String checkStringLength(String string, int maxLength) {

        StringBuilder stringBuilder = new StringBuilder();
        string = string.toLowerCase();

        try {
            Arrays.stream(string.split(" ")).toList().forEach(word ->
                    stringBuilder.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1))
                            .append(" ")
            );
        } catch (Exception e) {
            System.out.println("Error while checking player name length: " + e.getMessage());
        }

        string = stringBuilder.toString();

        if (string.length() > maxLength) {
            string = string.substring(0, maxLength) + "...";
        }
        return string;
    }

    /**
     * Disables a given node inside a grid pane
     *
     * @param parentGridPane Grid pane where the node is located
     * @param node           Node to disable
     */
    public static void disableGridPaneButton(GridPane parentGridPane, Node node) {
        parentGridPane.getChildren().stream()
                .filter(n -> n.equals(node))
                .forEach(n -> n.setDisable(true));
    }

    /**
     * Disables all nodes inside a grid pane
     *
     * @param parentGridPane Grid pane where the nodes are located
     */
    public static void disableAllGridPaneButtons(GridPane parentGridPane) {
        parentGridPane.getChildren()
                .forEach(node -> node.setDisable(true));
    }

    /**
     * Disables all nodes inside a grid pane
     *
     * @param mainGridPane       Main grid pane where the parent grid pane is located
     * @param parentGridPaneFxID ID of the parent grid pane
     */
    public static void disableAllGridPaneButtons(GridPane mainGridPane, String parentGridPaneFxID) {
        GridPane parentGridPane = (GridPane) mainGridPane.lookup("#" + parentGridPaneFxID);
        parentGridPane.getChildren().forEach(node -> node.setDisable(true));
    }

    /**
     * Enables a given node inside a grid pane
     *
     * @param parentGridPane Grid pane where the node is located
     * @param node           Node to enable
     */
    public static void enableGridPaneButton(GridPane parentGridPane, Node node) {
        parentGridPane.getChildren().stream()
                .filter(n -> n.equals(node))
                .forEach(n -> n.setDisable(false));
    }

    /**
     * Enables all nodes inside a grid pane
     *
     * @param parentGridPane Grid pane where the nodes are located
     */
    public static void enableAllGridPaneButtons(GridPane parentGridPane) {
        parentGridPane.getChildren().forEach(node -> node.setDisable(false));
    }

    /**
     * Enables all nodes inside a grid pane
     *
     * @param mainGridPane       Main grid pane where the parent grid pane is located
     * @param parentGridPaneFxID ID of the parent grid pane
     */
    public static void enableAllGridPaneButtons(GridPane mainGridPane, String parentGridPaneFxID) {
        GridPane parentGridPane = (GridPane) mainGridPane.lookup("#" + parentGridPaneFxID);
        parentGridPane.getChildren().forEach(node -> node.setDisable(false));
    }

    /**
     * Selects a given sub grid pane
     *
     * @param mainGridPane     Main grid pane where the sub grid pane is located
     * @param selectedNode Sub grid pane to select
     */
    public static void selectNode(GridPane mainGridPane, Node selectedNode) {
        deselectAllSubNodes(mainGridPane);
        selectedNode.getStyleClass().add("clickableNodePressed");
    }

    /**
     * Selects a given sub grid pane
     *
     * @param mainGridPane         Main grid pane where the sub grid pane is located
     * @param selectedGridPaneFxID ID of the sub grid pane to select
     */
    public static void selectNode(GridPane mainGridPane, String selectedGridPaneFxID) {
        Objects.requireNonNull(mainGridPane.getChildren().stream()
                        .filter(node -> node.getId().equals(selectedGridPaneFxID))
                        .findFirst()
                        .orElse(null))
                .getStyleClass().add("clickableNodePressed");
    }

    /**
     * De-selects all sub grid panes
     *
     * @param gridPane Main grid pane where the sub grid panes are located
     */
    public static void deselectAllSubNodes(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> node.getStyleClass().remove("clickableNodePressed"));
    }

    /**
     * De-selects all sub grid panes
     *
     * @param mainGridPane       Main grid pane where the parent grid pane is located
     * @param parentGridPaneFxID ID of the parent grid pane
     */
    public static void deselectAllSubNodes(GridPane mainGridPane, String parentGridPaneFxID) {
        GridPane gridPane = (GridPane) mainGridPane.lookup("#" + parentGridPaneFxID);

        gridPane.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                ((GridPane) node).getStyleClass().remove("clickableNodePressed");
            }
        });
    }

    /**
     * Returns a node by its class
     *
     * @param parentGridPane      Grid pane where the node is located
     * @param selectedObjectClass Class to search for in all the nodes in the parent grid pane
     * @return StringID of node on index 0 and Node with the given class on index 1
     */
    public static List<Object> returnSelectedNodes(GridPane parentGridPane, String selectedObjectClass) {
        return returnSelectedNodesSub(selectedObjectClass, parentGridPane);
    }

    /**
     * Returns a node by its class
     *
     * @param mainAnchorPane      Main anchor pane where the parent grid pane is located
     * @param parentGridPaneFxID  ID of the parent grid pane
     * @param selectedObjectClass Class to search for in all the nodes in the parent grid pane
     * @return StringID of node on index 0 and Node with the given class on index 1
     */
    public static List<Object> returnSelectedNodes(GridPane mainAnchorPane, String parentGridPaneFxID, String selectedObjectClass) {
        GridPane parentGridPane = (GridPane) mainAnchorPane.lookup("#" + parentGridPaneFxID);
        return returnSelectedNodesSub(selectedObjectClass, parentGridPane);
    }

    /**
     * Sub function of returnSelectedNodes
     *
     * @param selectedObjectClass Class to search for in all the nodes in the parent grid pane
     * @param parentGridPane      Grid pane where the node is located
     * @return StringID of node on index 0 and Node with the given class on index 1
     */
    @NotNull
    public static List<Object> returnSelectedNodesSub(String selectedObjectClass, GridPane parentGridPane) {

        try {
            Node selectedNode = returnNodeByClass(parentGridPane, selectedObjectClass);
            String nodeId = selectedNode.getId();

            return Arrays.asList(nodeId, selectedNode);
        } catch (NullPointerException e) {
            System.out.println("returnSelectedNodesSub *FUNCTION* -> No node found");
            return new ArrayList<>();
        }

    }

    /**
     * Returns a node by its class
     *
     * @param parentGridPane Grid pane where the node is located
     * @param objectClass    Class to search for in all the nodes in the parent grid pane
     * @return Node with the given class
     */
    public static Node returnNodeByClass(GridPane parentGridPane, String objectClass) {
        return parentGridPane.getChildren().stream()
                .filter(node -> node.getStyleClass().stream().anyMatch(styleClass -> styleClass.equals(objectClass)))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a node by its fx:id
     *
     * @param parentGridPane    Grid pane where the node is located
     * @param childGridPaneFxId fx:id of the node to search for
     * @return Node with the given fx:id
     */
    public static Node returnChildNodeById(GridPane parentGridPane, String childGridPaneFxId) {
        return parentGridPane.getChildren().stream()
                .filter(node -> {
                 if(node.getId() != null) {
                     return node.getId().equals(childGridPaneFxId);
                 } else {
                     return false;
                 }
                })
                .findFirst()
                .orElse(null);
    }

    public static Node getNullIdNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        AtomicReference<Node> result = new AtomicReference<>();
        gridPane.getChildren().forEach(node -> {
            if(node.getId() == null) {
                int nodeRow = returnNodeRowIndex(node);
                int nodeColumn = returnNodeColumnIndex(node);

                if(nodeRow == row && nodeColumn == column) {
                    result.set(node);
//                System.out.println("Node: " + node + " Row: " + nodeRow + " Column: " + nodeColumn);
                }
            }
        });

        return result.get();
    }

    public static int returnNodeRowIndex(Node node) {
        int nodeRow = 0;

        try {
            nodeRow = GridPane.getRowIndex(node);
        } catch (Exception ignored) {}

        return nodeRow;
    }

    public static int returnNodeColumnIndex(Node node) {
        int nodeColumn = 0;

        try {
            nodeColumn = GridPane.getColumnIndex(node);
        } catch (Exception ignored) {}

        return nodeColumn;
    }


    /**
     * Translates a node
     *
     * @param node                         Node to translate
     * @param boundsXDiff                  X translation
     * @param boundsYDiff                  Y translation
     * @param timeStamp                    Time of the translation
     * @param beforeFinishRunnable         Runnable to run before the translation finishes
     * @param beforeFinishTimeStampPercent Runnable to run at a certain percent of the translation
     * @param cycleCount                   Number of times to repeat the translation
     * @param autoReverse                  If the translation should be reversed
     * @return Timeline of the translation
     */
    @NotNull
    public static List<Timeline> translationEffect(Node node,
                                             double boundsXDiff, double boundsYDiff,
                                             double timeStamp, Runnable beforeFinishRunnable, double beforeFinishTimeStampPercent,
                                             int cycleCount, boolean autoReverse) {

        Timeline nodeTimelineX = new Timeline();
        Timeline nodeTimelineY = new Timeline();

        KeyFrame nodeKeyFrameX = new KeyFrame(Duration.seconds(timeStamp * beforeFinishTimeStampPercent), event -> beforeFinishRunnable.run());

        KeyValue nodeKeyValueX2 = new KeyValue(node.translateXProperty(), boundsXDiff, Interpolator.EASE_IN);
        KeyFrame nodeKeyFrameX2 = new KeyFrame(Duration.seconds(timeStamp), nodeKeyValueX2);

        nodeTimelineX.getKeyFrames().addAll(nodeKeyFrameX, nodeKeyFrameX2);

        KeyValue nodeKeyValueY = new KeyValue(node.translateYProperty(), boundsYDiff, Interpolator.EASE_IN);
        KeyFrame nodeKeyFrameY = new KeyFrame(Duration.seconds(timeStamp), nodeKeyValueY);
        nodeTimelineY.getKeyFrames().add(nodeKeyFrameY);

        nodeTimelineX.setCycleCount(cycleCount);
        nodeTimelineY.setCycleCount(cycleCount);

        nodeTimelineX.setAutoReverse(autoReverse);
        nodeTimelineY.setAutoReverse(autoReverse);

        List<Timeline> timelineList = new ArrayList<>();
        timelineList.add(nodeTimelineX);
        timelineList.add(nodeTimelineY);

        return timelineList;
    }

    public static List<Timeline> animateCardTranslation(Node attackingNode, Node attackedNode) {

        Bounds startingBounds = attackingNode.localToScene(attackingNode.getBoundsInLocal());
        Bounds endingBounds = attackedNode.localToScene(attackedNode.getBoundsInLocal());

        double boundsXDiff = endingBounds.getCenterX() - startingBounds.getCenterX();
        double boundsYDiff = endingBounds.getCenterY() - startingBounds.getCenterY();
//
//        System.out.println(attackingNode.getId() + " :boundsXDiff: " + boundsXDiff);
//        System.out.println(attackingNode.getId() + " :boundsYDiff: " + boundsYDiff);

//        System.out.println("Ending bounds: " + endingBounds);
//        System.out.println("Starting boundsX: " + startingBounds.getCenterX());
//        System.out.println("Starting boundsY: " + startingBounds.getCenterY());

//        System.out.println("AFTER");
//        System.out.println("Starting boundsX: " + startingBounds.getCenterX());
//        System.out.println("Starting boundsY: " + startingBounds.getCenterY());
//        System.out.println("Ending boundsX: " + endingBounds.getCenterX());
//        System.out.println("Ending boundsY: " + endingBounds.getCenterY());
//        System.out.println("------------------");

        return translationEffect(attackingNode, boundsXDiff, boundsYDiff,
                0.6, () -> {}, 0.5,
                1, false);
    }


    public static Timeline fadeOutEffect(Node node) {
        Timeline nodeFadeOutTimeLine = new Timeline();
        KeyValue nodeKey = new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_IN);
        KeyFrame nodeFrame = new KeyFrame(Duration.seconds(1), nodeKey);
        nodeFadeOutTimeLine.getKeyFrames().add(nodeFrame);

        nodeFadeOutTimeLine.play();
        return nodeFadeOutTimeLine;
    }

}
