package de.simone;

public class Main {

    public static void main(String[] args) {
        // initializeEsper();
        Env.init();
        AKeyboard.init();

        RUtils.killStarcraftProcess();
        RUtils.killChaosLauncherProcess();
        // IMPORTANT: Make sure Chaoslauncher -> Settings -> "Run Starcraft on Startup" is checked
        RUtils.startChaosLauncherProcess();

        Ratzass ratzass = new Ratzass();
        ratzass.run();
    }
}
