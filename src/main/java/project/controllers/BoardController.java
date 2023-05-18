package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import project.classes.Card;
import project.classes.Npc;
import project.classes.Player;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static project.functions.GeneralFunctions.generateDoubleBetween;
import static project.functions.JavaFxFunctions.returnFXMLURL;
import static project.functions.JavaFxFunctions.sendToScene;

/**
 * Board Controller
 */
public class BoardController implements Initializable {
    public static int maxCards;
    private static List<Card> deck = new ArrayList<>();
    private static List<Player> playerList = new ArrayList<>();
    private static List<Npc> npcList = new ArrayList<>();
    private static int playerNumber;
    private static int npcNumber;
    // Variant 0 => No Modifications to the game
    // Variant 1 => Max cards based on number of players
    // Variant 2 => Players can choose their cards by turn
    // Variant 3 => Variant 1 + Variant 2
    private static int variantNumber;

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

    public static void boardScene(MouseEvent event, int playerNumberParam,
                                  int npcNumberParam, int variantNumberParam,
                                  List<Player> playerListParam, List<Npc> npcListParam) {
        playerNumber = playerNumberParam;
        npcNumber = npcNumberParam;
        variantNumber = variantNumberParam;
        playerList = playerListParam;
        npcList = npcListParam;


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
        initializeDeck(variantNumber);
        if (variantNumber == 0 || variantNumber == 1) initializeCharacters();

//        deck.forEach(card -> {
//            System.out.println("Number: " + card.getCardNumber() + " Heads: " + card.getCardHeads());
//        });
    }

    /**
     * Initialize the deck
     *
     * @param variantNumber Variant Number Chosen in the Main Menu
     */

    public void initializeDeck(int variantNumber) {
        if (variantNumber == 1 || variantNumber == 3) {
            maxCards = (playerNumber + npcNumber) * 10 + 4;
        } else {
            maxCards = 104;
        }

        for (int i = 1; i <= maxCards; i++) {
            int heads = 0;
            heads += endsWith(i, 5, 2);
            heads += endsWith(i, 0, 3);
            heads += multipleOf(i, 11, 5);

            if (heads == 0) heads = 1;

            Card card = Card.builder()
                    .cardNumber(i)
                    .cardHeads(heads)
                    .cardImage(i + ".png")
                    .build();

            deck.add(card);
        }
    }

    /**
     * Initialize the characters
     */

    public void initializeCharacters() {
        playerList = Player.initializePlayers(playerNumber);
        npcList = Npc.initializeNpcs(npcNumber);

        initializeCharacterCards();
    }

    /**
     * Initialize the characters cards
     */

    public void initializeCharacterCards() {
        playerList.forEach(player -> {
            for (int i = 1; i <= 10; i++) {
                int randomCard = (int) generateDoubleBetween(1, maxCards);
                player.getCardsList().add(deck.get(randomCard));
            }
        });

        npcList.forEach(npc -> {
            for (int i = 1; i <= 10; i++) {
                int randomCard = (int) generateDoubleBetween(1, maxCards);
                npc.getCardsList().add(deck.get(randomCard));
            }
        });
    }

    /**
     * Check if the number ends with a specific number
     *
     * @return Amount of heads to add
     */

    private int endsWith(int number, int endNumber, int returnNumber) {
        if (String.valueOf(number).endsWith(String.valueOf(endNumber))) {
            return returnNumber;
        } else {
            return 0;
        }
    }

    /**
     * Check if the number is a multiple of a specific number
     *
     * @param number       Input number
     * @param multiple     Multiple number
     * @param returnNumber Amount of heads to add
     * @return Amount of heads to add
     */

    private int multipleOf(int number, int multiple, int returnNumber) {
        if (number % multiple == 0) {
            return returnNumber;
        } else {
            return 0;
        }
    }

}
