package de.simone.command;

import java.awt.event.KeyEvent;

/**
 * Constant definitions for the ABL code.
 * 
 * Includes the following information:
 * - races
 * - unit types
 * - orders
 * - tech types
 * - upgrade types
 *
 * Include the following line under the imports in the abl header:
 * constants starcraftbot.abl.java.StarCraftConstants;
 */
public interface StarCraftConstants {

    public static int NULL_UNIT = -1;
    public static int PIXELS_PER_TILE = 32;
    public static int DISTANCE_CLOSE = 20; // TODO: Tune this...
    public static int DISTANCE_ARRIVED = 3; // TODO: Tune this...
    public static int FARM_SUPPLY = 8;

    public enum TrainUnitStatus {
        Request, SentCommand, Training
    }

    // reasons for construction a pylon
    public static final int PYLON_FIRST = 0;
    public static final int PYLON_OPEN = 1;
    public static final int PYLON_MINERALS = 2;
    public static final int PYLON_CHOKE = 3;
    public static final int PYLON_BOUNDARY = 4;

    public static final int MINERAL_LOAD = 5;
    public static final int GAS_LOAD = 4;

    public enum WorkerTask {
        IDLE, MINING, GAS, CONSTRUCTING, SCOUTING, DEFEND, CLEAR
    }

    public enum FighterTask {
        IDLE, GUARD, ATTACK, FLEE, REGROUP, GATHER, EGG
    }

    public enum ActionType {
        Build, ASAP, Scout, Attack
    };

    public static final int VK_R = KeyEvent.VK_R;
    public static final int VK_G = KeyEvent.VK_G;
    public static final int VK_M = KeyEvent.VK_M;

    public static enum ConstructionStatus {
        Request, Prepare, Ready, Constructing, Paused, Finished
    }

}
