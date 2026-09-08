package de.simone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import lombok.extern.java.Log;

/**
 * Configuration Properties
 * 
 * Agent Settings
 * - ABLBotName (String): Name of the ABL bot to instantiate
 * - BuildOrderScript (String): path of the build order to run
 *
 * Game Settings
 * - GameSettings.IgnoreBases (boolean): disbale BWTA?
 * - GameSettings.ShowGUI (boolean): show the agent GUI?
 * - GameSettings.AutoRestart (boolean): restart games on game end?
 * - GameSettings.UseManners (boolean): quit when loss is detected?
 * - GameSettings.Fastest (boolean): setSpeed(0)?
 * - GameSettings.UserInput (boolean): enable the user to perform actions?
 * - GameSettings.QuitOnGameEnd (boolean): end this process when the game ends?
 * - GameSettings.AutoCamera (boolean): Enables automatic camera positioning
 * 
 */
@Log
public class Env   {
    public enum BehaviorTreeStatus {
        Running, Suspended, Stepping;
    }
    private static Properties properties = new Properties();

    public static String chaosLauncherPath;
    public static BehaviorTreeStatus treeStatus = BehaviorTreeStatus.Running;

    private long lastCameraUpdate = 0;
    private int drawBuildLocations = 0;
    private boolean drawParticles = false;
    private int delay = 0;
    private int quitFrame = 0;
    private static File configFile;

    // game settings
    public static boolean autoRestart = false;
    public static boolean useManners = false;
    public static int speed = 0; // 42 = fastest, 67 = normal, 167 = slowest
    public static boolean userInput = true;
    public static boolean fogOfWar = false;
    public static boolean quitOnGameEnd = false;
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
        configFile = new File(Env.class.getResource("/config.properties").getFile());
    }

    private static void loadStarCraftProperties() {
        try {
            properties.load(Env.class.getResourceAsStream("/config.properties"));
            chaosLauncherPath = properties.getProperty("GameSettings.chaosLauncherPath");
            ignoreBases = Boolean.parseBoolean(properties.getProperty("GameSettings.IgnoreBases"));
            autoRestart = Boolean.parseBoolean(properties.getProperty("GameSettings.AutoRestart"));
            useManners = Boolean.parseBoolean(properties.getProperty("GameSettings.UseManners"));
            speed = Integer.parseInt(properties.getProperty("GameSettings.speed"));
            userInput = Boolean.parseBoolean(properties.getProperty("GameSettings.UserInput"));
            quitOnGameEnd = Boolean.parseBoolean(properties.getProperty("GameSettings.QuitOnGameEnd"));
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
            properties.setProperty("GameSettings.AutoRestart", Boolean.toString(autoRestart));
            properties.setProperty("GameSettings.UseManners", Boolean.toString(useManners));
            properties.setProperty("GameSettings.speed", Integer.toString(speed));
            properties.setProperty("GameSettings.UserInput", Boolean.toString(userInput));
            properties.setProperty("GameSettings.QuitOnGameEnd", Boolean.toString(quitOnGameEnd));
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
          properties.  store(fos, "Updated at " + now);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
