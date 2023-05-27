package project.functions;

import project.GuiLauncherMain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

//    public static ArrayList<String> returnImageList(String imageDir) {
//
////        File[] images = new File[0];
//        //            images = new File(Objects.requireNonNull(GuiLauncherMain.class.getResource(imageDir)).toURI()).listFiles();
//        URI uri = returnURI(imageDir);
//        File[] images = new File(uri).listFiles();
//
//        assert images != null;
//        ArrayList<String> imageList = new ArrayList<>();
//
//        Stream.of(images)
//                .filter(file -> !file.isDirectory())
//                .map(File::getName)
//                .forEach(imageList::add);
//
//        return imageList;
//    }

    public static ArrayList<String> returnImageList(String folder) {
        File[] images = new File(folder).listFiles();

        assert images != null;
        ArrayList<String> imageList = new ArrayList<>();

        Stream.of(images)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .forEach(imageList::add);

        return imageList;
    }


    public static String readFileAsString(String fileName) {
        InputStream inputStream = GuiLauncherMain.class.getResourceAsStream("/project/texts/" + fileName);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public static URI returnURI(String path) {
        try {
            return Objects.requireNonNull(GuiLauncherMain.class.getResource(path)).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String returnPath(String objectName) {
        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource(objectName)).toString() ;
    }

    public static URL returnURL(String path, String objectName) {
        try {
            return new URL(returnPath(path) + objectName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
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

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
