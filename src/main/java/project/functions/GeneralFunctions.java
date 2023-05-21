package project.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class GeneralFunctions {

    /**
     * Generate a random double between min and max
     *
     * @param min min value
     * @param max max value
     * @return random double between min and max
     */
    public static double generateDoubleBetween(double min, double max) {
        if (min == max) {
            return min;
        } else if (max < min) {
            return Math.round(new Random().nextDouble(min - max) + max);
        } else {
            return Math.round(new Random().nextDouble(max - min) + min);
        }
    }

    /**
     * Chooses a random double in the given double array
     *
     * @param array input double array
     * @return random double from the array
     */
    public static double chooseRandomDouble(double[] array) {
        return array[new Random().nextInt(array.length)];
    }

    /**
     * Generates a random string with a given length
     *
     * @param length length of the string to generate
     * @return random string with a given length
     */
    public static String generateRandomString(int length) {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "").substring(0, length);
    }

    /**
     * Checks if a given string is empty
     *
     * @param input input string
     * @return true if the string is not empty, false otherwise
     */
    public static boolean checkString(String input) {
        return input.length() != 0;
    }

    /**
     * Check if the given string is a positive integer
     *
     * @param input input string
     * @return true if the string is a positive integer, false otherwise
     */
    public static boolean checkPositiveInt(String input) {
        if (checkInt(input)) {
            return Integer.parseInt(input) > 0;
        } else {
            return false;
        }
    }

    /**
     * Check if the given string is an integer
     *
     * @param input input string
     * @return true if the string is an integer, false otherwise
     */
    public static boolean checkInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ArrayList<String> returnImageList(String imageDir) {
        File[] images = new File(imageDir).listFiles();

        assert images != null;

        ArrayList<String> imageList = new ArrayList<>();

        Stream.of(images)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .forEach(imageList::add);

        return imageList;
    }

    public static String returnTargetPath(String dir) {
        return "target/classes/project/" + dir;
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
