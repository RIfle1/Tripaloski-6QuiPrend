package project.functions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GeneralFunctions {

    public static double generateDoubleBetween(double min, double max) {
        if (min == max) {
            return min;
        } else if (max < min) {
            return Math.round(new Random().nextDouble(min - max) + max);
        } else {
            return Math.round(new Random().nextDouble(max - min) + min);
        }
    }

    public static double chooseRandomDouble(double[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static String generateRandomString(int length) {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "").substring(0,length);
    }



    public static boolean checkString(String input) {
        return input.length() != 0;
    }

    public static boolean checkPositiveInt(String input) {
        if(checkInt(input)) {
            return Integer.parseInt(input) > 0;
        }
        else {
            return false;
        }
    }

    public static boolean checkInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    public static String returnFileAttribute(String dir, String filenameCompressed, String attribute) {
//        List<String> saves = returnSaves(filenameCompressed);
//
//        File file = new File(dir + "/" + saves.get(0));
//
//        FileTime fileTime = null;
//        try {
//            fileTime = (FileTime) Files.getAttribute(file.toPath(), attribute);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Date fileDate = new Date(fileTime.toMillis());
//        String pattern = "dd-MM-yyyy HH:mm:ss";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        return simpleDateFormat.format(fileDate);
//    }

}
