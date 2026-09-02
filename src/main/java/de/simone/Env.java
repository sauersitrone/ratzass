package de.simone;

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
public class Env extends Properties {
    public enum BehaviorTreeStatus {
        Running, Suspended, Stepping;
    }

    public static String chaosLauncherPath;
    public static BehaviorTreeStatus treeStatus = BehaviorTreeStatus.Running;

    private long lastCameraUpdate = 0;
    private int drawBuildLocations = 0;
    private boolean drawParticles = false;
    private int delay = 0;
    private int quitFrame = 0;

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

    private Env() {
        //
    }

    public static void init() {
        Env env = new Env();
        env.loadStarCraftProperties();
    }

    private void loadStarCraftProperties() {
        try {
            load(Env.class.getResourceAsStream("/config.properties"));
            chaosLauncherPath = getProperty("GameSettings.chaosLauncherPath");
            ignoreBases = Boolean.parseBoolean(getProperty("GameSettings.IgnoreBases"));
            autoRestart = Boolean.parseBoolean(getProperty("GameSettings.AutoRestart"));
            useManners = Boolean.parseBoolean(getProperty("GameSettings.UseManners"));
            speed = Integer.parseInt(getProperty("GameSettings.speed"));
            userInput = Boolean.parseBoolean(getProperty("GameSettings.UserInput"));
            quitOnGameEnd = Boolean.parseBoolean(getProperty("GameSettings.QuitOnGameEnd"));
            autoCamera = Boolean.parseBoolean(getProperty("GameSettings.AutoCamera"));
            fogOfWar = Boolean.parseBoolean(getProperty("GameSettings.fogOfWar"));

            drawIDs = Boolean.parseBoolean(getProperty("GameSettings.map.drawIDs"));
            drawPings = Boolean.parseBoolean(getProperty("GameSettings.map.drawPings"));
            drawPlayerUnits = Boolean.parseBoolean(getProperty("GameSettings.map.drawPlayerUnits"));
            drawEnemyUnits = Boolean.parseBoolean(getProperty("GameSettings.map.drawEnemyUnits"));
            drawNeutralUnits = Boolean.parseBoolean(getProperty("GameSettings.map.drawNeutralUnits"));
            drawResources = Boolean.parseBoolean(getProperty("GameSettings.map.drawResources"));
            drawStartSpots = Boolean.parseBoolean(getProperty("GameSettings.map.drawStartSpots"));
            drawRegions = Boolean.parseBoolean(getProperty("GameSettings.map.drawRegions"));
            fillRegions = Boolean.parseBoolean(getProperty("GameSettings.map.fillRegions"));
            drawChokepoints = Boolean.parseBoolean(getProperty("GameSettings.map.drawChokepoints"));

            scrollToExecutingNode = Boolean.parseBoolean(getProperty("BehaviorTree.scrollToExecutingNode"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
