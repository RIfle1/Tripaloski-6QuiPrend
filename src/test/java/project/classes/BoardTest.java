package project.classes;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void setCardsList() {
        Board board = new Board(new Card[][]{
                {Card.builder().cardNumber(1).build(), Card.builder().cardNumber(2).build()},
                {Card.builder().cardNumber(3).build(), Card.builder().cardNumber(4).build()}
        });

        board.setCardsList();
        board.setCardsList();

        assertEquals(4, board.getCardsList().size());
    }

    @Test
    void returnCardsOnBoardRow() {
        Card card1 = Card.builder().cardNumber(1).build();
        Card card2 = Card.builder().cardNumber(2).build();

        Board board = Board.builder()
                .board(new Card[4][6])
                .build();

        board.getBoard()[0][0] = card1;
        board.getBoard()[0][1] = card2;

        assertEquals(2, board.returnCardsOnBoardRow(0).size());
    }

    @Test
    void deleteCardsOnBoardRow() {
        Card card1 = Card.builder().cardNumber(1).build();
        Card card2 = Card.builder().cardNumber(2).build();

        Board board = Board.builder()
                .board(new Card[4][6])
                .build();

        board.getBoard()[0][0] = card1;
        board.getBoard()[0][1] = card2;

        board.deleteCardsOnBoardRow(0);

        assertEquals(0, board.returnCardsOnBoardRow(0).size());
    }
}