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
    private static Properties properties = new Properties();

    private static File configFile;
    
    // game settings
    public static String chaosLauncherPath;
    public static int speed = 50;
    public static boolean userInput = true;
    public static boolean fogOfWar = false;
    public static boolean autoCamera = false;

    // StarCraft map drawing settings
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
            chaosLauncherPath = properties.getProperty("chaosLauncherPath");
            speed = Integer.parseInt(properties.getProperty("speed"));
            userInput = Boolean.parseBoolean(properties.getProperty("userInput"));
            autoCamera = Boolean.parseBoolean(properties.getProperty("autoCamera"));
            fogOfWar = Boolean.parseBoolean(properties.getProperty("fogOfWar"));
            showResources = Boolean.parseBoolean(properties.getProperty("showResources"));

            drawIDs = Boolean.parseBoolean(properties.getProperty("map.drawIDs"));
            drawPings = Boolean.parseBoolean(properties.getProperty("map.drawPings"));
            drawPlayerUnits = Boolean.parseBoolean(properties.getProperty("map.drawPlayerUnits"));
            drawEnemyUnits = Boolean.parseBoolean(properties.getProperty("map.drawEnemyUnits"));
            drawNeutralUnits = Boolean.parseBoolean(properties.getProperty("map.drawNeutralUnits"));
            drawResources = Boolean.parseBoolean(properties.getProperty("map.drawResources"));
            drawStartSpots = Boolean.parseBoolean(properties.getProperty("map.drawStartSpots"));
            drawRegions = Boolean.parseBoolean(properties.getProperty("map.drawRegions"));
            fillRegions = Boolean.parseBoolean(properties.getProperty("map.fillRegions"));
            drawChokepoints = Boolean.parseBoolean(properties.getProperty("map.drawChokepoints"));

            scrollToExecutingNode = Boolean.parseBoolean(properties.getProperty("behaviorTree.scrollToExecutingNode"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.setProperty("chaosLauncherPath", chaosLauncherPath);
            properties.setProperty("speed", Integer.toString(speed));
            properties.setProperty("userInput", Boolean.toString(userInput));
            properties.setProperty("autoCamera", Boolean.toString(autoCamera));
            properties.setProperty("fogOfWar", Boolean.toString(fogOfWar));
            properties.setProperty("showResources", Boolean.toString(showResources));
            properties.setProperty("map.drawIDs", Boolean.toString(drawIDs));
            properties.setProperty("map.drawPings", Boolean.toString(drawPings));
            properties.setProperty("map.drawPlayerUnits", Boolean.toString(drawPlayerUnits));
            properties.setProperty("map.drawEnemyUnits", Boolean.toString(drawEnemyUnits));
            properties.setProperty("map.drawNeutralUnits", Boolean.toString(drawNeutralUnits));
            properties.setProperty("map.drawResources", Boolean.toString(drawResources));
            properties.setProperty("map.drawStartSpots", Boolean.toString(drawStartSpots));
            properties.setProperty("map.drawRegions", Boolean.toString(drawRegions));
            properties.setProperty("map.fillRegions", Boolean.toString(fillRegions));
            properties.setProperty("map.drawChokepoints", Boolean.toString(drawChokepoints));
            properties.setProperty("behaviorTree.scrollToExecutingNode", Boolean.toString(scrollToExecutingNode));

            LocalDateTime now = LocalDateTime.now();
            properties.store(fos, "Updated at " + now);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
