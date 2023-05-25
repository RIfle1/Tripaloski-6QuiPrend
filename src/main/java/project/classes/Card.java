package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static project.functions.GeneralFunctions.generateDoubleBetween;

@Getter
@Setter
@Builder
public class Card {
    private int cardNumber;
    private int cardHeads;
    private String cardImage;

    /**
     * Constructor
     * @param cardNumber Card Number
     * @param cardHeads Amount of Heads on the card
     * @param cardImage Name of the card image ex: "1.png"
     */

    public Card(int cardNumber, int cardHeads, String cardImage) {
        this.cardNumber = cardNumber;
        this.cardHeads = cardHeads;
        this.cardImage = cardImage;
    }

    /**
     * Return a random card from the deck
     * @param deck Deck
     * @return Random Card
     */
    public static Card returnRandomCard(Deck deck) {
        int randomCardNumber = (int) generateDoubleBetween(0, deck.getCardsList().size() - 1);
        return deck.getCardsList().get(randomCardNumber);
    }
}

