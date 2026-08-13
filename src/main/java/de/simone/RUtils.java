package de.simone;

import java.util.Map;
import java.util.TreeMap;

import bwapi.Position;
import bwapi.Unit;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Table;

public class RUtils {

    private static Map<String, Integer> errorTimestamps = new TreeMap<>();
    private static Map<String, Integer> errors = new TreeMap<>();

    public static void printEndOfGameStats() {
        if (RBWListener.game == null) {
            return;
        }

        System.out.println("Game statistics:");
        System.out.printf("%-20s %-20s", "Total time:", RBWListener._secondsNow + " seconds");

        Table summaryTable = RBWListener.unitEventsTable.summarize("totalResources", AggregateFunctions.sum).by("type");
        System.out.println("Total Resources by Unit Type:");
        System.out.println(summaryTable.print());
    }

    public static void killStarcraftProcess() {
        executeInCommandLine("taskkill /IM StarCraft.exe /T /F");
        // destroyInCommandLine("taskkill /IM StarCraft.exe /T /F");
    }

    public static void killChaosLauncherProcess() {
        // destroyInCommandLine("taskkill /IM Chaoslauncher.exe /T /F");
        executeInCommandLine("taskkill /IM Chaoslauncher.exe /T /F");
        // executeInCommandLine("taskkill /IM Chaoslauncher - MultiInstance.exe /T /F");
    }

    /**
     * Autostart Chaoslauncher
     * Combined with Chaoslauncher -> Settings -> Run Starcraft on Startup
     * SC will be autostarted at this moment
     */
    public static void startChaosLauncherProcess() {
        try {
            Thread.sleep(150);
            String command = "cmd /c " + Env.chaosLauncherPath;

            executeInCommandLine(command);
        } catch (InterruptedException ignored) {
        }
    }

    private static void executeInCommandLine(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static String getResourceFile(String fileName) {
        ClassLoader classLoader = RUtils.class.getClassLoader();
        String fileName2 = classLoader.getResource(fileName).getFile();

        fileName2 = fileName2.substring(1);
        fileName2 = fileName2.replace("%20", " ");
        return fileName2;
    }

    public static void printMaxOncePerMinutePlusPrintStackTrace(String message) {
        if (!theSameErrorWasLoggedLessThanMinuteAgo(message)) {
            System.out.println(message);
            Thread.dumpStack();
        }

        increaseErrorCount(message);
    }

    private static boolean theSameErrorWasLoggedLessThanMinuteAgo(String message) {

        return errorTimestamps.containsKey(message) && (RBWListener._secondsNow - errorTimestamps.get(message) < 60);
    }

    private static void increaseErrorCount(String message) {
        int currentCount = errors.getOrDefault(message, 0);
        errors.put(message, currentCount + 1);
    }

    public static void printMaxOncePerMinute(String message) {
        if (!theSameErrorWasLoggedLessThanMinuteAgo(message)) {
            System.out.println(message);
        }

        increaseErrorCount(message);
    }

    public static Position translateByPixels(Unit unit, int pixelDX, int pixelDY) {
        return new Position(unit.getX() + pixelDX, unit.getY() + pixelDY);
    }

    public static void exitGame() {
        killProcesses();
    }

    private static void killProcesses() {
        System.out.println("Killing StarCraft process... ");
        RUtils.killStarcraftProcess();

        System.out.println("Killing Chaoslauncher process... ");
        RUtils.killChaosLauncherProcess();

        System.out.println("Exit...");
        System.exit(0);
    }

}
