package project.controllers;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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
import project.classes.Characters;
import project.classes.Deck;

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
    private static AbstractCharacter currentCharacter;
    private static GridPane chooseCardGridPane;
    private static int roundNumber;
    @FXML
    private GridPane chooseCardInfoGridPane;
    @FXML
    private Text characterTurnT;

    public static void chooseCardsScene(MouseEvent event,
                                        int playersNumber, int npcNumber, int variantNumber,
                                        int roundNumberParam, int startingPoints) {
        deck = new Deck(variantNumber, playersNumber, npcNumber);
        characters = new Characters(playersNumber, npcNumber, startingPoints);
        roundNumber = roundNumberParam;

        FXMLLoader fxmlLoader = new FXMLLoader(returnFXMLURL("ChooseCards.fxml"));
        ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = sendToScene(actionEvent, fxmlLoader);

        scene.setOnKeyPressed(e -> onExitKeyPressed(e, ChooseCardsController::exit));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeChooseCardGridPane();
        displayChooseCardsGridPane();

        characterTurn(0);
    }

    static void exit() {
        String msg = "Are you sure you want to return to the Game Menu?";
        if (checkConfirmationPopUp(stage, msg)) mainMenuScene(stage);
    }

    public static void onExitKeyPressed(KeyEvent event, Runnable runnableFunc) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            runnableFunc.run();
        }
    }

    private void initializeChooseCardGridPane() {
        chooseCardGridPane = new GridPane();
        chooseCardGridPane.setAlignment(Pos.CENTER);

        chooseCardGridPane.setHgap(10);
        chooseCardGridPane.setVgap(10);

        chooseCardGridPane.setPadding(new Insets(10, 10, 10, 10));

        chooseCardInfoGridPane.add(chooseCardGridPane, 0, 1);
    }

    private void displayChooseCardsGridPane() {
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger column = new AtomicInteger(0);

        int maxRows = 8;
        int maxColumns = 17;

        deck.getCardsList().forEach(card -> {
            Rectangle cardRectangle = returnImageRectangle(80, 120,
                    10, 10, "cards/" + card.getCardImage());
            cardRectangle.setId(String.valueOf(card.getCardNumber()));
            cardRectangle.getStyleClass().add("clickableNode");

            cardRectangle.setOnMouseClicked(e -> {
                currentCharacter.getCardsList().add(card);
                deck.getCardsList().remove(card);
                fadeOutEffect(cardRectangle, 1, 0.2);

                cardRectangle.setDisable(true);
                characterTurn(characters.getCharactersList().indexOf(currentCharacter) + 1);
            });

            if(column.get() > maxColumns) {
                row.getAndIncrement();
                column.set(0);
            }

            chooseCardGridPane.add(cardRectangle, column.get(), row.get());
            column.getAndIncrement();
        });
    }

    private void characterTurn(int characterIndex) {
        if(characterIndex > characters.getCharactersList().size() - 1) {
            characterIndex = 0;
        }

        currentCharacter = characters.getCharactersList().get(characterIndex);
        characterTurnT.setText(currentCharacter.getCharacterName() + "'s Turn To Choose A Card");

        checkCharactersCards();
    }

    private void checkCharactersCards() {
        boolean allCharactersHaveCards = false;

        for(AbstractCharacter character : characters.getCharactersList()) {
            if(character.getCardsList().size() < 10) {
                allCharactersHaveCards = false;
                break;
            }
            allCharactersHaveCards = true;
        }

        if(allCharactersHaveCards) {
            boardScene(stage, deck, characters, roundNumber);
        }
    }
}
