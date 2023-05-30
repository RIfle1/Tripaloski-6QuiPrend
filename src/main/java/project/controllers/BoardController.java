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
import project.abstractClasses.AbstractCharacter;
import project.classes.*;
import project.enums.Difficulty;
import project.enums.Variant;

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
    private static Difficulty difficulty;
    // Variant 0 => No Modifications to the game
    // Variant 1 => Max cards based on number of players
    // Variant 2 => Players can choose their cards by turn
    // Variant 3 => Variant 1 + Variant 2
    private static int roundNumber;
    private static GridPane scoreBoardGridPane;
    private static GridPane characterCardsGridPane;
    private static GridPane takenCardsGridPane;
    private static GridPane chosenCardsGridPane;
    private static ArrayList<Card> chosenCardsList;
    private static AbstractCharacter currentCharacter;
    private static Card currentCard;
    private static boolean notExited = true;
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
     * Function used by the main menu controller to send to the board scene
     *
     * @param event             MouseEvent
     * @param playerNumberParam Number of players
     * @param npcNumberParam    Number of NPCs
     * @param variantParam      Variant Number
     */
    public static void boardScene(MouseEvent event, int playerNumberParam, int npcNumberParam, Variant variantParam, int roundNumberParam, int startingPointsParam, Difficulty difficultyParam) {
        roundNumber = roundNumberParam;
        deck = new Deck(variantParam, playerNumberParam, npcNumberParam);
        characters = new Characters(playerNumberParam, npcNumberParam, startingPointsParam);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        boardSceneSub(stage, roundNumberParam, difficultyParam);
    }

    /**
     * Function used by the choose card controller to send to the board scene
     *
     * @param stageParam Stage
     * @param deckParam Deck
     * @param charactersParam Characters
     * @param roundNumberParam Number of rounds
     * @param difficultyParam Difficulty
     */
    public static void boardScene(Stage stageParam, Deck deckParam, Characters charactersParam, int roundNumberParam, Difficulty difficultyParam) {
        roundNumber = roundNumberParam;
        characters = charactersParam;
        deck = deckParam;
        stage = stageParam;


        boardSceneSub(stageParam, roundNumberParam, difficultyParam);
    }

    /**
     * Common board scene sub function
     *
     * @param stage Stage
     * @param roundNumberParam Number of Rounds
     * @param difficultyParam Difficulty
     */
    private static void boardSceneSub(Stage stage, int roundNumberParam, Difficulty difficultyParam) {
        roundNumber = roundNumberParam;
        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("Board.fxml"));
        difficulty = difficultyParam;
        notExited = true;
        chosenCardsList = new ArrayList<>();

        Scene scene = sendToScene(stage, fxmlLoader);

        scene.setOnKeyPressed(e -> onExitKeyPressed(e, BoardController::exit));
    }

    /**
     * Exit Function
     */
    private static void exit() {
        String msg = "Are you sure you want to return to the Game Menu? All progress will be lost.";
        if (checkConfirmationPopUp(stage, msg)) {
            mainMenuScene(stage);
            notExited = false;
        }
    }

    /**
     * Checks if the newBestCard is better than the old best card
     *
     * @param bestCardsList List of best cards
     * @param newBestCard New best card
     * @return boolean
     */
    private static boolean replaceBestCard(List<BestCard> bestCardsList, BestCard newBestCard) {
        boolean doesCardExist = bestCardsList.stream().anyMatch(bestCard -> bestCard.getCard().equals(newBestCard.getCard()));
        if (doesCardExist) {
            BestCard oldBestCard = bestCardsList
                    .stream().filter(bestCard -> bestCard.getCard().equals(newBestCard.getCard())).findFirst().orElse(null);

            assert oldBestCard != null;
            boolean isNewCardBetter = newBestCard.getNumberDifference() < oldBestCard.getNumberDifference();
            if (isNewCardBetter) {
                bestCardsList.remove(oldBestCard);
            }

            return isNewCardBetter;
        } else {
            return true;
        }
    }

    /**
     * Calculates the number difference between a staticCard and a new Card
     *
     * @param bestCardNumberDifference Previous best card number difference
     * @param newCardNumber New card number
     * @param staticCardNumber Static card number
     * @return RowCalculations
     */
    private RowCalculations getRowCalculations(int bestCardNumberDifference, int newCardNumber, int staticCardNumber) {
        int currentCardNumberDifference = Math.abs(staticCardNumber - newCardNumber);
        boolean isCurrentBigger = newCardNumber < staticCardNumber;
        boolean isCurrentSmaller = newCardNumber > staticCardNumber;
        boolean isDifferenceSmaller = currentCardNumberDifference < bestCardNumberDifference;
        return new RowCalculations(currentCardNumberDifference, isCurrentSmaller, isCurrentBigger, isDifferenceSmaller);
    }

    /**
     * Calculates how long each row is and which card has been placed last
     *
     * @param localBoard Board
     * @param row Row
     * @return RowResults
     */
    private RowResults getRowResults(Card[][] localBoard, int row) {
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

    /**
     * Initialize the game
     *
     * @param url            URL
     * @param resourceBundle Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        characters.initializeCards(deck);

        initializeScoreBoard();
        initializeChosenCardsInfoGridPane();
        initializeChosenCardsGridPane();
        initializeCharacterCardsGridPane();
        initializeTakenCardsGridPane();

        displayScoreBoard(scoreBoardInfoGridPane, scoreBoardGridPane, characters, 0, 1, 10);

        initializeBoard();
        displayBoard();
        setBoardRowsOnHover();

        characterTurn(0);
    }



    /**
     * Resolves the cards in the chosen cards list and calls the function
     * again until all the cards in the chosen cards list are gone
     */
    public void resolveCards() {
        if (notExited) {
            displayNextTurnInfo("Cards Resolution");
            chosenCardsList = sortCardsByIncreasingOrder(chosenCardsList);

            Card[][] localBoard = board.getBoard();

            currentCard = chosenCardsList.get(0);
            currentCharacter = characters.findCharacterByCard(currentCard);

            int bestCardNumberDifference = currentCard.getCardNumber();
            int cardNumber = currentCard.getCardNumber();
            int bestRow = -1;
            int bestColumn = -1;

            for (int row = 0; row < localBoard.length; row++) {

                RowResults rowResults = getRowResults(localBoard, row);
                RowCalculations rowCalculations = getRowCalculations(bestCardNumberDifference, rowResults.lastRowCardNumber, cardNumber);

//                System.out.println(currentCharacter.getCharacterName() +
//                        " | Card Number: " + cardNumber +
//                        " | Last Row Card Number: " + rowResults.lastRowCardNumber() +
//                        " | currentDifference: " + rowCalculations.currentCardNumberDifference() +
//                        " | bestDifference: " + bestCardNumberDifference +
//                        " | isCurrentBigger: " + rowCalculations.isCurrentBigger() +
//                        " | isDifferenceSmaller: " + rowCalculations.isDifferenceSmaller() +
//                        " | Row: " + row +
//                        " | Column: " + rowResults.rowTakenLength());

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

                if (currentCharacter instanceof Player) {
                    playerTurnText += ", please choose the row you want to take.";
                    setBoardRowsOnHover();
                    enableAllGridPaneButtons(gameBoardGridPane);
                } else if (currentCharacter instanceof Npc) {
                    playerTurnText += ", NPC will choose the row to take.";
                    int bestRowToTake = returnBestRowToTake();

                    if (difficulty.equals(Difficulty.EASY) || difficulty.equals(Difficulty.MEDIUM)) {
                        bestRowToTake = (int) generateDoubleBetween(0, 3);
                    }
                    removeCardsFromBoard(bestRowToTake);
                }
                displayNextTurnInfo(playerTurnText);
            }
            System.out.println("----------");
        }
    }

    /**
     * Checks which row has the least amount of heads
     *
     * @return the row with the least amount of heads
     */
    private int returnBestRowToTake() {

        int bestRow = -1;
        int leastRowHeads = 100;

        for (int row = 0; row < board.getBoard().length; row++) {
            int rowHeads = 0;

            for (int column = 0; column < board.getBoard()[row].length; column++) {
                if (board.getBoard()[row][column] != null) {
                    rowHeads += board.getBoard()[row][column].getCardHeads();
                }
            }

            if (rowHeads < leastRowHeads) {
                bestRow = row;
                leastRowHeads = rowHeads;
            }
        }

        System.out.println("Best Row: " + bestRow + " Least Row Heads: " + leastRowHeads);

        return bestRow;
    }

    /**
     * Checks if the game has ended by reducing the number of rounds on each iteration
     * If the game ended, displays the end game scene
     */
    private void checkEndGame() {
        roundNumber--;

        if (isEndGame()) {
            disableAllGridPaneButtons(gameBoardGridPane);
            disableAllGridPaneButtons(characterCardsGridPane);

            displayNextTurnInfo("Game Ended");

            endGameScene(stage);
        }
    }

    /**
     * Checks if the game has ended by round number or by characters with no points
     *
     * @return true if the game has ended, false otherwise
     */
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

    /**
     * Checks which characters have no points and adds them to a list
     *
     * @return a list of characters with no points
     */
    private List<AbstractCharacter> returnCharactersWithNoPoints() {
        List<AbstractCharacter> charactersWithNoPoints = new ArrayList<>();

        for (AbstractCharacter character : characters.getCharactersList()) {
            if (character.getPoints() < 0) {
                charactersWithNoPoints.add(character);
            }
        }

        return charactersWithNoPoints;
    }

    /**
     * Resolves the chosen card by adding it to the board and removing it from the chosen cards list
     *
     * @param bestRow    the best row to place the card
     * @param bestColumn the best column to place the card
     */
    private void resolveChosenCard(int bestRow, int bestColumn) {
        board.getBoard()[bestRow][bestColumn] = currentCard;

        currentCharacter = characters.findCharacterByCard(currentCard);
        currentCharacter.getCardsList().remove(currentCard);
        chosenCardsList.remove(currentCard);

        if (chosenCardsList.size() > 0) {
            resolveCards();
        } else {
            checkEndGame();
            if (!isEndGame()) characterTurn(0);
        }

    }

    /**
     * Animates the card from the chosen cards list and places it on the board
     *
     * @param card         the card to be placed on the board
     * @param bestRow      the best row to place the card
     * @param bestColumn   the best column to place the card
     * @param runnableFunc the function to be run after the animation is finished
     */
    private void resolveChosenCardAnimation(Card card, int bestRow, int bestColumn, Runnable runnableFunc) {
        if (notExited) {
            try {
                Rectangle cardRectangle = (Rectangle) returnChildNodeById(chosenCardsGridPane, String.valueOf(card.getCardNumber()));

                String imagePath = returnImagePath("cards/" + card.getCardNumber() + ".png");
                Rectangle selectedCardRectangle2 = returnImageRectangle(90, 140, 10, 10, imagePath);
                selectedCardRectangle2.setId(cardRectangle.getId());

                Rectangle targetRectangle = (Rectangle) getNullIdNodeByRowColumnIndex(bestRow, bestColumn, gameBoardGridPane);

                List<Timeline> timelineList = animateCardTranslation(cardRectangle, targetRectangle);
                timelineList.get(0).setOnFinished(e -> {
                    gameBoardGridPane.add(selectedCardRectangle2, bestColumn, bestRow);
                    chosenCardsGridPane.getChildren().remove(cardRectangle);
                    runnableFunc.run();
                });

                timelineList.forEach(Timeline::play);
            } catch (Exception e) {
                System.out.println("resolveChosenCardAnimation *FUNCTION* Program stopped / cardRectangle null");
            }
        }
    }

    /**
     * Function that selects the whole row when the mouse hover over one card in the row
     */
    private void setBoardRowsOnHover() {
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

    /**
     * When the mode enters the node, it gets selected
     *
     * @param node The node to be selected
     */
    private void onMouseEntered(Rectangle node) {
        node.getStyleClass().remove("clickableNodeS");
        node.getStyleClass().add("clickableNodePressedS");
    }

    /**
     * When the mouse exists the node, it gets unselected
     *
     * @param node The node to be unselected
     */
    private void onMouseExited(Rectangle node) {
        node.getStyleClass().remove("clickableNodePressedS");
        node.getStyleClass().add("clickableNodeS");
    }

    /**
     * Selects a node permanently
     *
     * @param node The node to be selected
     */
    private void permanentSelectFunction(Rectangle node) {
        node.getStyleClass().add("clickableNodePressedS");
    }

    /**
     * Sets a function to be executed if the node is in the same row as the nodeRow and the node has no id
     *
     * @param nodeRow The row of the node
     * @param nodeSub The node to be checked
     * @param func    The function to be executed
     */
    private void setSynchronizedFunction(int nodeRow, Rectangle nodeSub, Runnable func) {
        int nodeRowSub = returnNodeRowIndex(nodeSub);

        if (nodeRow == nodeRowSub && nodeSub.getId() == null) {
            func.run();
        }

    }

    /**
     * Sorts cards by their number in an increasing order
     *
     * @param cardsList The list of cards to be sorted
     * @return The sorted list of cards
     */
    private static ArrayList<Card> sortCardsByIncreasingOrder(List<Card> cardsList) {
        return cardsList.stream().sorted(Comparator.comparingInt(Card::getCardNumber)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Chooses a card from the character's cards list and adds it to the chosen cards list
     *
     * @param cardNumber The number of the selected card
     */
    private void chooseCardOnClick(String cardNumber) {
        try {
            Rectangle selectedCardRectangle = (Rectangle) returnChildNodeById(characterCardsGridPane, cardNumber);
            Card selectedCard = currentCharacter.returnCharacterCardByNumber(selectedCardRectangle.getId());
            disableAllGridPaneButtons(characterCardsGridPane);

            // ADD THE CARD TO THE CHOSEN CARDS LIST
            chosenCardsList.add(selectedCard);

            // CREATE A NEW CARD RECTANGLE WITH THE BACKSIDE AND ADD IT TO THE CHOSEN CARDS GRID PANE
            String imagePath = returnImagePath("cards/backside.png");
            Rectangle selectedCardRectangle2 = returnImageRectangle(90, 140, 10, 10, imagePath);
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

                    resolveCards();
                } else {
                    characterTurn(cardsPlaced + 1);
                }

            });

            timelineList.forEach(Timeline::play);

        } catch (Exception e) {
            System.out.println("chooseCardOnClick *FUNCTION* -> No card selected");
        }


    }

    /**
     * Removes the cards from the board and adds them to the current character's taken cards list
     *
     * @param selectedRowRectangle One of the rectangles in the row that was selected
     */
    @FXML
    private void takeRowOnClick(Rectangle selectedRowRectangle) {
        int selectedRow = 0;

        try {
            disableAllGridPaneButtons(gameBoardGridPane);
            selectedRow = returnNodeRowIndex(selectedRowRectangle);
        } catch (Exception e) {
            System.out.println("takeRowOnClick *FUNCTION* -> No row selected");
            createPopup(stage, Alert.AlertType.ERROR, "Please select a row");
        }

        removeCardsFromBoard(selectedRow);
    }

    /**
     * Removes the cards from the board and adds them to the current character's taken cards list
     *
     * @param selectedRow the row that was selected
     */
    private void removeCardsFromBoard(int selectedRow) {
        if (notExited) {
            try {
                List<Card> cardsOnBoardRow = board.returnCardsOnBoardRow(selectedRow);
                // GAME LOGIC STUFF
                currentCharacter.getTakenCardsList().addAll(cardsOnBoardRow);
                board.deleteCardsOnBoardRow(selectedRow);
                calculateCharactersPoints();

                // VISUAL AND ANIMATION STUFF
                updateCharacterInfoGridPane(currentCharacter);
                deselectAllSubNodes(gameBoardGridPane);

                AtomicInteger row = new AtomicInteger();
                AtomicInteger column = new AtomicInteger(takenCardsGridPane.getChildren().size());
                int maxCardsPerRow = 36;

                cardsOnBoardRow.forEach(card -> {
                    Rectangle cardRectangle = (Rectangle) returnChildNodeById(gameBoardGridPane, String.valueOf(card.getCardNumber()));

                    List<Timeline> timelineList = animateCardTranslation(cardRectangle, takenCardsGridPane);
                    timelineList.get(0).setOnFinished((actionEvent) -> {
                        gameBoardGridPane.getChildren().remove(cardRectangle);
                        String imagePath = returnImagePath("cards/" + cardRectangle.getId() + ".png");
                        Rectangle cardRectangle2 = returnImageRectangle(90, 140, 10, 10, imagePath);

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

                        if (cardsPlaced < maxCardsPerRow * 2)
                            takenCardsGridPane.add(cardRectangle2, column.get(), row.get());

                        column.getAndIncrement();
                        deselectAllSubNodesS(gameBoardGridPane);
                    });
                    timelineList.forEach(Timeline::play);
                });

                resolveChosenCardAnimation(currentCard, selectedRow, 0, () -> {
                    resolveChosenCard(selectedRow, 0);
                });
            } catch (Exception e) {
                System.out.println("removeCardsFromBoard *FUNCTION* -> No cards on row / Program Stopped / Something is null");
            }
        }
    }

    /**
     * Calculates the points of each character based on the heads of the cards they have taken
     */
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

    /**
     * Displays the points reduction of a character
     *
     * @param abstractCharacter character to display the points reduction
     * @param pointsToRemove    points to remove from the character
     */
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

    /**
     * Next character's turn function
     *
     * @param characterNumber Character's number
     */
    private void characterTurn(int characterNumber) {
        if (notExited) {
            currentCharacter = characters.getCharactersList().get(characterNumber);

            displayCharacterCards(currentCharacter);
            displayCharacterInfo(currentCharacter);

            if (currentCharacter instanceof Npc) {

                System.out.println(currentCharacter.getCharacterName() + "'s TURN");
                System.out.println("NPC CARDS: ");
                currentCharacter.getCardsList().forEach(card -> System.out.print(card.getCardNumber() + ", "));
                System.out.println();
                Card chosenCard = returnBestCard();

                chooseCardOnClick(String.valueOf(chosenCard.getCardNumber()));
                System.out.println("------------------");
            }
        }
    }

    /**
     * Calculates the best card for NPC based on variant
     *
     * @return Card
     */
    private Card returnBestCard() {
        Card[][] localBoard = board.getBoard();
        RowResults rowResults;
        int bestCardNumberDifference = 100;

        List<BestCard> bestCardsList = new ArrayList<>();


        for (int row = 0; row < localBoard.length; row++) {
            rowResults = getRowResults(localBoard, row);

            for (Card card : currentCharacter.getCardsList()) {

                RowCalculations rowCalculations = getRowCalculations(bestCardNumberDifference, rowResults.lastRowCardNumber, card.getCardNumber());

                boolean isBestRow = rowResults.rowTakenLength() < 5;
                if (difficulty.equals(Difficulty.MEDIUM)) isBestRow = true;

                if (rowCalculations.isCurrentBigger() && isBestRow) {
                    bestCardNumberDifference = rowCalculations.currentCardNumberDifference();

                    BestCard bestCard = BestCard.builder()
                            .card(card)
                            .row(row)
                            .numberDifference(bestCardNumberDifference)
                            .column(rowResults.rowTakenLength())
                            .build();

                    if (replaceBestCard(bestCardsList, bestCard)) {
                        bestCardsList.add(bestCard);
                    }
                }
            }
        }

        if (bestCardsList.isEmpty()) {
            int row = returnBestRowToTake();
            int column = getRowResults(localBoard, row).rowTakenLength();

            BestCard bestCard = BestCard.builder()
                    .card(currentCharacter.returnSmallestCard())
                    .row(row)
                    .numberDifference(bestCardNumberDifference)
                    .column(column)
                    .build();


            bestCardsList.add(bestCard);
            System.out.println("bestCardsList is empty - Choosing the smallest card the NPC has");
        }

        bestCardsList.sort(Comparator.comparing(BestCard::getNumberDifference));

        System.out.println("returnBestCard *FUNCTION* BEST CARD: " +
                bestCardsList.get(0).getCard().getCardNumber() +
                " On row " + bestCardsList.get(0).getRow() +
                " And column " + bestCardsList.get(0).getColumn());

        Card bestCard = bestCardsList.get(0).getCard();

        if (difficulty.equals(Difficulty.EASY)) {
            bestCard = currentCharacter.getCardsList()
                    .get((int) generateDoubleBetween(0, currentCharacter.getCardsList().size() - 1));
        }

        return bestCard;
    }

    /**
     * Displays character cards on the screen
     *
     * @param abstractCharacter character to display cards
     */
    private void displayCharacterCards(AbstractCharacter abstractCharacter) {
        List<Card> characterCards = abstractCharacter.getCardsList();
        characterCardsGridPane.getChildren().clear();

        for (int i = 0; i < characterCards.size(); i++) {
            Card card = characterCards.get(i);

            String cardImage = card.getCardImage();
            if (abstractCharacter instanceof Npc) {
                cardImage = "backside.png";
            }
            String imagePath = returnImagePath("cards/" + cardImage);
            Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, imagePath);
            imageRectangle.setId(String.valueOf(card.getCardNumber()));

            if (abstractCharacter instanceof Player) {
                imageRectangle.getStyleClass().add("clickableNode");
            } else if (abstractCharacter instanceof Npc) {
                imageRectangle.getStyleClass().add("clickableNode2");
            }

            characterCardsGridPane.add(imageRectangle, i, 0);
            imageRectangle.setOnMouseReleased(event -> chooseCardOnClick(imageRectangle.getId()));
        }
    }

    /**
     * Display the character info
     *
     * @param abstractCharacter The character to display info for
     */
    private void displayCharacterInfo(AbstractCharacter abstractCharacter) {
        playerCardsT.setText(abstractCharacter.getCharacterName() + "'s Cards");
        playerTurnT.setText(abstractCharacter.getCharacterName() + "'s Turn");
    }

    /**
     * Display the next turn
     *
     * @param nextTurnText The text to display
     */
    private void displayNextTurnInfo(String nextTurnText) {
        playerCardsT.setText("");
        playerTurnT.setText(nextTurnText);
    }

    /**
     * Initialize the board with 4 random cards
     */
    private void initializeBoard() {
        board = new Board(new Card[4][6]);

        for (Card[] cardArray : board.getBoard()) {
            Card randomCard = returnRandomCard(deck);
            cardArray[0] = randomCard;
            deck.getCardsList().remove(randomCard);
        }
    }

    /**
     * Display the cards on the board
     */
    private void displayBoard() {
        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                try {
                    Card card = localBoard[row][column];
                    String imagePath = returnImagePath("cards/" + card.getCardImage());
                    Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, imagePath);
                    imageRectangle.setId(String.valueOf(card.getCardNumber()));

                    gameBoardGridPane.add(imageRectangle, column, row);
                } catch (NullPointerException ignored) {
                }

            }
        }
    }

    /**
     * Initialize the scoreboard GridPane
     */
    public void initializeScoreBoard() {
        scoreBoardGridPane = new GridPane();

        scoreBoardGridPane.setAlignment(Pos.TOP_CENTER);
        scoreBoardGridPane.setVgap(10);
    }

    /**
     * Initialize the chosen cards info GridPane
     */
    private void initializeChosenCardsInfoGridPane() {
        cardsInfoGridPane.getChildren().forEach(node -> {
            if (node instanceof Rectangle) cardsInfoGridPane.getChildren().remove(node);
        });
        String imagePath = returnImagePath("game/tableBackground.png");
        Rectangle tableImageRectangle = returnImageRectangle(931, 630, 40, 40, imagePath);
        cardsInfoGridPane.add(tableImageRectangle, 0, 0);
        tableImageRectangle.toBack();
    }

    /**
     * Initialize the chosen cards GridPane
     */
    private void initializeChosenCardsGridPane() {
        chosenCardsGridPane = new GridPane();

        chosenCardsGridPane.setAlignment(Pos.CENTER);
        GridPane.setMargin(chosenCardsGridPane, new Insets(5, 0, 5, 0));

        chosenCardsGridPane.setHgap(5);
        chosenCardsGridPane.setVgap(10);

        chosenCardsInfoGridPane.add(chosenCardsGridPane, 0, 0);

    }

    /**
     * Initialize the character cards GridPane
     */
    private void initializeCharacterCardsGridPane() {
        characterCardsGridPane = new GridPane();

        characterCardsGridPane.setAlignment(Pos.TOP_LEFT);
        GridPane.setMargin(characterCardsGridPane, new Insets(5, 5, 5, 5));

        characterCardsGridPane.setHgap(5);

        characterCardsInfoGridPane.add(characterCardsGridPane, 0, 1);
    }

    /**
     * Initialize the taken cards GridPane
     */
    private void initializeTakenCardsGridPane() {
        takenCardsGridPane = new GridPane();

        takenCardsGridPane.setAlignment(Pos.CENTER);
        GridPane.setMargin(takenCardsGridPane, new Insets(10, 5, 10, 5));

        takenCardsGridPane.setVgap(20);

        takenCardsInfoGridPane.add(takenCardsGridPane, 0, 0);
    }

    /**
     * Update the character info Grid Pane
     */

    private void updateCharacterInfoGridPane(AbstractCharacter abstractCharacter) {
        Text characterPointsT = (Text) scoreBoardGridPane.lookup("#" + abstractCharacter.getCharacterName() + "Points");
        characterPointsT.setText(abstractCharacter.getPoints() + "⭐");
    }

    /**
     * Constructor to get all row calculations info
     *
     * @param currentCardNumberDifference Current card number difference
     * @param isCurrentSmaller            Is current card smaller
     * @param isCurrentBigger             Is current card bigger
     * @param isDifferenceSmaller         Is difference smaller
     */
    private record RowCalculations(int currentCardNumberDifference, boolean isCurrentSmaller, boolean isCurrentBigger,
                                   boolean isDifferenceSmaller) {
    }

    /**
     * Constructor to get all row results info
     *
     * @param rowTakenLength
     * @param lastRowCardNumber
     */
    private record RowResults(int rowTakenLength, int lastRowCardNumber) {
    }
}
