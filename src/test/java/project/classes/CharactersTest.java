package project.classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.enums.Variant;

class CharactersTest {
    Characters characters = Characters.builder()
            .startingPoints(66)
            .playerNumber(1)
            .npcNumber(1)
            .build();
    Deck deck = Deck.builder()
            .variant(Variant.VARIANT_0)
            .playerNumber(1)
            .npcNumber(1)
            .build();
    @Test
    void setCharacterList() {
        characters.setCharacterList();
        Assertions.assertTrue(characters.getCharactersList().size() > 0);
    }

    @Test
    void initializeCards() {
        characters.initializeCards(deck);
        Assertions.assertEquals(10, characters.getCharactersList().get(0).getCardsList().size());
    }

    @Test
    void findCharacterByCard() {
        characters.setCharacterList();
        characters.initializeCards(deck);
        Assertions.assertEquals(characters.getCharactersList().get(0),
                characters.findCharacterByCard(characters.getCharactersList().get(0).getCardsList().get(0)));
    }
}