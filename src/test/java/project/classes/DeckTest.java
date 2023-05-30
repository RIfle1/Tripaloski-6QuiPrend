package project.classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.enums.Variant;

class DeckTest {
    Deck deck = Deck.builder()
            .variant(Variant.VARIANT_0)
            .playerNumber(1)
            .npcNumber(1)
            .build();

    @Test
    void initializeCardsList() {
        int cardsWith1Head = deck.getCardsList().stream().filter(card -> card.getCardHeads() == 1).toList().size();
        int cardsWith2Heads = deck.getCardsList().stream().filter(card -> card.getCardHeads() == 2).toList().size();
        int cardsWith3Heads = deck.getCardsList().stream().filter(card -> card.getCardHeads() == 3).toList().size();
        int cardsWith5Heads = deck.getCardsList().stream().filter(card -> card.getCardHeads() == 5).toList().size();
        int cardsWith7Heads = deck.getCardsList().stream().filter(card -> card.getCardHeads() == 7).toList().size();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(76, cardsWith1Head);
            Assertions.assertEquals(9, cardsWith2Heads);
            Assertions.assertEquals(10, cardsWith3Heads);
            Assertions.assertEquals(8, cardsWith5Heads);
            Assertions.assertEquals(1, cardsWith7Heads);
        });

    }

    @Test
    void returnHighestCard() {
        Assertions.assertEquals(104, deck.returnHighestCard().getCardNumber());
    }

    @Test
    void returnRandomCard() {
        Card card = deck.returnRandomCard();
        Assertions.assertEquals(Card.class, card.getClass());
    }

    @Test
    void multipleOf11() {
        Assertions.assertAll(() -> {
            Assertions.assertEquals(5, Deck.multipleOf11(11));
            Assertions.assertEquals(5, Deck.multipleOf11(22));
            Assertions.assertEquals(5, Deck.multipleOf11(33));

            Assertions.assertEquals(0, Deck.multipleOf11(1337));
            Assertions.assertEquals(0, Deck.multipleOf11(420));
            Assertions.assertEquals(0, Deck.multipleOf11(69));
        });
    }

    @Test
    void endsWith() {
        Assertions.assertAll(() -> {
            Assertions.assertEquals(2, Deck.endsWith(12, 2, 2));
            Assertions.assertEquals(2, Deck.endsWith(24, 4, 2));
            Assertions.assertEquals(2, Deck.endsWith(36, 6, 2));

            Assertions.assertEquals(0, Deck.endsWith(1337, 2, 2));
            Assertions.assertEquals(0, Deck.endsWith(420, 2, 2));
            Assertions.assertEquals(0, Deck.endsWith(69, 2, 2));
        });
    }
}