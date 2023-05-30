package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.controllers.MainMenuController;
import project.enums.Variant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static project.functions.GeneralFunctions.generateDoubleBetween;

@Getter
@Setter
public class Deck {
    private List<Card> cardsList;
    private Variant variant;
    private int playerNumber;
    private int npcNumber;
    private int maxCards;

    /**
     * Constructor
     * Automatically creates the cards list based on max Cards
     *
     * @param variant      Variant
     * @param playerNumber Player Number
     * @param npcNumber    Npc Number
     */
    @Builder
    public Deck(Variant variant, int playerNumber, int npcNumber) {
        this.variant = variant;
        this.playerNumber = playerNumber;
        this.npcNumber = npcNumber;
        this.maxCards = returnMaxCards();
        this.cardsList = initializeCardsList();
    }

    /**
     * Creates a list of cards based on the max cards
     *
     * @return List of cards
     */
    private List<Card> initializeCardsList() {
        List<Card> deck = new ArrayList<>();

        for (int i = 1; i <= maxCards; i++) {
            int heads = 0;
            heads += endsWith(i, 5, 2);
            heads += endsWith(i, 0, 3);
            heads += multipleOf11(i);

            if (heads == 0) heads = 1;
            Card card = Card.builder().cardNumber(i).cardHeads(heads).cardImage(i + ".png").build();
            deck.add(card);
        }
        return deck;
    }

    /**
     * Return the max amounts of cards based on the variant
     *
     * @return Max amount of cards
     */
    public int returnMaxCards() {
        if (variant.equals(Variant.VARIANT_1) || variant.equals(Variant.VARIANT_3)) {
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

    public static int endsWith(int number, int endNumber, int heads) {
        if (String.valueOf(number).endsWith(String.valueOf(endNumber))) {
            return heads;
        } else {
            return 0;
        }
    }

    /**
     * Check if the number is a multiple of 11
     *
     * @param number Input number
     * @return Amount of heads to add
     */

    public static int multipleOf11(int number) {
        if (number % 11 == 0) {
            return 5;
        } else {
            return 0;
        }
    }

    /**
     * Return the highest card from the deck
     * @return Highest Card
     */
    public Card returnHighestCard() {
        return cardsList.stream().max(Comparator.comparingInt(Card::getCardNumber)).orElse(null);
    }

    /**
     * Return a random card from the deck
     * @return Random Card
     */
    public Card returnRandomCard() {
        return cardsList.get((int) generateDoubleBetween(0, cardsList.size() - 1));
    }
}
