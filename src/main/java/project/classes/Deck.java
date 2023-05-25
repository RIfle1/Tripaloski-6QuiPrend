package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.controllers.MainMenuController;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Deck {
    private List<Card> cardsList;
    private int variantNumber;
    private int playerNumber;
    private int npcNumber;
    private int maxCards;
    @Builder
    public Deck(int variantNumber, int playerNumber, int npcNumber) {
        this.variantNumber = variantNumber;
        this.playerNumber = playerNumber;
        this.npcNumber = npcNumber;
        this.maxCards = returnMaxCards();
        this.cardsList = initializeCardsList();
    }

    private List<Card> initializeCardsList() {
        List<Card> deck = new ArrayList<>();

        for (int i = 1; i <= maxCards; i++) {
            int heads = 0;
            heads += endsWith(i, 5, 2);
            heads += endsWith(i, 0, 3);
            heads += multipleOf(i, 11, 5);

            if (heads == 0) heads = 1;

            Card card = Card.builder().cardNumber(i).cardHeads(heads).cardImage(i + ".png").build();

            deck.add(card);
        }

        return deck;
    }

    private int returnMaxCards() {
        if (variantNumber == 1 || variantNumber == 3) {
            return (playerNumber + npcNumber) * 10 + 4;
        } else {
            return MainMenuController.maxCards;
        }
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
