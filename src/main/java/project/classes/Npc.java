package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.abstractClasses.abstractCharacter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Npc extends abstractCharacter {

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
    public Npc(String characterName, int characterNumber, int points, List<Card> cardsList, List<Card> takenCardsList) {
        super(characterName, characterNumber, points, cardsList, takenCardsList);
    }

    /**
     * Initialize npcs
     *
     * @param maxCharacters Number of maximum npcs to initialize
     * @return List of npcs
     */

    public static List<Npc> initializeNpcs(int maxCharacters, int startingPoints) {
        List<Npc> npcList = new ArrayList<>();

        for (int i = 1; i <= maxCharacters; i++) {
            Npc npc = Npc.builder()
                    .characterName("Npc" + i)
                    .characterNumber(i)
                    .points(startingPoints)
                    .cardsList(new ArrayList<>())
                    .takenCardsList(new ArrayList<>())
                    .build();
            npcList.add(npc);
        }

        return npcList;
    }
}
