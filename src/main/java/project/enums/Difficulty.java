package project.enums;

import java.util.HashMap;
import java.util.List;

import static project.enums.EnumMethods.returnFormattedEnum;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    /**
     * Method to return a list of the enum values
     * @return List of enum values
     */
    public static List<String> getDifficultyList() {
        Difficulty[] difficultyValues = Difficulty.values();
        return EnumMethods.getEnumList(difficultyValues);
    }

    /**
     * Method that takes in parameter the enum as a string and returns in as an enum
     * @param difficulty The enum as a string
     * @return The enum
     */
    public static Difficulty setDifficulty(String difficulty) {
        HashMap<String, Difficulty> difficultyHashMap = new HashMap<>();
        Difficulty[] difficultyValues = Difficulty.values();

        for(Difficulty difficultyValue:difficultyValues) {
            difficultyHashMap.put(returnFormattedEnum(difficultyValue), difficultyValue);
        }
        return difficultyHashMap.get(difficulty);
    }

}
