package project.functions;

import project.GuiLauncherMain;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
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
     * Returns the images in a folder
     *
     * @param folder The folder
     * @return The images as strings in the folder
     */
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

    /**
     * Returns the contents a txt file as a string
     *
     * @param fileName The name of the file
     * @return The contents of the file as a string
     */
    public static String readFileAsString(String fileName) {
        InputStream inputStream = GuiLauncherMain.class.getResourceAsStream("/project/texts/" + fileName);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    /**
     * Returns the path of an object inside the resources folder
     *
     * @param objectName The name of the object
     * @return The path of the object as a string
     */
    public static String returnPath(String objectName) {
        return Objects.requireNonNull(GuiLauncherMain.class.getClassLoader().getResource(objectName)).toString();
    }

    /**
     * Returns the path of an object inside the resources folder
     *
     * @param path       The path of the object
     * @param objectName The name of the object
     * @return The path of the object as a URL
     */
    public static URL returnURL(String path, String objectName) {
        try {
            return new URL(returnPath(path) + objectName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
