package project.classes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Board {
    private Card[][] board;
    private List<Card> cardsList = new ArrayList<>();

    /**
     * Constructor
     * Sets CardsList automatically from the board
     *
     * @param board Board
     */
    public Board(Card[][] board) {
        this.board = board;
        setCardsList();
    }

    /**
     * Sets CardsList automatically from the board
     */
    public void setCardsList() {
        cardsList.clear();
        for (Card[] cardsArray : board) {
            for (Card card : cardsArray) {
                if (card != null) cardsList.add(card);
            }
        }
    }

    /**
     * Returns the board
     * Sets CardsList automatically from the board before returning it
     *
     * @return Board
     */
    public Card[][] getBoard() {
        setCardsList();
        return board;
    }

    /**
     * Returns the cards list
     * Sets CardsList automatically from the board before returning it
     *
     * @return Cards List
     */
    public List<Card> getCardsList() {
        setCardsList();
        return cardsList;
    }
}
