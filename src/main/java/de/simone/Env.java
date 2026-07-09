package de.simone;

import java.util.Properties;

public class Env extends Properties{

    public static String chaosLauncherPath;
    public static boolean isParamTweaker;
    public static boolean isTesting;
    public static boolean fogOfWar;

    
    /**
     * Disabling makes game so fast, you actually be like "Daaaaamn!".
     * Unfortunately it means nothing gets rendered, so game appears to be frozen.
     */
    public static boolean DISABLE_GUI = false;

    //    public static boolean USE_CODE_PROFILER = true;
    public static boolean USE_CODE_PROFILER = false;

    public static void init() {
        new Env();
    }
    
    private Env() {
        try {
            load(Main.class.getResourceAsStream("/resources/config.properties"));
            chaosLauncherPath = getProperty("chaosLauncherPath");
            isParamTweaker = Boolean.parseBoolean(getProperty("isParamTweaker"));
            isTesting = Boolean.parseBoolean(getProperty("isTesting"));
            fogOfWar = Boolean.parseBoolean(getProperty("fogOfWar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
