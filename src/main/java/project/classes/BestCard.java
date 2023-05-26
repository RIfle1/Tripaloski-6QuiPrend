package project.classes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BestCard {
    private Card card;
    private int row;
    private int column;
    private int numberDifference;

    @Builder
    public BestCard(Card card, int row, int column, int numberDifference) {
        this.card = card;
        this.row = row;
        this.column = column;
        this.numberDifference = numberDifference;
    }


}
