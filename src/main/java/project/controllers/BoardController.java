package project.controllers;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.abstractClasses.AbstractCharacter;
import project.classes.Board;
import project.classes.Card;
import project.classes.CharacterList;
import project.classes.Deck;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static project.classes.Card.returnRandomCard;
import static project.controllers.MainMenuController.mainMenuScene;
import static project.functions.GeneralFunctions.*;
import static project.functions.JavaFxFunctions.*;

/**
 * Board Controller
 */
public class BoardController implements Initializable {
    private static Deck deck;
    private static Deck unmodifiableDeck;
    private static Board board;
    private static CharacterList characterList;
    // Variant 0 => No Modifications to the game
    // Variant 1 => Max cards based on number of players
    // Variant 2 => Players can choose their cards by turn
    // Variant 3 => Variant 1 + Variant 2
    private static int variantNumber;
    private static int roundNumber;
    private static GridPane scoreBoardGridPane;
    private static GridPane playerCardsGridPane;
    private static GridPane chosenCardsGridPane;
    private static ArrayList<Card> chosenCardsList;
    private static AbstractCharacter currentCharacter;
    private static Card currentCard;
    @FXML
    private GridPane chosenCardsInfoGridPane;
    @FXML
    private GridPane gameBoardGridPane;
    @FXML
    private GridPane gameStatsGridPane;
    @FXML
    private GridPane playerCardsInfoGridPane;
    @FXML
    private Text playerCardsT;
    @FXML
    private Text playerTurnT;
    //    @FXML
//    private Button chooseCardButton;
//    @FXML
//    private Button takeRowButton;
    @FXML
    private GridPane takenCardsGridPane;

    /**
     * Send to the Board Scene
     *
     * @param event              MouseEvent
     * @param playerNumberParam  Number of players
     * @param npcNumberParam     Number of NPCs
     * @param variantNumberParam Variant Number
     */

    public static void boardScene(MouseEvent event, int playerNumberParam, int npcNumberParam, int variantNumberParam, int roundNumberParam, int startingPointsParam) {
        variantNumber = variantNumberParam;
        roundNumber = roundNumberParam;

        deck = new Deck(variantNumber, playerNumberParam, npcNumberParam);
        unmodifiableDeck = new Deck(variantNumber, playerNumberParam, npcNumberParam);
        chosenCardsList = new ArrayList<>();

        characterList = new CharacterList(playerNumberParam, npcNumberParam, startingPointsParam);

        FXMLLoader characterCreationFxmlLoader = new FXMLLoader(returnFXMLURL("Board.fxml"));
        ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());

        Scene scene = sendToScene(actionEvent, characterCreationFxmlLoader);

        scene.setOnKeyPressed(e -> onKeyPressed(e, scene));
    }

    static void onKeyPressed(KeyEvent event, Scene scene) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            Stage stage = (Stage) scene.getWindow();
            mainMenuScene(stage);
        }
    }

    public AbstractCharacter findCharacterByCard(Card card) {
        for (AbstractCharacter character : characterList.getCharacters()) {
            for (Card c : character.getCardsList()) {
                if (c.getCardNumber() == card.getCardNumber()) {
                    return character;
                }
            }
        }
        return null;
    }

    public void resolveCards() {
        chosenCardsList = sortCardsByIncreasingOrder(chosenCardsList);

        Card[][] localBoard = board.getBoard();
        System.out.println("Cards currently in board:");
        board.getCardsList().forEach(card -> {
            System.out.print(card.getCardNumber() + ", ");
        });
        System.out.println();

        currentCard = chosenCardsList.get(0);

        int bestCardNumberDifference = currentCard.getCardNumber();
        int currentCardNumberDifference;
        int bestRow = -1;
        int bestColumn = -1;
        int rowTakenLength;

        for (int row = 0; row < localBoard.length; row++) {

            Card lastRowCard = null;
            rowTakenLength = 0;
            for (int column = 0; column < localBoard[row].length; column++) {
                if (localBoard[row][column] != null) {
                    rowTakenLength++;
                }

                if (localBoard[row][column + 1] == null) {
                    lastRowCard = localBoard[row][column];
                    break;
                }
            }
            assert lastRowCard != null;
            int lastRowCardNumber = lastRowCard.getCardNumber();
            int cardNumber = currentCard.getCardNumber();

            currentCardNumberDifference = Math.abs(cardNumber - lastRowCardNumber);

            boolean isCurrentBigger = lastRowCardNumber < cardNumber;
            boolean isDifferenceSmaller = currentCardNumberDifference < bestCardNumberDifference;

            System.out.println("Card Number : " + cardNumber +
                    " Last Row Card Number: " + lastRowCardNumber +
                    " currentDifference: " + currentCardNumberDifference +
                    " bestDifference: " + bestCardNumberDifference +
                    " isCurrentBigger: " + isCurrentBigger +
                    " isDifferenceSmaller: " + isDifferenceSmaller +
                    " Row: " + row +
                    " Column: " + rowTakenLength);

            if (isCurrentBigger && isDifferenceSmaller) {
                bestRow = row;
                bestColumn = rowTakenLength;
                bestCardNumberDifference = currentCardNumberDifference;
            }
        }
        System.out.println("Best Row: " + bestRow + " Best Column: " + bestColumn);

        if (bestRow != -1) {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " went somewhere");

            int finalBestRow = bestRow;
            int finalBestColumn = bestColumn;
            resolveChosenCardAnimation(currentCard, bestRow, bestColumn, () -> {
                resolveChosenCard(finalBestRow, finalBestColumn);
            });
        } else {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " cannot go anywhere");

            currentCharacter = findCharacterByCard(currentCard);
            displayPlayerInfo("", currentCharacter.getCharacterName() + "'s card '" + currentCard.getCardNumber() + "' is too small, please choose the row you want to take.");
            setBoardRowsOnHover();
//            takeRowButton.setDisable(false);
        }

        System.out.println("----------");

//        System.out.println("Chosen cards list size: " + chosenCardsList.size());

    }

    private void resolveChosenCard(int bestRow, int bestColumn) {
        board.getBoard()[bestRow][bestColumn] = currentCard;
//        board.getCardsList().forEach(card -> {
//            System.out.println("Card Number: " + card.getCardNumber());
//        });
//        System.out.println("-------------------");

        currentCharacter = findCharacterByCard(currentCard);
        currentCharacter.getCardsList().remove(currentCard);
        chosenCardsList.remove(currentCard);

        if (chosenCardsList.size() > 0) {
            resolveCards();
        } else {
//            chooseCardButton.setDisable(false);

            playerTurn(0);
        }
    }

    private void resolveChosenCardAnimation(Card card, int bestRow, int bestColumn, Runnable runnableFunc) {
        // ANIMATION STUFF
        Rectangle cardRectangle = (Rectangle) returnChildNodeById(chosenCardsGridPane, String.valueOf(card.getCardNumber()));
        Rectangle selectedCardRectangle2 = returnImageRectangle(90, 140, 10, 10, "cards/" + card.getCardNumber() + ".png");
        selectedCardRectangle2.setId(cardRectangle.getId());

        Rectangle targetRectangle = (Rectangle) getNullIdNodeByRowColumnIndex(bestRow, bestColumn, gameBoardGridPane);

        List<Timeline> timelineList = animateCardTranslation(cardRectangle, targetRectangle);
        timelineList.get(0).setOnFinished(e -> {
            gameBoardGridPane.add(selectedCardRectangle2, bestColumn, bestRow);
            chosenCardsGridPane.getChildren().remove(cardRectangle);
            runnableFunc.run();
        });

        timelineList.forEach(Timeline::play);
    }

    public void setBoardRowsOnHover() {
        enableAllGridPaneButtons(gameBoardGridPane);

        gameBoardGridPane.getChildren().forEach(node -> {

            if (node instanceof Rectangle) {

                if (node.getId() == null) {
                    node.getStyleClass().add("clickableNodeS");
                }

                int nodeRow = returnNodeRowIndex(node);
                node.setOnMouseEntered(e -> {
                    gameBoardGridPane.getChildren().forEach(nodeSub -> {
                        if (nodeSub instanceof Rectangle) {
                            setSynchronizedFunction(nodeRow, (Rectangle) nodeSub, () -> onMouseEntered((Rectangle) nodeSub));

                        }
                    });
                });

                node.setOnMouseExited(e1 -> {
                    gameBoardGridPane.getChildren().forEach(nodeSub -> {
                        if (nodeSub instanceof Rectangle) {
                            setSynchronizedFunction(nodeRow, (Rectangle) nodeSub, () -> onMouseExited((Rectangle) nodeSub));
                        }
                    });


                });

                node.setOnMouseReleased(e2 -> {
                    takeRowOnClick((Rectangle) node);
                    gameBoardGridPane.getChildren().forEach(nodeSub -> {
                        if (nodeSub instanceof Rectangle) {
                            setSynchronizedFunction(nodeRow, (Rectangle) nodeSub, () -> permanentSelectFunction((Rectangle) nodeSub));
                        }
                    });
                });
            }
        });
    }

    private void onMouseEntered(Rectangle nodeSub) {
        nodeSub.getStyleClass().remove("clickableNodeS");
        nodeSub.getStyleClass().add("clickableNodePressedS");
    }

    private void onMouseExited(Rectangle nodeSub) {
        nodeSub.getStyleClass().remove("clickableNodePressedS");
        nodeSub.getStyleClass().add("clickableNodeS");
    }

    private void permanentSelectFunction(Rectangle nodeSub) {
        nodeSub.getStyleClass().add("clickableNodePressedS");
    }

    private void setSynchronizedFunction(int finalNodeRow1, Rectangle nodeSub, Runnable func) {
        int nodeRowSub = returnNodeRowIndex(nodeSub);

        if (finalNodeRow1 == nodeRowSub && nodeSub.getId() == null) {
            func.run();
        }

    }

    public ArrayList<Card> sortCardsByIncreasingOrder(List<Card> cardsList) {
        return cardsList.stream().sorted(Comparator.comparingInt(Card::getCardNumber)).collect(Collectors.toCollection(ArrayList::new));
    }

    void chooseCardOnClick(Rectangle selectedCardRectangle) {
        try {
//            chooseCardButton.setDisable(true);

//            Rectangle selectedCardRectangle = (Rectangle) returnSelectedNodes(playerCardsGridPane, "clickableNodePressed").get(1);
            Card selectedCard = returnCharacterCardByNumber(currentCharacter, selectedCardRectangle.getId());

            // ADD THE CARD TO THE CHOSEN CARDS LIST
            chosenCardsList.add(selectedCard);

            // CREATE A NEW CARD RECTANGLE WITH THE BACKSIDE AND ADD IT TO THE CHOSEN CARDS GRID PANE
            Rectangle selectedCardRectangle2 = returnImageRectangle(90, 140, 10, 10, "cards/backside.png");
            selectedCardRectangle2.setId(selectedCardRectangle.getId());

            assert selectedCard != null;
            int cardsPlaced = chosenCardsGridPane.getChildren().size();

            int columnIndex = cardsPlaced;
            int rowIndex = (int) ((double) columnIndex / 5);
            if (columnIndex >= 5) columnIndex -= 5;

            int finalColumnIndex = columnIndex;
            List<Timeline> timelineList = animateCardTranslation(selectedCardRectangle, chosenCardsGridPane);
            timelineList.get(0).setOnFinished((actionEvent1) -> {
                playerCardsGridPane.getChildren().remove(selectedCardRectangle);
                chosenCardsGridPane.add(selectedCardRectangle2, finalColumnIndex, rowIndex);

                if (cardsPlaced + 1 >= characterList.getCharacters().size()) {
                    playerCardsGridPane.getChildren().forEach(node -> {
                        Timeline fadeOutTimeline = fadeOutEffect(node);
                        fadeOutTimeline.setOnFinished((actionEvent2) -> {
                            playerCardsGridPane.getChildren().remove(node);
                        });
                    });
                    displayPlayerInfo("", "Cards Resolution");

                    resolveCards();
                } else {
                    playerTurn(cardsPlaced + 1);
//                    chooseCardButton.setDisable(false);
                }
            });

            timelineList.forEach(Timeline::play);

        } catch (Exception e) {
            System.out.println("chooseCardOnClick *FUNCTION* -> No card selected");
            Stage stage = (Stage) gameBoardGridPane.getScene().getWindow();
            createPopup(stage, Alert.AlertType.ERROR, "Please select a card");
//            chooseCardButton.setDisable(false);
        }
    }

    @FXML
    void takeRowOnClick(Rectangle selectedRowRectangle) {
        try {
//            Rectangle selectedRowRectangle = (Rectangle) returnSelectedNodes(gameBoardGridPane, "clickableNodePressed").get(1);
            int selectedRow = returnNodeRowIndex(selectedRowRectangle);

            List<Card> cardsOnBoardRow = returnCardsOnBoardRow(selectedRow);
            // GAME LOGIC STUFF
            currentCharacter.getTakenCardsList().addAll(cardsOnBoardRow);
            deleteCardsOnBoardRow(selectedRow);

            // VISUAL AND ANIMATION STUFF
            calculateCharactersPoints();
            updatePlayerInfoGridPane(currentCharacter);
            deselectAllSubNodes(gameBoardGridPane);
//            takeRowButton.setDisable(true);
            disableAllGridPaneButtons(gameBoardGridPane);
            cardsOnBoardRow.forEach(card -> {
                Rectangle cardRectangle = (Rectangle) returnChildNodeById(gameBoardGridPane, String.valueOf(card.getCardNumber()));

                List<Timeline> timelineList = animateCardTranslation(cardRectangle, takenCardsGridPane);
                timelineList.get(0).setOnFinished((actionEvent) -> {
                    gameBoardGridPane.getChildren().remove(cardRectangle);
                    Rectangle cardRectangle2 = returnImageRectangle(90, 140, 10, 10, "cards/" + cardRectangle.getId() + ".png");

                    Node previousNode = takenCardsGridPane.getChildren().get(takenCardsGridPane.getChildren().size() - 1);
                    cardRectangle2.setTranslateX(previousNode.getTranslateX() + 25);

                    takenCardsGridPane.add(cardRectangle2, 0, 0);

                    deselectAllSubNodesS(gameBoardGridPane);
//                    deselectAllSubNodes(gameBoardGridPane);
                });
                timelineList.forEach(Timeline::play);
            });

            resolveChosenCardAnimation(currentCard, selectedRow, 0, () -> {
                resolveChosenCard(selectedRow, 0);
            });
        } catch (Exception e) {
            System.out.println("takeRowOnClick *FUNCTION* -> No row selected");
            Stage stage = (Stage) gameBoardGridPane.getScene().getWindow();
            createPopup(stage, Alert.AlertType.ERROR, "Please select a row");
        }
    }

    private List<Card> returnCardsOnBoardRow(int rowParam) {
        List<Card> cardsOnBoardRow = new ArrayList<>();
        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                if (row == rowParam && localBoard[row][column] != null) {
                    cardsOnBoardRow.add(localBoard[row][column]);
                }
            }
        }
        return cardsOnBoardRow;
    }

    private void deleteCardsOnBoardRow(int rowParam) {
        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                if (row == rowParam && localBoard[row][column] != null) {
                    localBoard[row][column] = null;
                }
            }
        }
    }

    private void calculateCharactersPoints() {
        List<Card> cardsToRemove = new ArrayList<>();

        characterList.getCharacters().forEach(character -> {
            character.getTakenCardsList().forEach(card -> {
                character.setPoints(character.getPoints() - card.getCardHeads());
                cardsToRemove.add(card);
            });

            character.getTakenCardsList().removeAll(cardsToRemove);
        });
    }

    private Card returnCharacterCardByNumber(AbstractCharacter abstractCharacter, String cardNumber) {
        return abstractCharacter.getCardsList().stream().filter(card -> card.getCardNumber() == Integer.parseInt(cardNumber)).findFirst().orElse(null);
    }

//    private void turnCardRectangleToBackside(Rectangle cardRectangle) {
//        cardRectangle.getStyleClass().remove("clickableNode");
//        cardRectangle.getStyleClass().remove("clickableNodePressed");
//        setRectangleImage(cardRectangle, "cards/backside.png");
//    }
//
//    private void turnCardRectangleToFrontSide(Rectangle cardRectangle) {
//        String cardId = cardRectangle.getId();
//        setRectangleImage(cardRectangle, unmodifiableDeck.getDeck().get(Integer.parseInt(cardId) - 1).getCardImage());
//    }

    /**
     * Initialize the game
     *
     * @param url            URL
     * @param resourceBundle Resource Bundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCharacterCards();

        initializeScoreBoard();
        initializeChosenCardsGridPane();
        initializePlayerCardsGridPane();

        displayScoreBoard();

        initializeBoard();
        displayBoard();

        playerTurn(0);
//        takeRowButton.setDisable(true);
    }

    private void playerTurn(int playerNumber) {
        currentCharacter = characterList.getCharacters().get(playerNumber);

        displayPlayerCards(currentCharacter);
        displayPlayerInfo(currentCharacter);

//        characterList.getCharacters().forEach(character -> {
//            displayCharacterCards(character);
//        });
    }

    private void displayPlayerCards(AbstractCharacter abstractCharacter) {
        List<Card> characterCards = abstractCharacter.getCardsList();
        playerCardsGridPane.getChildren().clear();

        for (int i = 0; i < characterCards.size(); i++) {
            Card card = characterCards.get(i);

            Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, "cards/" + card.getCardImage());
            imageRectangle.getStyleClass().add("clickableNode");
            imageRectangle.setId(String.valueOf(card.getCardNumber()));

            imageRectangle.setOnMouseReleased(event -> {
                chooseCardOnClick(imageRectangle);
                selectNode(playerCardsGridPane, imageRectangle);
            });

            playerCardsGridPane.add(imageRectangle, i, 0);
        }
    }

    private void displayPlayerInfo(AbstractCharacter abstractCharacter) {
        playerCardsT.setText(abstractCharacter.getCharacterName() + "'s Cards");
        playerTurnT.setText(abstractCharacter.getCharacterName() + "'s Turn");
    }

    private void displayPlayerInfo(String playerCardsText, String playerTurnText) {
        playerCardsT.setText(playerCardsText);
        playerTurnT.setText(playerTurnText);
    }

    private void initializeBoard() {
        board = new Board(new Card[4][6]);

        for (Card[] cardArray : board.getBoard()) {
            Card randomCard = returnRandomCard(deck);
            cardArray[0] = randomCard;
            deck.getDeck().remove(randomCard);
        }
    }

    private void displayBoard() {
        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                try {
                    Card card = localBoard[row][column];
                    Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, "cards/" + card.getCardImage());
                    imageRectangle.setId(String.valueOf(card.getCardNumber()));

                    gameBoardGridPane.add(imageRectangle, column, row);
                } catch (NullPointerException e) {
//                    System.out.println("No card on Row: " + row + " Column: " + column);
                }

            }
        }
    }

    /**
     * Initialize the characters cards
     */

    public void initializeCharacterCards() {
        characterList.getCharacters().forEach(character -> {
            for (int i = 1; i <= 10; i++) {
                giveCard(character.getCardsList());
            }
        });
    }

    private void giveCard(List<Card> abstractCharacterCardList) {
        Card randomCard = returnRandomCard(deck);
        abstractCharacterCardList.add(randomCard);
        deck.getDeck().remove(randomCard);
    }


    /**
     * Initialize the scoreboard GridPane
     */
    private void initializeScoreBoard() {
        scoreBoardGridPane = new GridPane();

        scoreBoardGridPane.setAlignment(Pos.TOP_CENTER);
        scoreBoardGridPane.setVgap(10);

        ColumnConstraints scoreBoardGridPaneColumn1 = new ColumnConstraints();

        scoreBoardGridPaneColumn1.setPrefWidth(290);
        scoreBoardGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
        scoreBoardGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);

        scoreBoardGridPane.getColumnConstraints().add(scoreBoardGridPaneColumn1);
    }

    private void initializeChosenCardsGridPane() {
        chosenCardsGridPane = new GridPane();

        chosenCardsGridPane.setAlignment(Pos.CENTER);
        GridPane.setMargin(chosenCardsGridPane, new Insets(5, 0, 5, 0));

        chosenCardsGridPane.setHgap(5);
        chosenCardsGridPane.setVgap(10);

        chosenCardsInfoGridPane.add(chosenCardsGridPane, 0, 0);
    }

    private void initializePlayerCardsGridPane() {
        playerCardsGridPane = new GridPane();

        playerCardsGridPane.setAlignment(Pos.TOP_LEFT);
        GridPane.setMargin(playerCardsGridPane, new Insets(0, 0, 0, 5));

        playerCardsGridPane.setHgap(5);

        playerCardsInfoGridPane.add(playerCardsGridPane, 0, 1);
    }

    /**
     * Display the scoreboard
     */
    private void displayScoreBoard() {
        gameStatsGridPane.getChildren().remove(scoreBoardGridPane);
        scoreBoardGridPane.getChildren().clear();


        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList(returnTargetPath("images/icons"));

        characterList.getCharacters().forEach(character -> {
            GridPane characterInfoGridPane = returnCharacterInfoGrid(imageList, character);
            scoreBoardGridPane.add(characterInfoGridPane, 0, rowNumber.get());
            rowNumber.getAndIncrement();
        });


        gameStatsGridPane.add(scoreBoardGridPane, 1, 1);
    }

    /**
     * Returns Character Info Grid Pane for the scoreboard
     *
     * @param imageList         Image List
     * @param abstractCharacter Abstract Character
     */
    private GridPane returnCharacterInfoGrid(ArrayList<String> imageList, AbstractCharacter abstractCharacter) {
        Text playerName = new Text(abstractCharacter.getCharacterName());
        playerName.getStyleClass().add("playerInfoTextStyle1");

        Text playerPoints = new Text(abstractCharacter.getPoints() + "⭐");
        playerPoints.setId(abstractCharacter.getCharacterName() + "Points");
        playerPoints.getStyleClass().add("playerInfoTextStyle2");

        String imageName = imageList.get((int) generateDoubleBetween(0, imageList.size() - 1));
        if (imageList.size() > 1) imageList.remove(imageName);

        Rectangle imageRectangle = returnImageRectangle(50, 50, 50, 50, "icons/" + imageName);
        imageRectangle.setEffect(new DropShadow(20, Color.BLACK));

        GridPane horizontalGridPane = new GridPane();
//            horizontalGridPane.setAlignment(Pos.CENTER);

        ColumnConstraints horizontalGridPaneColumn1 = new ColumnConstraints();
        ColumnConstraints horizontalGridPaneColumn2 = new ColumnConstraints();

        horizontalGridPaneColumn1.setPrefWidth(70);
        horizontalGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
        horizontalGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);
        horizontalGridPaneColumn1.setHalignment(HPos.CENTER);

        horizontalGridPaneColumn2.setPrefWidth(120);
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

        ColumnConstraints verticalGridPaneColumn1 = new ColumnConstraints();

        verticalGridPaneColumn1.setPrefWidth(120);
        verticalGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
        verticalGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);

        RowConstraints verticalGridPaneRow1 = new RowConstraints();
        RowConstraints verticalGridPaneRow2 = new RowConstraints();

        verticalGridPaneRow1.setPrefHeight(35);
        verticalGridPaneRow1.setMaxHeight(Region.USE_PREF_SIZE);
        verticalGridPaneRow1.setMinHeight(Region.USE_PREF_SIZE);

        verticalGridPaneRow2.setPrefHeight(35);
        verticalGridPaneRow2.setMaxHeight(Region.USE_PREF_SIZE);
        verticalGridPaneRow2.setMinHeight(Region.USE_PREF_SIZE);

        verticalGridPane.getColumnConstraints().addAll(verticalGridPaneColumn1);
        verticalGridPane.getRowConstraints().addAll(verticalGridPaneRow1, verticalGridPaneRow2);

        verticalGridPane.add(playerName, 0, 0);
        verticalGridPane.add(playerPoints, 0, 1);


        horizontalGridPane.add(verticalGridPane, 1, 0);

        BackgroundFill horizontalGridPaneBackgroundFill = new BackgroundFill(new Color(1, 1, 1, 0.5), new CornerRadii(15), Insets.EMPTY);
        Background horizontalGridPaneBackground = new Background(horizontalGridPaneBackgroundFill);
        horizontalGridPane.setBackground(horizontalGridPaneBackground);
        horizontalGridPane.setId(abstractCharacter.getCharacterName());

//        scoreBoardGridPane.add(horizontalGridPane, 0, rowNumber.get());
        return horizontalGridPane;
    }

    private void updatePlayerInfoGridPane(AbstractCharacter abstractCharacter) {
        Text playerPointsT = (Text) scoreBoardGridPane.lookup("#" + abstractCharacter.getCharacterName() + "Points");
        playerPointsT.setText(abstractCharacter.getPoints() + "⭐");
    }
}
