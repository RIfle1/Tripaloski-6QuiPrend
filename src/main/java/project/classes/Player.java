package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.abstractClasses.AbstractCharacter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player extends AbstractCharacter {


    /**
     * Constructor
     *
     * @param characterName   Character Name
     * @param points          Points
     * @param characterNumber Character Number
     * @param cardsList       Cards List
     * @param takenCardsList  Taken Cards List
     */

    @Builder
    public Player(String characterName, int characterNumber, int points, List<Card> cardsList, List<Card> takenCardsList) {
        super(characterName, characterNumber, points, cardsList, takenCardsList);
    }

    /**
     * Initialize players
     *
     * @param maxCharacters number of maximum players to initialize
     * @return a list of players
     */

    public static List<Player> initializePlayers(int maxCharacters, int startingPoints) {
        List<Player> playerList = new ArrayList<>();

        for (int i = 1; i <= maxCharacters; i++) {
            Player player = Player.builder()
                    .characterName("Player" + i)
                    .characterNumber(i)
                    .points(startingPoints)
                    .cardsList(new ArrayList<>())
                    .takenCardsList(new ArrayList<>())
                    .build();
            playerList.add(player);
        }

        return playerList;
    }
}
