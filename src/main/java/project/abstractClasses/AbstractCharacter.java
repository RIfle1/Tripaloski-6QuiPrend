package project.abstractClasses;

import lombok.Getter;
import lombok.Setter;
import project.classes.Card;

import java.util.List;

/**
 * Abstract Character Class
 */
@Setter
@Getter
public abstract class AbstractCharacter {
    private String characterName;
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

    public AbstractCharacter(String characterName, int characterNumber, int points, List<Card> cardsList, List<Card> takenCardsList) {
        this.characterName = characterName;
        this.characterNumber = characterNumber;
        this.points = points;
        this.cardsList = cardsList;
        this.takenCardsList = takenCardsList;
    }
}
