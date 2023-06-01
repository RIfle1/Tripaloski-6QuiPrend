package project.functions;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import project.abstractClasses.AbstractCharacter;
import project.classes.Characters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * Runs a function when the Escape key is pressed
     *
     * @param event        KeyEvent of the key pressed
     * @param runnableFunc Function to run when the Escape key is pressed
     */
    public static void onExitKeyPressed(KeyEvent event, Runnable runnableFunc) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            runnableFunc.run();
        }
    }

    /**
     * Checks if the OK button has been clicked in a confirmation popup
     *
     * @param stage    stage
     * @param popUpMsg message to display
     * @return true if OK button is clicked, false otherwise
     */
    public static boolean checkConfirmationPopUp(Stage stage, String popUpMsg) {
        Optional<ButtonType> result = Objects.requireNonNull(createPopup(stage,
                Alert.AlertType.CONFIRMATION, popUpMsg));

        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
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
     * Returns a Rectangle of a given image string
     *
     * @param width     width of the rectangle
     * @param height    height of the rectangle
     * @param arcWidth  arc width of the rectangle
     * @param arcHeight arc height of the rectangle
     * @param imagePath path of the image
     * @return Rectangle Object of the given image string
     */
    public static Rectangle returnImageRectangle(int width, int height,
                                                 int arcWidth, int arcHeight,
                                                 String imagePath) {
        Rectangle imageRectangle = new Rectangle(width, height);
        imageRectangle.setArcHeight(arcHeight);
        imageRectangle.setArcWidth(arcWidth);
        setRectangleImage(imageRectangle, imagePath);

        return imageRectangle;
    }

    /**
     * Sets the image of a given rectangle
     *
     * @param rectangle rectangle to set the image on
     * @param imagePath path of the image
     */
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
        return returnPath("project/images/") + objectName;
    }

    /**
     * Returns a URL of a given FXML file
     *
     * @param fxmlName Name of the FXML file
     * @return URL of the given FXML file
     */
    public static URL returnFXMLURL(String fxmlName) {
        return returnURL("project/fxml/", fxmlName);
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
     * Enables all nodes inside a grid pane
     *
     * @param parentGridPane Grid pane where the nodes are located
     */
    public static void enableAllGridPaneButtons(GridPane parentGridPane) {
        parentGridPane.getChildren().forEach(node -> node.setDisable(false));
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
     * Returns a node by its fx:id
     *
     * @param parentGridPane    Grid pane where the node is located
     * @param childGridPaneFxId fx:id of the node to search for
     * @return Node with the given fx:id
     */
    public static Node returnChildNodeById(GridPane parentGridPane, String childGridPaneFxId) {
        return parentGridPane.lookup("#" + childGridPaneFxId);
    }

    /**
     * Returns nodes with a null fx:id inside a grid pane with a column and a row
     *
     * @param row            row of the node
     * @param column         column of the node
     * @param parentGridPane grid pane where the node is located
     * @return Node with a null fx:if and the given row and column
     */
    public static Node getNullIdNodeByRowColumnIndex(final int row, final int column, GridPane parentGridPane) {
        AtomicReference<Node> result = new AtomicReference<>();
        parentGridPane.getChildren().forEach(node -> {
            if (node.getId() == null) {
                int nodeRow = returnNodeRowIndex(node);
                int nodeColumn = returnNodeColumnIndex(node);

                if (nodeRow == row && nodeColumn == column) {
                    result.set(node);
                }
            }
        });

        if (result.get() == null) {
            System.out.println("getNullIdNodeByRowColumnIndex *FUNCTION* -> No node found on row " + row + " and column " + column);
        }

        return result.get();
    }

    /**
     * Returns the row index of a given node
     *
     * @param node node to get the row index from
     * @return row index of the given node
     */
    public static int returnNodeRowIndex(Node node) {
        int nodeRow = 0;

        try {
            nodeRow = GridPane.getRowIndex(node);
        } catch (Exception ignored) {
        }

        return nodeRow;
    }

    /**
     * Returns the column index of a given node
     *
     * @param node node to get the column index from
     * @return column index of the given node
     */
    public static int returnNodeColumnIndex(Node node) {
        int nodeColumn = 0;

        try {
            nodeColumn = GridPane.getColumnIndex(node);
        } catch (Exception ignored) {
        }

        return nodeColumn;
    }

    /**
     * Translates a node by X and Y values
     *
     * @param node                         Node to translate
     * @param boundsXDiff                  X translation amount
     * @param boundsYDiff                  Y translation amount
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

    /**
     * Animates a card translation from one gridPane to another
     *
     * @param attackingNode node to translate
     * @param attackedNode  node to translate to
     * @return Timeline of the translation
     */
    public static List<Timeline> animateCardTranslation(Node attackingNode, Node attackedNode) {
        try {

            Bounds startingBounds = attackingNode.localToScene(attackingNode.getBoundsInLocal());
            Bounds endingBounds = attackedNode.localToScene(attackedNode.getBoundsInLocal());

            double endingBoundsX = endingBounds.getCenterX();
            double endingBoundsY = endingBounds.getCenterY();

            double startingBoundsX = startingBounds.getCenterX();
            double startingBoundsY = startingBounds.getCenterY();

            double boundsXDiff = endingBoundsX - startingBoundsX;
            double boundsYDiff = endingBoundsY - startingBoundsY;

//            System.out.println("Ending Bounds X: " + endingBoundsX);
//            System.out.println("Ending Bounds Y: " + endingBoundsY);
//
//            System.out.println("Starting Bounds X: " + startingBoundsX);
//            System.out.println("Starting Bounds Y: " + startingBoundsY);
//
//            System.out.println("boundsXDiff: " + " " + boundsXDiff);
//            System.out.println("boundsYDiff: " + " " + boundsYDiff);

            return translationEffect(attackingNode, boundsXDiff, boundsYDiff,
                    0.3, () -> {
                    }, 0.5,
                    1, false);
        } catch (Exception e) {
            System.out.println("animateCardTranslation *FUNCTION* -> Attacking Node or Attacked Node is null");
            return new ArrayList<>();
        }

    }

    /**
     * Fade out Effect for a node
     *
     * @param node      node to fade out
     * @param timeStamp time of the fade out
     * @param endValue  end value of the fade out
     * @return Timeline of the fade out
     */
    public static Timeline fadeOutEffect(Node node, double timeStamp, double endValue) {
        Timeline nodeFadeOutTimeLine = new Timeline();
        KeyValue nodeKey = new KeyValue(node.opacityProperty(), endValue, Interpolator.EASE_IN);
        KeyFrame nodeFrame = new KeyFrame(Duration.seconds(timeStamp), nodeKey);
        nodeFadeOutTimeLine.getKeyFrames().add(nodeFrame);

        nodeFadeOutTimeLine.play();
        return nodeFadeOutTimeLine;
    }

    /**
     * Fade in Effect for a node
     *
     * @param node      node to fade in
     * @param timeStamp time of the fade in
     */
    public static void fadeInEffect(Node node, double timeStamp) {
        Timeline nodeFadeInTimeLine = new Timeline();
        KeyValue nodeKey = new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_IN);
        KeyFrame nodeFrame = new KeyFrame(Duration.seconds(timeStamp), nodeKey);
        nodeFadeInTimeLine.getKeyFrames().add(nodeFrame);

        nodeFadeInTimeLine.play();

    }

    /**
     * Zoom in effect of a node
     *
     * @param node         node to zoom in
     * @param timeStamp    time of the zoom in
     * @param zoomInFactor zoom in factor
     * @return Timeline of the zoom in
     */
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

    /**
     * Displays the scoreboard
     *
     * @param parentGridPane parent grid pane
     * @param childGridPane  child grid pane
     * @param characters     characters to put on the scoreboard
     * @param columnIndex    column index to add the scoreboard to
     * @param rowIndex       row index to add the scoreboard to
     * @param maxPerColumn   max per characters per column
     */
    public static void displayScoreBoard(GridPane parentGridPane, GridPane childGridPane, Characters characters
            , int columnIndex, int rowIndex, int maxPerColumn) {
        parentGridPane.getChildren().remove(childGridPane);
        childGridPane.getChildren().clear();


        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList("icons");

        AtomicInteger row = new AtomicInteger();

        characters.getCharactersList().forEach(character -> {
            String imageName;

            if (character.getCharacterImage() == null) {
                imageName = imageList.get((int) generateDoubleBetween(0, imageList.size() - 1));
                if (imageList.size() > 1) imageList.remove(imageName);
                character.setCharacterImage(imageName);
            } else {
                imageName = character.getCharacterImage();
            }

            GridPane characterInfoGridPane = returnCharacterInfoGridPane(imageName, character);


            if (rowNumber.get() > maxPerColumn - 1) {
                row.getAndIncrement();
                rowNumber.set(0);
            }
            childGridPane.add(characterInfoGridPane, row.get(), rowNumber.get());
            rowNumber.getAndIncrement();
        });


        parentGridPane.add(childGridPane, columnIndex, rowIndex);
    }

    /**
     * Prints the position of a node
     *
     * @param node node to print the position of
     */
    public static void printPosition(Node node) {
        Bounds startingBounds = node.localToScene(node.getBoundsInLocal());
        System.out.println("Rectangle " + node.getId() + " bounds:");

        System.out.println("X: " + startingBounds.getCenterX());
        System.out.println("Y: " + startingBounds.getCenterY());
        System.out.println("-----");
    }

    public static void printGridPaneChildrenPositions(GridPane parentGridPane) {
        parentGridPane.getChildren().forEach(JavaFxFunctions::printPosition);

    }

}
