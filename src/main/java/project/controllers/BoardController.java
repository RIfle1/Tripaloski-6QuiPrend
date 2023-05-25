package project.controllers;

import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import project.abstractClasses.AbstractCharacter;
import project.classes.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static project.classes.Card.returnRandomCard;
import static project.controllers.EndGameController.endGameScene;
import static project.controllers.MainMenuController.mainMenuScene;
import static project.controllers.MainMenuController.onExitKeyPressed;
import static project.functions.GeneralFunctions.generateDoubleBetween;
import static project.functions.JavaFxFunctions.*;

/**
 * Board Controller
 */
public class BoardController implements Initializable {
    public static Characters characters;
    private static Stage stage;
    private static Deck deck;
    private static Board board;
    // Variant 0 => No Modifications to the game
    // Variant 1 => Max cards based on number of players
    // Variant 2 => Players can choose their cards by turn
    // Variant 3 => Variant 1 + Variant 2
    private static int roundNumber;
    private static GridPane scoreBoardGridPane;
    private static GridPane characterCardsGridPane;
    private static GridPane takenCardsGridPane;
    private static GridPane chosenCardsGridPane;
    private static ArrayList<Card> chosenCardsList = new ArrayList<>();
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
    private GridPane characterCardsInfoGridPane;
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
        roundNumber = roundNumberParam;
        deck = new Deck(variantNumberParam, playerNumberParam, npcNumberParam);
        characters = new Characters(playerNumberParam, npcNumberParam, startingPointsParam);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        boardSceneSub(stage, roundNumberParam);
    }

    public static void boardScene(Stage stageParam, Deck deckParam, Characters charactersParam, int roundNumberParam) {
        roundNumber = roundNumberParam;
        characters = charactersParam;
        deck = deckParam;
        stage = stageParam;

        boardSceneSub(stageParam, roundNumberParam);
    }

    private static void boardSceneSub(Stage stage, int roundNumberParam) {
        roundNumber = roundNumberParam;
        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("Board.fxml"));

        Scene scene = sendToScene(stage, fxmlLoader);

        scene.setOnKeyPressed(e -> onExitKeyPressed(e, BoardController::exit));
    }

    static void exit() {
        String msg = "Are you sure you want to return to the Game Menu? All progress will be lost.";
        if (checkConfirmationPopUp(stage, msg)) mainMenuScene(stage);
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

        displayScoreBoard(scoreBoardInfoGridPane, scoreBoardGridPane, characters, 0, 1, 10);

        initializeBoard();
        displayBoard();
        setBoardRowsOnHover();

        characterTurn(0);
    }

    public AbstractCharacter findCharacterByCard(Card card) {
        for (AbstractCharacter character : characters.getCharactersList()) {
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

        currentCard = chosenCardsList.get(0);
        currentCharacter = findCharacterByCard(currentCard);

        int bestCardNumberDifference = currentCard.getCardNumber();
        int cardNumber = currentCard.getCardNumber();
        int bestRow = -1;
        int bestColumn = -1;

        for (int row = 0; row < localBoard.length; row++) {

            RowResults rowResults = getRowResults(localBoard, row);
            RowCalculations rowCalculations = getRowCalculations(bestCardNumberDifference, rowResults, cardNumber);

            System.out.println(currentCharacter.getCharacterName() +
                    " | Card Number: " + cardNumber +
                    " | Last Row Card Number: " + rowResults.lastRowCardNumber() +
                    " | currentDifference: " + rowCalculations.currentCardNumberDifference() +
                    " | bestDifference: " + bestCardNumberDifference +
                    " | isCurrentBigger: " + rowCalculations.isCurrentBigger() +
                    " | isDifferenceSmaller: " + rowCalculations.isDifferenceSmaller() +
                    " | Row: " + row +
                    " | Column: " + rowResults.rowTakenLength());

            if (rowCalculations.isCurrentBigger() && rowCalculations.isDifferenceSmaller()) {
                bestRow = row;
                bestColumn = rowResults.rowTakenLength();
                bestCardNumberDifference = rowCalculations.currentCardNumberDifference();
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

            removeCardsFromBoard(bestRow);
        } else {
            System.out.println("Card Number: " + currentCard.getCardNumber() + " has to be assigned by the owner");

            String playerTurnText = currentCharacter.getCharacterName() + "'s card '"
                    + currentCard.getCardNumber() + "' is too small";

            if(currentCharacter instanceof Player) {
                playerTurnText += ", please choose the row you want to take.";
                setBoardRowsOnHover();
                enableAllGridPaneButtons(gameBoardGridPane);
            }
            else if(currentCharacter instanceof Npc) {
                playerTurnText += ", NPC will choose the row to take.";

                removeCardsFromBoard(findBestRowToTake());
            }

            displayCharacterInfo("", playerTurnText);

//            setBoardRowsOnHover();
//            takeRowButton.setDisable(false);
        }
        System.out.println("----------");
    }

    @NotNull
    private static RowCalculations getRowCalculations(int bestCardNumberDifference, RowResults rowResults, int cardNumber) {
        int currentCardNumberDifference = Math.abs(cardNumber - rowResults.lastRowCardNumber());
        boolean isCurrentBigger = rowResults.lastRowCardNumber() < cardNumber;
        boolean isDifferenceSmaller = currentCardNumberDifference < bestCardNumberDifference;
        return new RowCalculations(currentCardNumberDifference, isCurrentBigger, isDifferenceSmaller);
    }

    private record RowCalculations(int currentCardNumberDifference, boolean isCurrentBigger, boolean isDifferenceSmaller) {
    }

    @NotNull
    private static RowResults getRowResults(Card[][] localBoard, int row) {
        Card lastRowCard = null;
        int rowTakenLength = 0;
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
        int rowLastCardNumber = lastRowCard.getCardNumber();
        return new RowResults(rowTakenLength, rowLastCardNumber);
    }

    private record RowResults(int rowTakenLength, int lastRowCardNumber) {
    }

    private int findBestRowToTake() {

        int bestRow = -1;
        int leastRowHeads = 100;

        for(int row = 0; row < board.getBoard().length; row++) {
            int rowHeads = 0;

            for(int column = 0; column < board.getBoard()[row].length; column++) {
                if(board.getBoard()[row][column] != null) {
                    rowHeads += board.getBoard()[row][column].getCardHeads();
                }
            }

            if(rowHeads < leastRowHeads) {
                bestRow = row;
                leastRowHeads = rowHeads;
            }
        }

        System.out.println("Best Row: " + bestRow + " Least Row Heads: " + leastRowHeads);

        return bestRow;
    }

    private void checkEndGame() {
        roundNumber--;

        if (isEndGame()) {
            disableAllGridPaneButtons(gameBoardGridPane);
            disableAllGridPaneButtons(characterCardsGridPane);

            displayCharacterInfo("", "Game Ended");

            endGameScene(stage);
        }
    }

    private boolean isEndGame() {
        List<AbstractCharacter> charactersWithNoPoints = returnCharactersWithNoPoints();
        boolean isEndGame = false;

        if (roundNumber == 0) {
            isEndGame = true;
        } else if (charactersWithNoPoints.size() > 0) {
            isEndGame = true;
        }

        return isEndGame;
    }

    private List<AbstractCharacter> returnCharactersWithNoPoints() {
        List<AbstractCharacter> charactersWithNoPoints = new ArrayList<>();

        for (AbstractCharacter character : characters.getCharactersList()) {
            if (character.getPoints() < 0) {
                charactersWithNoPoints.add(character);
            }
        }

        return charactersWithNoPoints;
    }

    private void resolveChosenCard(int bestRow, int bestColumn) {
        board.getBoard()[bestRow][bestColumn] = currentCard;

        currentCharacter = findCharacterByCard(currentCard);
        currentCharacter.getCardsList().remove(currentCard);
        chosenCardsList.remove(currentCard);

        if (chosenCardsList.size() > 0) {
            resolveCards();
        } else {
//            chooseCardButton.setDisable(false);
            checkEndGame();
            if (!isEndGame()) characterTurn(0);
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
            disableAllGridPaneButtons(characterCardsGridPane);

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
                characterCardsGridPane.getChildren().remove(selectedCardRectangle);
                chosenCardsGridPane.add(selectedCardRectangle2, finalColumnIndex, rowIndex);

                if (cardsPlaced + 1 >= characters.getCharactersList().size()) {
                    characterCardsGridPane.getChildren().forEach(node -> {
                        Timeline fadeOutTimeline = fadeOutEffect(node, 1, 0);
                        fadeOutTimeline.setOnFinished((actionEvent2) -> {
                            characterCardsGridPane.getChildren().remove(node);
                        });
                    });
                    displayCharacterInfo("", "Cards Resolution");

                    resolveCards();
                } else {
                    characterTurn(cardsPlaced + 1);
                }
            });

            timelineList.forEach(Timeline::play);

        } catch (Exception e) {
            System.out.println("chooseCardOnClick *FUNCTION* -> No card selected");
            createPopup(stage, Alert.AlertType.ERROR, "Please select a card");
        }
    }

    @FXML
    void takeRowOnClick(Rectangle selectedRowRectangle) {
        int selectedRow = 0;

        try {
            selectedRow = returnNodeRowIndex(selectedRowRectangle);
        } catch (Exception e) {
            System.out.println("takeRowOnClick *FUNCTION* -> No row selected");
            System.out.println(e);
            createPopup(stage, Alert.AlertType.ERROR, "Please select a row");
        }

        removeCardsFromBoard(selectedRow);
    }

    private void removeCardsFromBoard(int selectedRow) {
        List<Card> cardsOnBoardRow = returnCardsOnBoardRow(selectedRow);
        // GAME LOGIC STUFF
        currentCharacter.getTakenCardsList().addAll(cardsOnBoardRow);
        deleteCardsOnBoardRow(selectedRow);
        calculateCharactersPoints();

        // VISUAL AND ANIMATION STUFF
        updateCharacterInfoGridPane(currentCharacter);
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
                }

                if (cardsPlaced < maxCardsPerRow * 2) takenCardsGridPane.add(cardRectangle2, column.get(), row.get());

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

        characters.getCharactersList().forEach(character -> {
            AtomicInteger pointsToRemove = new AtomicInteger();

            character.getTakenCardsList().forEach(card -> {
                int newPoints = character.getPoints() - card.getCardHeads();
                character.setPoints(newPoints);

                pointsToRemove.addAndGet(-card.getCardHeads());

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
            fadeOutEffect(pointsToRemoveText, 1, 0).setOnFinished((e2) -> {
                characterInfoGridPane.getChildren().remove(pointsToRemoveText);
            });
        });

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

    private Card returnCharacterCardByNumber(AbstractCharacter abstractCharacter, String cardNumber) {
        return abstractCharacter.getCardsList().stream().filter(card -> card.getCardNumber() == Integer.parseInt(cardNumber)).findFirst().orElse(null);
    }

    private void characterTurn(int playerNumber) {
        currentCharacter = characters.getCharactersList().get(playerNumber);

        displayCharacterCards(currentCharacter);
        displayCharacterInfo(currentCharacter);

        if(currentCharacter.getClass().equals(Npc.class)) {
//            Card chosenCard = currentCharacter.getCardsList().get((int) generateDoubleBetween(0, currentCharacter.getCardsList().size() - 1));
            System.out.println(currentCharacter.getCharacterName() + "'s TURN");
            System.out.println("NPC CARDS: ");
            currentCharacter.getCardsList().forEach(card -> System.out.print(card.getCardNumber() + ", "));
            System.out.println();
            Card chosenCard = returnClosestCard();

            Rectangle chosenRectangle = (Rectangle) characterCardsGridPane.lookup("#" + chosenCard.getCardNumber());

            chooseCardOnClick(chosenRectangle);
            System.out.println("------------------");
        }
    }

    private Card returnClosestCard() {
        Card[][] localBoard = board.getBoard();
        RowResults rowResults;
        Card bestCard = null;
        int bestCardNumberDifference = 100;
        int bestCardRow = -1;
        int bestCardColumn = -1;

        for(int row = 0; row < localBoard.length; row ++) {
            rowResults = getRowResults(localBoard, row);

            for(Card card : currentCharacter.getCardsList()) {
                RowCalculations rowCalculations = getRowCalculations(bestCardNumberDifference, rowResults, card.getCardNumber());

                if(rowCalculations.isCurrentBigger() && rowCalculations.isDifferenceSmaller()) {
                    bestCardNumberDifference = rowCalculations.currentCardNumberDifference();
                    bestCard = card;
                    bestCardRow = row;
                    bestCardColumn = rowResults.rowTakenLength();
                }
            }
        }

        assert bestCard != null;
        System.out.println("returnClosestCard *FUNCTION* BEST CARD: " +
                bestCard.getCardNumber() +
                " On row " + bestCardRow +
                " And column " + bestCardColumn);

        return bestCard;
    }

    private void displayCharacterCards(AbstractCharacter abstractCharacter) {
        List<Card> characterCards = abstractCharacter.getCardsList();
        characterCardsGridPane.getChildren().clear();

        for (int i = 0; i < characterCards.size(); i++) {
            Card card = characterCards.get(i);

            String cardImage = card.getCardImage();
            if(abstractCharacter.getClass().equals(Npc.class)) {
                cardImage = "backside.png";
            }

            Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, "cards/" + cardImage);
            imageRectangle.setId(String.valueOf(card.getCardNumber()));

            if(abstractCharacter.getClass().equals(Player.class)) {
                imageRectangle.getStyleClass().add("clickableNode");
                imageRectangle.setOnMouseReleased(event -> chooseCardOnClick(imageRectangle));
            }

            characterCardsGridPane.add(imageRectangle, i, 0);
        }
    }

    private void displayCharacterInfo(AbstractCharacter abstractCharacter) {
        playerCardsT.setText(abstractCharacter.getCharacterName() + "'s Cards");
        playerTurnT.setText(abstractCharacter.getCharacterName() + "'s Turn");
    }

    private void displayCharacterInfo(String playerCardsText, String playerTurnText) {
        playerCardsT.setText(playerCardsText);
        playerTurnT.setText(playerTurnText);
    }

    private void initializeBoard() {
        board = new Board(new Card[4][6]);

        for (Card[] cardArray : board.getBoard()) {
            Card randomCard = returnRandomCard(deck);
            cardArray[0] = randomCard;
            deck.getCardsList().remove(randomCard);
        }
    }

    private void displayBoard() {
        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                try {
                    Card card = localBoard[row][column];
                    Rectangle imageRectangle = returnImageRectangle(90, 140, 10,
                            10, "cards/" + card.getCardImage());
                    imageRectangle.setId(String.valueOf(card.getCardNumber()));

                    gameBoardGridPane.add(imageRectangle, column, row);
                } catch (NullPointerException ignored) {}

            }
        }
    }

    /**
     * Initialize the characters cards
     */

    public void initializeCharacterCards() {
        if(deck.getVariantNumber() != 2 && deck.getVariantNumber() != 3) {
            characters.getCharactersList().forEach(character -> {
                for (int i = 1; i <= 10; i++) {
                    giveCard(character.getCardsList());
                }
            });

            characters.getCharactersList()
                    .forEach(character -> character.getCardsList().sort(Comparator.comparingInt(Card::getCardNumber)));
        }
    }

    private void giveCard(List<Card> abstractCharacterCardList) {
        Card randomCard = returnRandomCard(deck);
        abstractCharacterCardList.add(randomCard);
        deck.getCardsList().remove(randomCard);
    }

    /**
     * Initialize the scoreboard GridPane
     */
    public void initializeScoreBoard() {
        scoreBoardGridPane = new GridPane();

        scoreBoardGridPane.setAlignment(Pos.TOP_CENTER);
        scoreBoardGridPane.setVgap(10);
    }

    private void initializeChosenCardsInfoGridPane() {
        Rectangle tableImageRectangle = returnImageRectangle(931, 630, 40, 40, "game/tableBackground.png");
        cardsInfoGridPane.add(tableImageRectangle, 0, 0);
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
        characterCardsGridPane = new GridPane();

        characterCardsGridPane.setAlignment(Pos.TOP_LEFT);
        GridPane.setMargin(characterCardsGridPane, new Insets(5, 5, 5, 5));

        characterCardsGridPane.setHgap(5);

        characterCardsInfoGridPane.add(characterCardsGridPane, 0, 1);
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


    private void updateCharacterInfoGridPane(AbstractCharacter abstractCharacter) {
        Text characterPointsT = (Text) scoreBoardGridPane.lookup("#" + abstractCharacter.getCharacterName() + "Points");
        characterPointsT.setText(abstractCharacter.getPoints() + "⭐");
    }
}
