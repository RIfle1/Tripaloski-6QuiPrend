package project.enums;
import java.util.ArrayList;
import java.util.List;

public class EnumMethods {
    public static String returnFormattedEnum(Object object) {

        String enumString = object.toString().toLowerCase();
        String[] enumArrRaw = enumString.split("_");
        int enumArrRawLength = enumArrRaw.length - 1;

        StringBuilder newEnumWord = new StringBuilder();
        for(String enumWord:enumArrRaw) {
            char firstLetter = enumWord.charAt(0);
            newEnumWord.append(String.valueOf(firstLetter).toUpperCase()).append(enumWord.substring(1));

            // Add a space between each word
            if(enumArrRawLength > 0) {
                newEnumWord.append(" ");
                enumArrRawLength--;
            }
        }

        return newEnumWord.toString();
    }

    public static List<String> getEnumList(Object[] enumValues){
        List<String> enumArray = new ArrayList<>();
        for(Object object:enumValues) {
            enumArray.add(returnFormattedEnum(object));
        }
        return enumArray;
    }

}
