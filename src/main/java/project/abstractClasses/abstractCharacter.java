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
public abstract class abstractCharacter {
    private String characterName;
    private int characterNumber;
    private List<Card> cardsList;
    private List<Card> takenCardsList;

    /**
     * @param characterName Character Name
     * @param characterNumber Character Number
     * @param cardsList Cards List
     * @param takenCardsList Taken Cards List
     */

    public abstractCharacter(String characterName, int characterNumber, List<Card> cardsList, List<Card> takenCardsList) {
        this.characterName = characterName;
        this.characterNumber = characterNumber;
        this.cardsList = cardsList;
        this.takenCardsList = takenCardsList;
    }

}
