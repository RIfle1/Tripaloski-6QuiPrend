package project.classes;

import lombok.Builder;
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
     * Constructor
     * Sets CardsList automatically from the board
     *
     * @param board Board
     */
    @Builder
    public Board(Card[][] board) {
        this.board = board;
        setCardsList();
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

    /**
     * Returns the cards on the board on the selected row
     *
     * @param rowParam the row to get the cards from
     * @return A list of cards
     */
    public List<Card> returnCardsOnBoardRow(int rowParam) {
        List<Card> cardsOnBoardRow = new ArrayList<>();
        Card[][] localBoard = this.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                if (row == rowParam && localBoard[row][column] != null) {
                    cardsOnBoardRow.add(localBoard[row][column]);
                }
            }
        }
        return cardsOnBoardRow;
    }

    /**
     * Deletes the cards on the board on the selected row
     *
     * @param rowParam the row to delete the cards from
     */
    public void deleteCardsOnBoardRow(int rowParam) {
        Card[][] localBoard = this.getBoard();

        for (int row = 0; row < localBoard.length; row++) {
            for (int column = 0; column < localBoard[row].length; column++) {
                if (row == rowParam && localBoard[row][column] != null) {
                    localBoard[row][column] = null;
                }
            }
        }
    }
}
