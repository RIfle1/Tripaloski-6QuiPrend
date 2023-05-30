package project.abstractClasses;

import lombok.Getter;
import lombok.Setter;
import project.classes.Card;
import project.classes.Deck;

import java.util.Comparator;
import java.util.List;

import static project.classes.Card.returnRandomCard;

/**
 * Abstract Character Class
 */
@Setter
@Getter
public abstract class AbstractCharacter {
    private String characterName;
    private String characterImage;
    private int characterNumber;
    private int points;
    private List<Card> cardsList;
    private List<Card> takenCardsList;

    /**
     * Constructor
     *
     * @param characterName   Character Name
     * @param characterImage  Character Image
     * @param characterNumber Character Number
     * @param points          Points
     * @param cardsList       Cards List
     * @param takenCardsList  Taken Cards List
     */

    public AbstractCharacter(String characterName, String characterImage, int characterNumber, int points, List<Card> cardsList, List<Card> takenCardsList) {
        this.characterName = characterName;
        this.characterImage = characterImage;
        this.characterNumber = characterNumber;
        this.points = points;
        this.cardsList = cardsList;
        this.takenCardsList = takenCardsList;
    }

    /**
     * Return the smallest card from the character's cards list
     *
     * @return Smallest Card
     */

    public Card returnSmallestCard() {
        if (!cardsList.isEmpty()) {
            return cardsList.stream().min(Comparator.comparingInt(Card::getCardNumber)).orElseThrow(RuntimeException::new);
        }
        return null;
    }

    /**
     * Sort the character's card by increasing order
     */
    public void sortCardsIncreasing() {
        cardsList.sort(Comparator.comparingInt(Card::getCardNumber));
    }

    /**
     * Gives a card to a character and removes it from the deck
     *
     */
    public void giveCard(Deck deck) {
        Card randomCard = returnRandomCard(deck);
        cardsList.add(randomCard);
        deck.getCardsList().remove(randomCard);
    }

    /**
     * Function that returns a card from a character's card list by card number
     *
     * @param cardNumber        Card's number
     * @return Card
     */
    public Card returnCharacterCardByNumber(String cardNumber) {
        return this.getCardsList().stream().filter(card -> card.getCardNumber() == Integer.parseInt(cardNumber)).findFirst().orElse(null);
    }
}
