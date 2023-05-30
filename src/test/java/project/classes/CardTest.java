package project.classes;

import org.junit.jupiter.api.Test;
import project.enums.Variant;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void returnRandomCard() {
        Deck deck = Deck.builder()
                .variant(Variant.VARIANT_0)
                .npcNumber(1)
                .playerNumber(1)
                .build();
        Card card = Card.returnRandomCard(deck);
        assertNotNull(card);
    }
}