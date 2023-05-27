package project.enums;

import java.util.HashMap;
import java.util.List;

import static project.enums.EnumMethods.returnFormattedEnum;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    public static List<String> getDifficultyList() {
        Difficulty[] difficultyValues = Difficulty.values();
        return EnumMethods.getEnumList(difficultyValues);
    }

    public static Difficulty setDifficulty(String difficulty) {
        HashMap<String, Difficulty> difficultyHashMap = new HashMap<>();
        Difficulty[] difficultyValues = Difficulty.values();

        for(Difficulty difficultyValue:difficultyValues) {
            difficultyHashMap.put(returnFormattedEnum(difficultyValue), difficultyValue);
        }
        return difficultyHashMap.get(difficulty);
    }

}
