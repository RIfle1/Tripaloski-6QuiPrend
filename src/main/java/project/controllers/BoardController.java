package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static AbstractCharacter currentCharacter;
    @FXML
    private GridPane chosenCardsInfoGridPane;
    @FXML
    private GridPane gameBoardGriPane;
    @FXML
    private GridPane gameStatsGridPane;
    @FXML
    private GridPane playerCardsInfoGridPane;
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
        unmodifiableDeck = new Deck(variantNumber, playerNumberParam, npcNumberParam);

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

    @FXML
    void chooseCardOnClick(MouseEvent event) {
        try {
            Rectangle selectedCardRectangle = (Rectangle) returnSelectedNodes(playerCardsGridPane, "clickableNodePressed").get(1);
            Card selectedCard = returnCharacterCardByNumber(currentCharacter, selectedCardRectangle.getId());

            currentCharacter.getCardsList().remove(selectedCard);

            Rectangle selectedCardRectangle2 = returnImageRectangle(100, 150, 10, 10, "cards/backside.png");
            selectedCardRectangle2.setId(selectedCardRectangle.getId());

            assert selectedCard != null;
            int columnIndex = chosenCardsGridPane.getChildren().size();
            int rowIndex = (int) ((double) columnIndex / 5);
            if(columnIndex >= 5) columnIndex -= 5;

            int finalColumnIndex = columnIndex;
            animateCardTranslation(selectedCardRectangle, chosenCardsGridPane,
                    () -> {
                        playerCardsGridPane.getChildren().remove(selectedCardRectangle);
                        chosenCardsGridPane.add(selectedCardRectangle2, finalColumnIndex, rowIndex);
                    });

        } catch (Exception e) {
            System.out.println("chooseCardOnClick *FUNCTION* -> No card selected");
            ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());
            createPopup(actionEvent, Alert.AlertType.ERROR, "Please select a card");
        }
    }

        public Card returnCharacterCardByNumber(AbstractCharacter abstractCharacter, String cardNumber) {
        return abstractCharacter.getCardsList()
                .stream()
                .filter(card -> card.getCardNumber() == Integer.parseInt(cardNumber)).findFirst().orElse(null);
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

        displayScoreBoard();

        initializeBoard();
        displayBoard();

        playerTurn();
    }

    private void playerTurn() {
        currentCharacter = characterList.getCharacters().get(0);

        displayCharacterCards(currentCharacter);
        displayPlayerInfo(currentCharacter);

//        characterList.getCharacters().forEach(character -> {
//            displayCharacterCards(character);
//        });
    }

    private void displayCharacterCards(AbstractCharacter abstractCharacter) {
        List<Card> characterCards = abstractCharacter.getCardsList();
        playerCardsGridPane = new GridPane();

        playerCardsGridPane.setAlignment(Pos.TOP_LEFT);
        GridPane.setMargin(playerCardsGridPane, new Insets(0, 0, 0, 5));

        playerCardsGridPane.setHgap(5);

        for (int i = 0; i < characterCards.size(); i++) {
            Card card = characterCards.get(i);

            Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, "cards/" + card.getCardImage());
            imageRectangle.getStyleClass().add("clickableNode");
            imageRectangle.setId(String.valueOf(card.getCardNumber()));

            imageRectangle.setOnMouseReleased(event -> selectNode(playerCardsGridPane, imageRectangle));

            playerCardsGridPane.add(imageRectangle, i, 0);
        }

        playerCardsInfoGridPane.add(playerCardsGridPane, 0, 1);
    }

    private void displayPlayerInfo(AbstractCharacter abstractCharacter) {
        playerCardsT.setText(abstractCharacter.getCharacterName() + "'s Cards");
        playerTurnT.setText(abstractCharacter.getCharacterName() + "'s Turn");
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

                    gameBoardGriPane.add(imageRectangle, column, row);
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

        GridPane.setMargin(chosenCardsGridPane, new Insets(5, 0, 5, 0));
        chosenCardsGridPane.setAlignment(Pos.CENTER);
        chosenCardsGridPane.setHgap(5);
        chosenCardsGridPane.setVgap(10);

        chosenCardsInfoGridPane.add(chosenCardsGridPane, 0, 0);
    }

    /**
     * Display the scoreboard
     */
    private void displayScoreBoard() {
        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList(returnTargetPath("images/icons"));

        characterList.getCharacters().forEach(character -> {
            returnCharacterInfoGrid(rowNumber, imageList, character);
            rowNumber.getAndIncrement();
        });

        gameStatsGridPane.getChildren().remove(scoreBoardGridPane);
        gameStatsGridPane.add(scoreBoardGridPane, 1, 1);
    }

    /**
     * Returns Character Info Grid Pane for the scoreboard
     *
     * @param rowNumber         Row Number
     * @param imageList         Image List
     * @param abstractCharacter Abstract Character
     */
    private void returnCharacterInfoGrid(AtomicInteger rowNumber, ArrayList<String> imageList, AbstractCharacter abstractCharacter) {
        Text playerName = new Text(abstractCharacter.getCharacterName());
        playerName.getStyleClass().add("playerInfoTextStyle1");

        Text playerPoints = new Text(abstractCharacter.getPoints() + "⭐");
        playerPoints.getStyleClass().add("playerInfoTextStyle2");

        String imageName = imageList.get((int) generateDoubleBetween(0, imageList.size() - 1));
        if (imageList.size() > 1) imageList.remove(imageName);

        Rectangle imageRectangle = returnImageRectangle(50, 50,
                50, 50, "icons/" + imageName);
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

        scoreBoardGridPane.add(horizontalGridPane, 0, rowNumber.get());
    }


}
