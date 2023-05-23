package project.classes;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Board {
    private Card[][] board;
    private List<Card> cardsList = new ArrayList<>();

    public Board(Card[][] board) {
        this.board = board;
        setCardsList();
    }

    public void setCardsList() {
        cardsList.clear();
        for (Card[] cardsArray : board) {
            for(Card card : cardsArray) {
                if(card != null) cardsList.add(card);
            }
        }
    }

    public Card[][] getBoard() {
        setCardsList();
        return board;
    }

    public List<Card> getCardsList() {
        setCardsList();
        return cardsList;
    }
}
