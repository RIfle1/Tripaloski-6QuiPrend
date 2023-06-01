package project.controllers;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.abstractClasses.AbstractCharacter;
import project.classes.Card;
import project.classes.Characters;
import project.classes.Deck;
import project.classes.Npc;
import project.enums.Difficulty;
import project.enums.Variant;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static project.controllers.BoardController.boardScene;
import static project.controllers.MainMenuController.mainMenuScene;
import static project.functions.JavaFxFunctions.*;

public class ChooseCardsController implements Initializable {
    private static Stage stage;
    private static Characters characters;
    private static Deck deck;
    private static Difficulty difficulty;
    private static AbstractCharacter currentCharacter;
    private static GridPane chooseCardGridPane;
    private static int roundNumber;
    private static int roundNumberCap;
    private static boolean sentToBoard;
    @FXML
    private GridPane chooseCardInfoGridPane;
    @FXML
    private Text characterTurnT;

    /**
     * This method is used to send the user to the ChooseCards scene
     *
     * @param event Since this method is called in a mouse event, this parameter is needed
     * @param playersNumber The number of players
     * @param npcNumber The number of NPCs
     * @param variant The variant of the game
     * @param roundNumberParam The number of rounds
     * @param startingPoints The starting points
     * @param difficultyParam The difficulty of the NPCs
     */
    public static void chooseCardsScene(MouseEvent event,
                                        int playersNumber, int npcNumber, Variant variant,
                                        int roundNumberParam, int startingPoints, Difficulty difficultyParam) {
        deck = new Deck(variant, playersNumber, npcNumber);
        characters = new Characters(playersNumber, npcNumber, startingPoints);
        roundNumber = roundNumberParam;
        difficulty = difficultyParam;
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        chooseCardsSceneSub(stage, roundNumber);
    }

    /**
     * This method is used to let the players choose their cards again when a round is over in the main game
     *
     * @param stageParam The stage
     * @param deckParam The deck
     * @param charactersParam The characters
     */
    public static void chooseCardsScene(Stage stageParam, Deck deckParam, Characters charactersParam,
                                        int roundNumberParam, int roundNumberCapParam, Difficulty difficultyParam) {
        deck = deckParam;
        characters = charactersParam;
        roundNumber = roundNumberParam;
        difficulty = difficultyParam;
        stage = stageParam;

        chooseCardsSceneSub(stageParam, roundNumberCapParam);
    }

    /**
     * Sub method for chooseCardsScene
     */
    private static void chooseCardsSceneSub(Stage stage, int roundNumberCapParam) {
        roundNumberCap = roundNumberCapParam;

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("ChooseCards.fxml"));
        Scene scene = sendToScene(stage, fxmlLoader);
        scene.setOnKeyPressed(e -> onExitKeyPressed(e, ChooseCardsController::exit));
    }

    /**
     * Initializes the ChooseCards scene
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeChooseCardGridPane();
        displayChooseCardsGridPane();

        sentToBoard = false;
        characterTurn(0);
    }

    /**
     * Exit Method
     */
    private static void exit() {
        String msg = "Are you sure you want to return to the Game Menu?";
        if (checkConfirmationPopUp(stage, msg)) mainMenuScene(stage);
    }

    /**
     * Initialize ChooseCardGridPane
     */
    private void initializeChooseCardGridPane() {
        chooseCardGridPane = new GridPane();
        chooseCardGridPane.setAlignment(Pos.CENTER);

        chooseCardGridPane.setHgap(10);
        chooseCardGridPane.setVgap(10);

        chooseCardGridPane.setPadding(new Insets(10, 10, 10, 10));

        chooseCardInfoGridPane.add(chooseCardGridPane, 0, 1);
    }

    /**
     * Display the cards from the deck on the screen
     */
    private void displayChooseCardsGridPane() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        int maxColumns = 17;

        deck.getCardsList().forEach(card -> {
            String imagePath = returnImagePath("cards/" + card.getCardImage());
            Rectangle cardRectangle = returnImageRectangle(80, 120, 10, 10, imagePath);
            cardRectangle.setId(String.valueOf(card.getCardNumber()));
            cardRectangle.getStyleClass().add("clickableNode");

            cardRectangle.setOnMouseReleased(e -> {
                chooseCardOnClick(card, cardRectangle);
            });

            if(column.get() > maxColumns) {
                row.getAndIncrement();
                column.set(0);
            }

            chooseCardGridPane.add(cardRectangle, column.get(), row.get());
            column.getAndIncrement();
        });
    }

    /**
     * Choose card function
     *
     * @param card The card that is chosen
     * @param cardRectangle The rectangle of the card
     */
    private void chooseCardOnClick(Card card, Rectangle cardRectangle) {
        currentCharacter.getCardsList().add(card);
        deck.getCardsList().remove(card);
        fadeOutEffect(cardRectangle, 0.1, 0.2).setOnFinished(event -> {
            checkCharactersCards();
            if(!sentToBoard) characterTurn(characters.getCharactersList().indexOf(currentCharacter) + 1);
        });
        cardRectangle.setDisable(true);
        cardRectangle.getStyleClass().remove("clickableNode");
        cardRectangle.getStyleClass().add("clickableNodePressed");
    }

    /**
     * Method for each character to choose a card
     * @param characterIndex The index of the character
     */
    private void characterTurn(int characterIndex) {
        if(characterIndex > characters.getCharactersList().size() - 1) {
            characterIndex = 0;
        }

        currentCharacter = characters.getCharactersList().get(characterIndex);
        characterTurnT.setText(currentCharacter.getCharacterName() + "'s Turn To Choose A Card");

        if(currentCharacter instanceof Npc) {
            disableAllGridPaneButtons(chooseCardGridPane);
            Card chosenCard = deck.returnHighestCard();

            if(difficulty.equals(Difficulty.EASY) || difficulty.equals(Difficulty.MEDIUM)) {
                chosenCard = deck.returnRandomCard();
            }

            Rectangle highestCardRectangle = (Rectangle) chooseCardInfoGridPane.lookup("#" + chosenCard.getCardNumber());

            chooseCardOnClick(chosenCard, highestCardRectangle);

            enableAllGridPaneButtons(chooseCardGridPane);
        }

    }

    /**
     * Check if all characters have max amount of cards
     */
    private void checkCharactersCards() {

        if(allCharactersHaveCards()) {
            sentToBoard = true;
            System.out.println("sent to board");
            boardScene(stage, deck, characters, roundNumber, roundNumberCap, difficulty);
        }
    }

    /**
     * Checks if the character has 10 cards
     *
     * @param character The character
     * @return True if the character has 10 cards, false otherwise
     */
    private boolean isCharacterCardsListFull(AbstractCharacter character) {
        return character.getCardsList().size() == 10;
    }

    private boolean allCharactersHaveCards() {
        boolean allCharactersHaveCards = false;

        for(AbstractCharacter character : characters.getCharactersList()) {
            allCharactersHaveCards = isCharacterCardsListFull(character);
        }

        return allCharactersHaveCards;
    }
}
