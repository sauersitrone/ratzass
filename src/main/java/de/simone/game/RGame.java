package de.simone.game;

import java.util.List;

import bwapi.Game;
import bwapi.Player;
import de.simone.Env;
import de.simone.Ratzass;

/**
 * Represents various aspect of the game like time elapsed (in frames or approximated seconds), free supply
 * (from our point of view), game speed, enemy APlayer etc.<br />
 * <br /><b>It's worth to study this class carefully as it contains some really useful methods.</b>
 */
public class RGame {
    private static boolean umsMode = false; // Should be set to `true` on UMS (custom) maps

    /**
     * Quits the game gently, killing all processes and cleaning up.
     */
    public static void exit() {
        Ratzass.instance.onEnd(false);
    }

    public static void quit() {
        exit();
    }

    /**
     * Quits the game gently, killing all processes and cleaning up.
     */
    public static void exit(String message) {
        System.out.println(message);
        Ratzass.instance.exitGame();
    }

    public static void changeDisableUI(boolean disableUI) {
        Env.DISABLE_GUI = disableUI;
        Game game = Ratzass.game;
        if (game != null) {
            game.setGUI(Env.DISABLE_GUI);
        }
    }

    /**
     * Returns game speed.
     */
    public static int getGameSpeed() {
        return GameSpeed.gameSpeed;
    }

    /**
     * Returns true once per n game frames.
     */
    public static boolean everyNthGameFrame(int n) {
        return Ratzass.game.getFrameCount() % n == 0;
    }

    /**
     * Returns false once per n game frames.
     */
    public static boolean notNthGameFrame(int n) {
        return Ratzass.game.getFrameCount() % n != 0;
    }

    /**
     * Number of minerals.
     */
    public static int minerals() {
        return Ratzass.game.self().minerals();
    }

    /**
     * Number of gas.
     */
    public static int gas() {
        return Ratzass.game.self().gas();
    }

    /**
     * Number of free supply.
     */
    public static int supplyFree() {
        return supplyTotal() - supplyUsed();
    }

    /**
     * Number of supply used.
     */
    public static int supplyUsed() {
        return Ratzass.game.self().supplyUsed() / 2;
    }

    /**
     * Number of supply totally available.
     */
    public static int supplyTotal() {
        return Ratzass.game.self().supplyTotal() / 2;
    }

    /**
     * Returns current APlayer.
     */
    public static Player getPlayerUs() {
return Ratzass.game.self();
        // if (_our == null) {
        //     _our = new APlayer(Ratzass.game.self());
        // }

        // return _our;
    }

    /**
     * Returns all Players.
     */
    public static List<Player> getPlayers() {
        List<Player> players = Ratzass.game.getPlayers();
        return players;
    }

    /**
     * Returns enemy APlayer.
     */
    public static Player enemy() {
        return rawPlayer();
        // if (_enemy == null) {
        //     _enemy = new APlayer(rawPlayer());
        // }
        // return _enemy;
    }

    public static Player rawPlayer() {
        return Ratzass.game.enemies().iterator().next();
    }

    public static String enemyName() {
        return rawPlayer().getName();
    }

    /**
     * Returns neutral APlayer (minerals, geysers, critters).
     */
    public static Player neutralPlayer() {
        return Ratzass.game.neutral();
    }

    /**
     * UMS maps are custom made maps, which may be used to test micro-management. They can cause a lot of exceptions.
     */
    public static boolean isUms() {
        return umsMode;
    }

    /**
     * UMS maps are custom made maps, which may be used to test micro-management.
     */
    public static void setUmsMode() {
        // if (MapSpecificCommander.shouldTreatAsNormalMap()) {
        //     return;
        // }

        // if (!RGame.umsMode) {
        //     RGame.umsMode = true;
        //     System.out.println("### UMS mode enabled! ###");

        //     MissionChanger.forceMissionAttack("UmsAlwaysAttack");
        // }
    }

    public static String getMapName() {
        return Ratzass.game.mapName();
    }

    /**
     * Sends in-game message that will be visible by other APlayers.
     */
    public static void sendMessage(String message) {
        if (Ratzass.game != null) {
            Ratzass.game.sendText(message);
        }
    }
}
