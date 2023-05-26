package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.abstractClasses.AbstractCharacter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Characters {
    private List<AbstractCharacter> charactersList;
    private int playerNumber;
    private int npcNumber;
    private int startingPoints;
    @Builder
    public Characters(int playerNumber, int npcNumber, int startingPoints) {
        this.playerNumber = playerNumber;
        this.npcNumber = npcNumber;
        this.startingPoints = startingPoints;
        setCharacterList();
    }

    public void setCharacterList() {
        List<Player> playerList = Player.initializePlayers(playerNumber, startingPoints);
        List<Npc> npcList = Npc.initializeNpcs(npcNumber, startingPoints);
        List<AbstractCharacter> character = new ArrayList<>();

        character.addAll(playerList);
        character.addAll(npcList);

        this.charactersList = character;
    }

}