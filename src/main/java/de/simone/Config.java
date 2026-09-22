package de.simone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Properties;

public class Config {
    public enum BehaviorTreeStatus {
        Running, Suspended, Stepping;
    }

    private static Properties properties = new Properties();

    public static String chaosLauncherPath;
    public static BehaviorTreeStatus treeStatus = BehaviorTreeStatus.Running;

    private static File configFile;

    // game settings
    public static int speed = 50;
    public static boolean userInput = true;
    public static boolean fogOfWar = false;
    public static boolean autoCamera = false;

    // StarCraft map drawing settings
    public static boolean ignoreBases = false; // for micro scenarios
    public static boolean drawIDs = true;
    public static boolean drawPings = false;
    public static boolean drawPlayerUnits = true;
    public static boolean drawEnemyUnits = true;
    public static boolean drawNeutralUnits = true;
    public static boolean drawResources = true;
    public static boolean drawStartSpots = true;
    public static boolean drawRegions = true;
    public static boolean fillRegions = false;
    public static boolean drawChokepoints = true;

    // behavior tree settings
    public static boolean scrollToExecutingNode = true;
    public static boolean showResources = false;
    static {
        loadStarCraftProperties();
        try {
            URL url = Config.class.getResource("/config.properties");
            configFile = new File(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getResourceFile(String fileName) {
        try {
            URL url = Config.class.getResource("/" + fileName);
            Path path = Paths.get(url.toURI());
            return path.toFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void loadStarCraftProperties() {
        try {
            properties.load(Config.class.getResourceAsStream("/config.properties"));
            chaosLauncherPath = properties.getProperty("GameSettings.chaosLauncherPath");
            ignoreBases = Boolean.parseBoolean(properties.getProperty("GameSettings.IgnoreBases"));
            speed = Integer.parseInt(properties.getProperty("GameSettings.speed"));
            userInput = Boolean.parseBoolean(properties.getProperty("GameSettings.UserInput"));
            autoCamera = Boolean.parseBoolean(properties.getProperty("GameSettings.AutoCamera"));
            fogOfWar = Boolean.parseBoolean(properties.getProperty("GameSettings.fogOfWar"));
            drawIDs = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawIDs"));
            drawPings = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawPings"));
            drawPlayerUnits = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawPlayerUnits"));
            drawEnemyUnits = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawEnemyUnits"));
            drawNeutralUnits = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawNeutralUnits"));
            drawResources = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawResources"));
            drawStartSpots = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawStartSpots"));
            drawRegions = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawRegions"));
            fillRegions = Boolean.parseBoolean(properties.getProperty("GameSettings.map.fillRegions"));
            drawChokepoints = Boolean.parseBoolean(properties.getProperty("GameSettings.map.drawChokepoints"));

            scrollToExecutingNode = Boolean.parseBoolean(properties.getProperty("BehaviorTree.scrollToExecutingNode"));
            showResources = Boolean.parseBoolean(properties.getProperty("BehaviorTree.showResources"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.setProperty("GameSettings.chaosLauncherPath", chaosLauncherPath);
            properties.setProperty("GameSettings.IgnoreBases", Boolean.toString(ignoreBases));
            properties.setProperty("GameSettings.speed", Integer.toString(speed));
            properties.setProperty("GameSettings.UserInput", Boolean.toString(userInput));
            properties.setProperty("GameSettings.AutoCamera", Boolean.toString(autoCamera));
            properties.setProperty("GameSettings.fogOfWar", Boolean.toString(fogOfWar));
            properties.setProperty("GameSettings.map.drawIDs", Boolean.toString(drawIDs));
            properties.setProperty("GameSettings.map.drawPings", Boolean.toString(drawPings));
            properties.setProperty("GameSettings.map.drawPlayerUnits", Boolean.toString(drawPlayerUnits));
            properties.setProperty("GameSettings.map.drawEnemyUnits", Boolean.toString(drawEnemyUnits));
            properties.setProperty("GameSettings.map.drawNeutralUnits", Boolean.toString(drawNeutralUnits));
            properties.setProperty("GameSettings.map.drawResources", Boolean.toString(drawResources));
            properties.setProperty("GameSettings.map.drawStartSpots", Boolean.toString(drawStartSpots));
            properties.setProperty("GameSettings.map.drawRegions", Boolean.toString(drawRegions));
            properties.setProperty("GameSettings.map.fillRegions", Boolean.toString(fillRegions));
            properties.setProperty("GameSettings.map.drawChokepoints", Boolean.toString(drawChokepoints));

            properties.setProperty("BehaviorTree.scrollToExecutingNode", Boolean.toString(scrollToExecutingNode));
            properties.setProperty("BehaviorTree.showResources", Boolean.toString(showResources));

            LocalDateTime now = LocalDateTime.now();
            properties.store(fos, "Updated at " + now);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
