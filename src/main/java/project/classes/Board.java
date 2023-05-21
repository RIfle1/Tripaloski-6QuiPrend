package project.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private Card[][] board;

    public Board(Card[][] board) {
        this.board = board;
    }
}
