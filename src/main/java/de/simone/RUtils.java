package de.simone;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Table;

public class RUtils {

    public static void printEndOfGameStats() {
        if (Ratzass.game == null) {
            return;
        }

        System.out.println("Game statistics:");
        System.out.printf("%-20s %-20s", "Total time:", Ratzass._secondsNow + " seconds");

        Table summaryTable = Ratzass.unitEventsTable.summarize("totalResources", AggregateFunctions.sum).by("type");
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
}
