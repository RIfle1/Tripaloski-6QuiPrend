package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.abstractClasses.AbstractCharacter;
import project.enums.Variant;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Characters {
    private List<AbstractCharacter> charactersList;
    private int playerNumber;
    private int npcNumber;
    private int startingPoints;

    /**
     * Constructor
     * Characters List is set automatically
     *
     * @param playerNumber   Number of Players
     * @param npcNumber      Number of Npcs
     * @param startingPoints Starting Points
     */
    @Builder
    public Characters(int playerNumber, int npcNumber, int startingPoints) {
        this.playerNumber = playerNumber;
        this.npcNumber = npcNumber;
        this.startingPoints = startingPoints;
        setCharacterList();
    }

    /**
     * Sets the character list based on the number of players and npcs
     */
    public void setCharacterList() {
        List<Player> playerList = Player.initializePlayers(playerNumber, startingPoints);
        List<Npc> npcList = Npc.initializeNpcs(npcNumber, startingPoints);
        List<AbstractCharacter> charactersList = new ArrayList<>();

        charactersList.addAll(playerList);
        charactersList.addAll(npcList);

        this.charactersList = charactersList;
    }

    /**
     * Initializes the cards for each character
     *
     * @param deck Deck
     */
    public void initializeCards(Deck deck) {
        if (deck.getVariant().equals(Variant.VARIANT_0) || deck.getVariant().equals(Variant.VARIANT_1)) {
            this.getCharactersList().forEach(character -> {
                for (int i = 1; i <= 10; i++) {
                    character.giveCard(deck);
                }
            });
        }
        this.getCharactersList().forEach(AbstractCharacter::sortCardsIncreasing);
    }

    /**
     * Finds which Character owns a card
     *
     * @param card Card
     * @return Character
     */
    public AbstractCharacter findCharacterByCard(Card card) {
        for (AbstractCharacter character : this.getCharactersList()) {
            for (Card c : character.getCardsList()) {
                if (c.getCardNumber() == card.getCardNumber()) {
                    return character;
                }
            }
        }
        return null;
    }

}
