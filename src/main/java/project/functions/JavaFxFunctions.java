package project.functions;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import project.GuiLauncherMain;
import project.abstractClasses.AbstractCharacter;
import project.classes.Characters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static project.functions.GeneralFunctions.*;


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
    public static Scene sendToScene(Stage stage, FXMLLoader FXMLLoader) {
        Scene scene = getScene(FXMLLoader);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        return scene;
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

    public static void onExitKeyPressed(KeyEvent event, Runnable runnableFunc) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            runnableFunc.run();
        }
    }

    public static boolean checkConfirmationPopUp(Stage stage, String popUpMsg) {
        Optional<ButtonType> result = Objects.requireNonNull(createPopup(stage,
                Alert.AlertType.CONFIRMATION, popUpMsg));

        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
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
        Image spellImage = returnImage(objectName);
        ImageView spellImageView = new ImageView(spellImage);
        spellImageView.setFitHeight(height);
        spellImageView.setFitWidth(width);
        spellImageView.setOpacity(opacity);


        return spellImageView;
    }

    public static Rectangle returnImageRectangle(int width, int height,
                                                 int arcWidth, int arcHeight,
                                                 String imagePath) {
        Rectangle imageRectangle = new Rectangle(width, height);
        imageRectangle.setArcHeight(arcHeight);
        imageRectangle.setArcWidth(arcWidth);
        setRectangleImage(imageRectangle, imagePath);

        return imageRectangle;
    }

    public static void setRectangleImage(Rectangle rectangle, String imagePath) {
        ImagePattern imagePattern = new ImagePattern(returnImage(imagePath));
        rectangle.setFill(imagePattern);
    }


    /**
     * Returns an Image of a given image string
     *
     * @param imagePath Path of the image
     * @return Image Object of the given image string
     */
    public static Image returnImage(String imagePath) {
        return new Image(imagePath);
    }

    /**
     * Returns the path of a given image string
     *
     * @param objectName Name of the image
     * @return Path of the given image string
     */
    public static String returnImagePath(String objectName) {
//        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/images/")) + objectName;
        return returnPath("project/images/") + objectName;
    }

    /**
     * Returns a URL of a given FXML file
     *
     * @param fxmlName Name of the FXML file
     * @return URL of the given FXML file
     */
    public static URL returnFXMLURL(String fxmlName) {
//        try {
////            return new URL(Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource("project/fxml/")) + fxmlName);
//            String path = returnPath("project/fxml/") + fxmlName;
//            return new URL(path);
//
//
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
        return returnURL("project/fxml/", fxmlName);
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

    public static void deselectAllSubNodesS(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> node.getStyleClass().remove("clickableNodePressedS"));
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
        return parentGridPane.lookup("#" + childGridPaneFxId);
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

        if(result.get() == null) {
            System.out.println("getNullIdNodeByRowColumnIndex *FUNCTION* -> No node found on row " + row + " and column " + column);
        }

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
        try {
            Bounds startingBounds = attackingNode.localToScene(attackingNode.getBoundsInLocal());
            Bounds endingBounds = attackedNode.localToScene(attackedNode.getBoundsInLocal());

            double boundsXDiff = endingBounds.getCenterX() - startingBounds.getCenterX();
            double boundsYDiff = endingBounds.getCenterY() - startingBounds.getCenterY();

//            System.out.println("Bounds X Start: " + startingBounds.getCenterX());
//            System.out.println("Bounds Y Start: " + startingBounds.getCenterY());
//
//            System.out.println("Bounds X End: " + endingBounds.getCenterX());
//            System.out.println("Bounds Y End: " + endingBounds.getCenterY());
//
//            System.out.println("Bounds X Diff: " + boundsXDiff);
//            System.out.println("Bounds Y Diff: " + boundsYDiff);

            return translationEffect(attackingNode, boundsXDiff, boundsYDiff,
                    0.3, () -> {}, 0.5,
                    1, false);
        }
        catch (Exception e) {
            System.out.println("animateCardTranslation *FUNCTION* -> Attacking Node or Attacked Node is null");
            return new ArrayList<>();
        }

    }


    public static Timeline fadeOutEffect(Node node, double timeStamp, double endValue) {
        Timeline nodeFadeOutTimeLine = new Timeline();
        KeyValue nodeKey = new KeyValue(node.opacityProperty(), endValue, Interpolator.EASE_IN);
        KeyFrame nodeFrame = new KeyFrame(Duration.seconds(timeStamp), nodeKey);
        nodeFadeOutTimeLine.getKeyFrames().add(nodeFrame);

        nodeFadeOutTimeLine.play();
        return nodeFadeOutTimeLine;
    }

    public static Timeline fadeInEffect(Node node, double timeStamp) {
        Timeline nodeFadeInTimeLine = new Timeline();
        KeyValue nodeKey = new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_IN);
        KeyFrame nodeFrame = new KeyFrame(Duration.seconds(timeStamp), nodeKey);
        nodeFadeInTimeLine.getKeyFrames().add(nodeFrame);

        nodeFadeInTimeLine.play();
        return nodeFadeInTimeLine;

    }

    public static Timeline zoomInNode(Node node, double timeStamp, double zoomInFactor) {
        Timeline nodeZoomInTimeLine = new Timeline();
        KeyValue nodeKeyX = new KeyValue(node.scaleXProperty(), zoomInFactor, Interpolator.EASE_IN);
        KeyValue nodeKeyY = new KeyValue(node.scaleYProperty(), zoomInFactor, Interpolator.EASE_IN);
        KeyFrame nodeFrameX = new KeyFrame(Duration.seconds(timeStamp), nodeKeyX);
        KeyFrame nodeFrameY = new KeyFrame(Duration.seconds(timeStamp), nodeKeyY);
        nodeZoomInTimeLine.getKeyFrames().addAll(nodeFrameX, nodeFrameY);

        nodeZoomInTimeLine.play();
        return nodeZoomInTimeLine;
    }

    /**
     * Returns Character Info Grid Pane for the scoreboard
     *
     * @param imageName         Image name
     * @param abstractCharacter Abstract Character
     */
    public static GridPane returnCharacterInfoGridPane(String imageName, AbstractCharacter abstractCharacter) {
        Text playerName = new Text(abstractCharacter.getCharacterName());
        playerName.getStyleClass().add("playerInfoTextStyle1");

        Text playerPoints = new Text(abstractCharacter.getPoints() + "⭐");
        playerPoints.setId(abstractCharacter.getCharacterName() + "Points");
        playerPoints.getStyleClass().add("playerInfoTextStyle2");


        Rectangle imageRectangle = returnImageRectangle(50, 50, 50, 50, new File("icons/" + imageName).toURI().toString());
        imageRectangle.setEffect(new DropShadow(20, Color.BLACK));

        GridPane horizontalGridPane = new GridPane();
//        horizontalGridPane.setAlignment(Pos.CENTER);

        ColumnConstraints horizontalGridPaneColumn1 = new ColumnConstraints();
        ColumnConstraints horizontalGridPaneColumn2 = new ColumnConstraints();

        horizontalGridPaneColumn1.setPrefWidth(80);
        horizontalGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
        horizontalGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);
        horizontalGridPaneColumn1.setHalignment(HPos.CENTER);

        horizontalGridPaneColumn2.setPrefWidth(220);
        horizontalGridPaneColumn2.setMaxWidth(Region.USE_PREF_SIZE);
        horizontalGridPaneColumn2.setMinWidth(Region.USE_PREF_SIZE);

        RowConstraints horizontalGridPaneRow1 = new RowConstraints();

        horizontalGridPaneRow1.setPrefHeight(70);
        horizontalGridPaneRow1.setMaxHeight(Region.USE_PREF_SIZE);
        horizontalGridPaneRow1.setMinHeight(Region.USE_PREF_SIZE);

        horizontalGridPane.getColumnConstraints().addAll(horizontalGridPaneColumn1, horizontalGridPaneColumn2);
        horizontalGridPane.getRowConstraints().addAll(horizontalGridPaneRow1);

        horizontalGridPane.add(imageRectangle, 0, 0);

        GridPane verticalGridPane = new GridPane();

        RowConstraints verticalGridPaneRow1 = new RowConstraints();
        RowConstraints verticalGridPaneRow2 = new RowConstraints();

        verticalGridPaneRow1.setPrefHeight(35);
        verticalGridPaneRow1.setMaxHeight(Region.USE_PREF_SIZE);
        verticalGridPaneRow1.setMinHeight(Region.USE_PREF_SIZE);

        verticalGridPaneRow2.setPrefHeight(35);
        verticalGridPaneRow2.setMaxHeight(Region.USE_PREF_SIZE);
        verticalGridPaneRow2.setMinHeight(Region.USE_PREF_SIZE);

        verticalGridPane.getRowConstraints().addAll(verticalGridPaneRow1, verticalGridPaneRow2);

        verticalGridPane.add(playerName, 0, 0);
        verticalGridPane.add(playerPoints, 0, 1);


        horizontalGridPane.add(verticalGridPane, 1, 0);

        BackgroundFill horizontalGridPaneBackgroundFill = new BackgroundFill(new Color(1, 1, 1, 0.5), new CornerRadii(15), Insets.EMPTY);
        Background horizontalGridPaneBackground = new Background(horizontalGridPaneBackgroundFill);
        horizontalGridPane.setBackground(horizontalGridPaneBackground);
        horizontalGridPane.setId(abstractCharacter.getCharacterName());

        return horizontalGridPane;
    }

    public static void displayScoreBoard(GridPane parentGridPane, GridPane childGridPane, Characters characters
            , int columnIndex, int rowIndex, int maxPerColumn) {
        parentGridPane.getChildren().remove(childGridPane);
        childGridPane.getChildren().clear();


        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList("icons");

        AtomicInteger row = new AtomicInteger();

        characters.getCharactersList().forEach(character -> {
            String imageName;

            if(character.getCharacterImage() == null) {
                imageName = imageList.get((int) generateDoubleBetween(0, imageList.size() - 1));
                if (imageList.size() > 1) imageList.remove(imageName);
                character.setCharacterImage(imageName);
            }
            else {
                imageName = character.getCharacterImage();
            }

            GridPane characterInfoGridPane = returnCharacterInfoGridPane(imageName, character);


            if(rowNumber.get() > maxPerColumn - 1) {
                row.getAndIncrement();
                rowNumber.set(0);
            }
            childGridPane.add(characterInfoGridPane, row.get(), rowNumber.get());
            rowNumber.getAndIncrement();
        });


        parentGridPane.add(childGridPane, columnIndex, rowIndex);
    }

    public static void printPosition(Node node) {
        Bounds startingBounds = node.localToScene(node.getBoundsInLocal());
        System.out.println("Rectangle " + node.getId() + " bounds:");

        System.out.println("X: " + startingBounds.getCenterX());
        System.out.println("Y: " + startingBounds.getCenterY());
        System.out.println("-----");
    }

}
