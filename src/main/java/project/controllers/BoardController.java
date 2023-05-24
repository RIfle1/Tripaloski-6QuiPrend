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
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.abstractClasses.AbstractCharacter;
import project.classes.Board;
import project.classes.Card;
import project.classes.CharacterList;
import project.classes.Deck;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static project.classes.Card.returnRandomCard;
import static project.controllers.EndGameController.endGameScene;
import static project.controllers.MainMenuController.mainMenuScene;
import static project.functions.GeneralFunctions.*;
import static project.functions.JavaFxFunctions.*;

/**
 * Board Controller
 */
public class BoardController implements Initializable {
    private static Stage stage;
    private static Deck deck;
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
    private static GridPane takenCardsGridPane;
    private static GridPane chosenCardsGridPane;
    private static ArrayList<Card> chosenCardsList;
    private static AbstractCharacter currentCharacter;
    private static Card currentCard;
    @FXML
    private GridPane cardsInfoGridPane;
    @FXML
    private GridPane chosenCardsInfoGridPane;
    @FXML
    private GridPane gameBoardGridPane;
    @FXML
    private GridPane scoreBoardInfoGridPane;
    @FXML
    private GridPane playerCardsInfoGridPane;
    @FXML
    private GridPane takenCardsInfoGridPane;
    @FXML
    private Text playerCardsT;
    @FXML
    private Text playerTurnT;

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
        chosenCardsList = new ArrayList<>();

        characterList = new CharacterList(playerNumberParam, npcNumberParam, startingPointsParam);

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("Board.fxml"));
        ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = sendToScene(actionEvent, fxmlLoader);


        scene.setOnKeyPressed(e -> onKeyPressed(e, scene));
    }

    static void onKeyPressed(KeyEvent event, Scene scene) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
//            Stage stage = (Stage) scene.getWindow();
            mainMenuScene(stage);
        }
    }

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
        initializeChosenCardsInfoGridPane();
        initializeChosenCardsGridPane();
        initializePlayerCardsGridPane();
        initializeTakenCardsGridPane();

        displayScoreBoard();

        initializeBoard();
        displayBoard();
        setBoardRowsOnHover();

        characterTurn(0);
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
//        System.out.println("Cards currently in board:");
//        board.getCardsList().forEach(card -> {
//            System.out.print(card.getCardNumber() + ", ");
//        });
//        System.out.println();

        currentCard = chosenCardsList.get(0);
        currentCharacter = findCharacterByCard(currentCard);

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

        if (bestRow > -1 && bestColumn < 5) {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " went on board");

            int finalBestRow = bestRow;
            int finalBestColumn = bestColumn;
            resolveChosenCardAnimation(currentCard, bestRow, bestColumn, () -> {
                resolveChosenCard(finalBestRow, finalBestColumn);
            });
        } else if (bestRow > -1) {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " is last card on row");

            sendCardsOnRow(bestRow);
        } else {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " has to be assigned by the owner");

            displayPlayerInfo("", currentCharacter.getCharacterName() + "'s card '"
                    + currentCard.getCardNumber() + "' is too small, please choose the row you want to take.");
            setBoardRowsOnHover();
            enableAllGridPaneButtons(gameBoardGridPane);

//            setBoardRowsOnHover();
//            takeRowButton.setDisable(false);
        }
        System.out.println("----------");

        checkEndGame();

//        System.out.println("Chosen cards list size: " + chosenCardsList.size());

    }

    private void checkEndGame() {
        if(isEndGame()) {
            disableAllGridPaneButtons(gameBoardGridPane);
            disableAllGridPaneButtons(playerCardsGridPane);

            displayPlayerInfo("", "Game Ended");

            endGameScene(stage);
        }
    }

    private boolean isEndGame() {
        List<AbstractCharacter> charactersWithNoPoints = returnCharactersWithNoPoints();
        boolean isEndGame = false;

        if (roundNumber == 0) {
            isEndGame = true;
            System.out.println("Round number is 0");
        } else if (charactersWithNoPoints.size() > 0) {
            isEndGame = true;
            System.out.println("Characters with no points: " + charactersWithNoPoints.size());
        }

        return isEndGame;
    }

    private List<AbstractCharacter> returnCharactersWithNoPoints() {
        List<AbstractCharacter> charactersWithNoPoints = new ArrayList<>();

        for (AbstractCharacter character : characterList.getCharacters()) {
            if (character.getPoints() < 0) {
                charactersWithNoPoints.add(character);
            }
        }

        return charactersWithNoPoints;
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
        } else if (!isEndGame()) {
//            chooseCardButton.setDisable(false);

            characterTurn(0);
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
//            sleep(100);
            gameBoardGridPane.add(selectedCardRectangle2, bestColumn, bestRow);
            chosenCardsGridPane.getChildren().remove(cardRectangle);
            runnableFunc.run();
        });

        timelineList.forEach(Timeline::play);
    }

    public void setBoardRowsOnHover() {

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

        disableAllGridPaneButtons(gameBoardGridPane);
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
            disableAllGridPaneButtons(playerCardsGridPane);

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
                        Timeline fadeOutTimeline = fadeOutEffect(node, 1);
                        fadeOutTimeline.setOnFinished((actionEvent2) -> {
                            playerCardsGridPane.getChildren().remove(node);
                        });
                    });
                    displayPlayerInfo("", "Cards Resolution");

                    resolveCards();
                    roundNumber--;
                } else {
                    characterTurn(cardsPlaced + 1);
//                    chooseCardButton.setDisable(false);
                }
            });

            timelineList.forEach(Timeline::play);

        } catch (Exception e) {
            System.out.println("chooseCardOnClick *FUNCTION* -> No card selected");
//            Stage stage = (Stage) gameBoardGridPane.getScene().getWindow();
            createPopup(stage, Alert.AlertType.ERROR, "Please select a card");
//            chooseCardButton.setDisable(false);
        }
    }

    @FXML
    void takeRowOnClick(Rectangle selectedRowRectangle) {
        disableAllGridPaneButtons(gameBoardGridPane);

        try {
//            Rectangle selectedRowRectangle = (Rectangle) returnSelectedNodes(gameBoardGridPane, "clickableNodePressed").get(1);
            int selectedRow = returnNodeRowIndex(selectedRowRectangle);

            sendCardsOnRow(selectedRow);
        } catch (Exception e) {
            System.out.println("takeRowOnClick *FUNCTION* -> No row selected");
//            Stage stage = (Stage) gameBoardGridPane.getScene().getWindow();
            createPopup(stage, Alert.AlertType.ERROR, "Please select a row");
        }
    }

    private void sendCardsOnRow(int selectedRow) {
        List<Card> cardsOnBoardRow = returnCardsOnBoardRow(selectedRow);
        // GAME LOGIC STUFF
        currentCharacter.getTakenCardsList().addAll(cardsOnBoardRow);
        deleteCardsOnBoardRow(selectedRow);
        calculateCharactersPoints();

        // VISUAL AND ANIMATION STUFF
        updatePlayerInfoGridPane(currentCharacter);
        deselectAllSubNodes(gameBoardGridPane);
//            disableAllGridPaneButtons(gameBoardGridPane);

        AtomicInteger row = new AtomicInteger();
        AtomicInteger column = new AtomicInteger(takenCardsGridPane.getChildren().size());
        int maxCardsPerRow = 36;

        cardsOnBoardRow.forEach(card -> {
            Rectangle cardRectangle = (Rectangle) returnChildNodeById(gameBoardGridPane, String.valueOf(card.getCardNumber()));

            List<Timeline> timelineList = animateCardTranslation(cardRectangle, takenCardsGridPane);
            timelineList.get(0).setOnFinished((actionEvent) -> {
                gameBoardGridPane.getChildren().remove(cardRectangle);
                Rectangle cardRectangle2 = returnImageRectangle(90, 140, 10, 10, "cards/" + cardRectangle.getId() + ".png");

                int cardsPlaced = takenCardsGridPane.getChildren().size();

                if (cardsPlaced < maxCardsPerRow) {
                    ColumnConstraints newColumn = new ColumnConstraints();
                    newColumn.setPrefWidth(cardRectangle2.getWidth() / 4);
                    newColumn.setMinWidth(Region.USE_PREF_SIZE);
                    newColumn.setMaxWidth(Region.USE_PREF_SIZE);
                    newColumn.setHalignment(HPos.CENTER);
                    takenCardsGridPane.getColumnConstraints().add(newColumn);
                } else if (column.get() >= maxCardsPerRow) {
                    row.set((int) Math.floor((double) cardsPlaced / maxCardsPerRow));
                    column.set(column.get() - maxCardsPerRow);
//                    System.out.println("***Changed***");
                }

                if (cardsPlaced < maxCardsPerRow * 2) takenCardsGridPane.add(cardRectangle2, column.get(), row.get());
//                System.out.println("Row: " + row.get() + " Column: " + column.get());
//                System.out.println("Columns count: " + takenCardsGridPane.getColumnConstraints().size());

                column.getAndIncrement();
                deselectAllSubNodesS(gameBoardGridPane);
            });
            timelineList.forEach(Timeline::play);
        });

        resolveChosenCardAnimation(currentCard, selectedRow, 0, () -> {
            resolveChosenCard(selectedRow, 0);
        });
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
            AtomicInteger pointsToRemove = new AtomicInteger();

            character.getTakenCardsList().forEach(card -> {
                int newPoints = character.getPoints() - card.getCardHeads();
                character.setPoints(newPoints);

                pointsToRemove.addAndGet(- card.getCardHeads());

                cardsToRemove.add(card);
            });

            character.getTakenCardsList().removeAll(cardsToRemove);

            displayCharacterPointsReduction(character, pointsToRemove.get());
        });
    }

    private void displayCharacterPointsReduction(AbstractCharacter abstractCharacter, int pointsToRemove) {
        GridPane characterInfoGridPane = (GridPane) scoreBoardGridPane.lookup("#" + abstractCharacter.getCharacterName());

        Text pointsToRemoveText = new Text(String.valueOf(pointsToRemove));
        pointsToRemoveText.setTranslateX(100);
        pointsToRemoveText.getStyleClass().add("pointsToRemoveText");

        if (pointsToRemove < 0) characterInfoGridPane.add(pointsToRemoveText, 1, 0);

        fadeInEffect(pointsToRemoveText, 1);
        zoomInNode(pointsToRemoveText, 0.5, 1.5).setOnFinished((e) -> {
            fadeOutEffect(pointsToRemoveText, 1).setOnFinished((e2) -> {
                characterInfoGridPane.getChildren().remove(pointsToRemoveText);
            });
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

    private void characterTurn(int playerNumber) {
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

    private void initializeChosenCardsInfoGridPane() {
        Rectangle tableImageRectangle = returnImageRectangle(931, 630, 40, 40, "game/tableBackground.png");
        cardsInfoGridPane.add(tableImageRectangle, 0, 0);
        tableImageRectangle.toBack();
        tableImageRectangle.toBack();

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
        GridPane.setMargin(playerCardsGridPane, new Insets(5, 5, 5, 5));

        playerCardsGridPane.setHgap(5);

        playerCardsInfoGridPane.add(playerCardsGridPane, 0, 1);
    }

    private void initializeTakenCardsGridPane() {
        takenCardsGridPane = new GridPane();

        takenCardsGridPane.setAlignment(Pos.CENTER);
        GridPane.setMargin(takenCardsGridPane, new Insets(10, 5, 10, 5));

        takenCardsGridPane.setVgap(20);

        takenCardsInfoGridPane.add(takenCardsGridPane, 0, 0);
    }

    /**
     * Display the scoreboard
     */
    private void displayScoreBoard() {
        scoreBoardInfoGridPane.getChildren().remove(scoreBoardGridPane);
        scoreBoardGridPane.getChildren().clear();


        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList(returnTargetPath("images/icons"));

        characterList.getCharacters().forEach(character -> {
            GridPane characterInfoGridPane = returnCharacterInfoGridPane(imageList, character);
            scoreBoardGridPane.add(characterInfoGridPane, 0, rowNumber.get());
            rowNumber.getAndIncrement();
        });


        scoreBoardInfoGridPane.add(scoreBoardGridPane, 0, 1);
    }

    /**
     * Returns Character Info Grid Pane for the scoreboard
     *
     * @param imageList         Image List
     * @param abstractCharacter Abstract Character
     */
    private GridPane returnCharacterInfoGridPane(ArrayList<String> imageList, AbstractCharacter abstractCharacter) {
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
