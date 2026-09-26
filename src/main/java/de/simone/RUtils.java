package de.simone;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;

import lombok.extern.java.Log;

@Log
public class RUtils {

    private static List<String> validImages = List.of(".png", ".jpg", ".jpeg", ".gif", ".bmp");

    /**
     * Starts the Starcraft process using Chaoslauncher. Ensures that any existing
     * Starcraft and Chaoslauncher processes are terminated before starting a new
     * one.
     * NOTE:
     * Make sure Chaoslauncher -> Settings -> "Run Starcraft on Startup" is checked
     */
    public static void startStarcraftProcess() {
        endStarcraftProcess();
        executeInCommandLine(new String[] { Config.chaosLauncherPath });
    }

    /**
     * Ends the Starcraft and Chaoslauncher processes if they are running.
     */
    public static void endStarcraftProcess() {
        executeInCommandLine(new String[] { "taskkill", "/IM", "StarCraft.exe", "/T", "/F" });
        executeInCommandLine(new String[] { "taskkill", "/IM", "Chaoslauncher.exe", "/T", "/F" });
    }

    private static void executeInCommandLine(String[] command) {
        try {
            Thread.sleep(150);
            Runtime.getRuntime().exec(command);
            Thread.sleep(120);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Returns the file path of the specified resource file. The file must be
     * located in the resources folder.
     * 
     * @param fileName - the name
     * @return the file
     */
    public static String getResourceFile(String fileName) {
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            URL url = classLoader.getResource(fileName);
            String file = url.toURI().toString();
            file = file.substring(6);
            file = file.replaceAll("%20", " ");
            return file;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    public static ImageIcon getImageIcon(String fileName) {
        // try the file name as is first
        String filePath = getResourceFile(fileName);
        if (filePath != null) {
            return new ImageIcon(filePath);
        }

        // try appending valid image extensions to the file name
        for (String extension : validImages) {
            filePath = getResourceFile(fileName + extension);
            if (filePath != null) {
                return new ImageIcon(filePath);
            }
        }
        return null;
    }

    /**
     * Returns a behavior tree parsed from the specified tree file using the
     * provided backboard.
     * 
     * @param <T>       - the type of the backboard
     * @param treeFile  - the path
     * @param backboard - the backboard
     * @return the behavior tree, or null if an error occurs
     */
    public static <T> BehaviorTree<T> getBehaviorTree(String treeFile, T backboard) {
        try (InputStream inputStream = RUtils.class.getResourceAsStream("/" + treeFile)) {
            BehaviorTreeParser<T> parser = new BehaviorTreeParser<T>(BehaviorTreeParser.DEBUG_NONE);
            BehaviorTree<T> behaviorTree = parser.parse(inputStream, backboard);
            return behaviorTree;
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
        return null;
    }

}
