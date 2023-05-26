package project.abstractClasses;

import lombok.Getter;
import lombok.Setter;
import project.classes.Card;

import java.util.Comparator;
import java.util.List;

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
     * @param characterName Character Name
     * @param characterNumber Character Number
     * @param cardsList Cards List
     * @param takenCardsList Taken Cards List
     */

    public AbstractCharacter(String characterName, String characterImage, int characterNumber, int points, List<Card> cardsList, List<Card> takenCardsList) {
        this.characterName = characterName;
        this.characterImage = characterImage;
        this.characterNumber = characterNumber;
        this.points = points;
        this.cardsList = cardsList;
        this.takenCardsList = takenCardsList;
    }

    public Card returnBiggestCard() {
        if(!cardsList.isEmpty()) {
            return cardsList.stream().max(Comparator.comparingInt(Card::getCardNumber)).orElseThrow(RuntimeException::new);
        }
        return null;
    }
}
