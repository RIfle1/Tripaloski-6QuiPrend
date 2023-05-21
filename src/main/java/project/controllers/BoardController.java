package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import project.abstractClasses.AbstractCharacter;
import project.classes.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static project.classes.Card.returnRandomCard;
import static project.functions.GeneralFunctions.*;
import static project.functions.JavaFxFunctions.*;

/**
 * Board Controller
 */
public class BoardController implements Initializable {
    private static Deck deck;
    private static Board board;
    private static List<Player> playerList = new ArrayList<>();
    private static List<Npc> npcList = new ArrayList<>();

    private static int playerNumber;
    private static int npcNumber;
    // Variant 0 => No Modifications to the game
    // Variant 1 => Max cards based on number of players
    // Variant 2 => Players can choose their cards by turn
    // Variant 3 => Variant 1 + Variant 2
    private static int variantNumber;
    private static int roundNumber;
    private static int startingPoints;
    private static GridPane scoreBoardGridPane = new GridPane();

    @FXML
    private GridPane chosenCardsGridPane;
    @FXML
    private GridPane gameBoardGriPane;
    @FXML
    private GridPane gameStatsGridPane;
    @FXML
    private GridPane playerCardsGridPane;
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
     * @param playerListParam    List of players (Optional)
     * @param npcListParam       List of NPCs (Optional)
     */

    public static void boardScene(MouseEvent event, int playerNumberParam, int npcNumberParam, int variantNumberParam, int roundNumberParam, int startingPointsParam, List<Player> playerListParam, List<Npc> npcListParam) {
        playerNumber = playerNumberParam;
        npcNumber = npcNumberParam;
        variantNumber = variantNumberParam;
        playerList = playerListParam;
        npcList = npcListParam;
        roundNumber = roundNumberParam;
        startingPoints = startingPointsParam;

        deck = new Deck(variantNumber, playerNumber, npcNumber);

        FXMLLoader characterCreationFxmlLoader = new FXMLLoader(returnFXMLURL("Board.fxml"));
        ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());
        sendToScene(actionEvent, characterCreationFxmlLoader);
    }

    /**
     * Initialize the game
     *
     * @param url            URL
     * @param resourceBundle Resource Bundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (variantNumber == 0 || variantNumber == 1) initializeCharacters();

        initializeScoreBoard();
        displayScoreBoard();

        initializeBoard();
        displayBoard();
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
//        scoreBoardGridPane

        Card[][] localBoard = board.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                try {
                    Card card = localBoard[row][column];
//                    System.out.println("cards/" + card.getCardImage() + " Row: " + row + " Column: " + column);
//                    ImageView cardImageView = returnObjectImageView("cards/" + card.getCardImage(), 130, 80, 1);
                    Rectangle imageRectangle = returnImageRectangle(90, 140, 10, 10, "cards/" + card.getCardImage());
                    imageRectangle.setId(String.valueOf(card.getCardNumber()));
                    gameBoardGriPane.add(imageRectangle, column, row);
                }
                catch (NullPointerException e) {
                    System.out.println("No card on Row: " + row + " Column: " + column);
                }

            }
        }
    }

    /**
     * Initialize the characters
     */

    public void initializeCharacters() {
        playerList = Player.initializePlayers(playerNumber, startingPoints);
        npcList = Npc.initializeNpcs(npcNumber, startingPoints);

        initializeCharacterCards();
    }

    /**
     * Initialize the characters cards
     */

    public void initializeCharacterCards() {
        playerList.forEach(player -> {
            for (int i = 1; i <= 10; i++) {
                giveCard(player.getCardsList());
            }
        });

        npcList.forEach(npc -> {
            for (int i = 1; i <= 10; i++) {
                giveCard(npc.getCardsList());
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
        scoreBoardGridPane.setAlignment(Pos.TOP_CENTER);
        scoreBoardGridPane.setVgap(10);

        ColumnConstraints scoreBoardGridPaneColumn1 = new ColumnConstraints();

        scoreBoardGridPaneColumn1.setPrefWidth(290);
        scoreBoardGridPaneColumn1.setMaxWidth(Region.USE_PREF_SIZE);
        scoreBoardGridPaneColumn1.setMinWidth(Region.USE_PREF_SIZE);

        scoreBoardGridPane.getColumnConstraints().add(scoreBoardGridPaneColumn1);
    }

    /**
     * Display the scoreboard
     */
    private void displayScoreBoard() {
        AtomicInteger rowNumber = new AtomicInteger(0);
        ArrayList<String> imageList = returnImageList(returnTargetPath("images/icons"));


        playerList.forEach(player -> {
            returnCharacterInfoGrid(rowNumber, imageList, player);
            rowNumber.getAndIncrement();
        });

        npcList.forEach(npc -> {
            returnCharacterInfoGrid(rowNumber, imageList, npc);
            rowNumber.getAndIncrement();
        });

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
                50,50, "icons/" + imageName);
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
