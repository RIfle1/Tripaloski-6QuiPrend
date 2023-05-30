package project.abstractClasses;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.classes.Card;
import project.classes.Characters;
import project.classes.Deck;
import project.enums.Variant;

import java.util.ArrayList;
import java.util.Arrays;

class AbstractCharacterTest {
    Characters characters = Characters.builder()
            .playerNumber(1)
            .npcNumber(1)
            .startingPoints(66)
            .build();

    Card card = Card.builder()
            .cardNumber(1)
            .cardHeads(1)
            .cardImage("1.png")
            .build();

    @Test
    void returnSmallestCard() {
        characters.getCharactersList().get(0)
                .setCardsList(new ArrayList<>(Arrays.asList(
                        Card.builder().cardNumber(1).build(),
                        Card.builder().cardNumber(2).build(),
                        Card.builder().cardNumber(3).build()
                )));
        Assertions.assertEquals(card.getCardNumber()
                , characters.getCharactersList().get(0).returnSmallestCard().getCardNumber());
    }

    @Test
    void giveCard() {
        Deck deck = Deck.builder()
                .variant(Variant.VARIANT_0)
                .playerNumber(1)
                .npcNumber(1)
                .build();

        characters.getCharactersList().get(0).giveCard(deck);

        Assertions.assertEquals(1,
                characters.getCharactersList().get(0).getCardsList().size());
    }

    @Test
    void returnCharacterCardByNumber() {
        characters.getCharactersList().get(0).getCardsList().add(card);
        Assertions.assertEquals(card,
                characters.getCharactersList().get(0).returnCharacterCardByNumber("1"));
    }
}