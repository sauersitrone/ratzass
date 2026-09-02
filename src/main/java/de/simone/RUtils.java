package de.simone;

import java.io.InputStream;
import java.util.logging.Level;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;

import lombok.extern.java.Log;

@Log
public class RUtils {

    public static void startStarcraftProcess() {
        endStarcraftProcess();
        // Make sure Chaoslauncher -> Settings -> "Run Starcraft on Startup" is checked
        executeInCommandLine(Env.chaosLauncherPath);
    }

    public static void endStarcraftProcess() {
        executeInCommandLine("taskkill /IM StarCraft.exe /T /F");
        executeInCommandLine("taskkill /IM Chaoslauncher.exe /T /F");
    }

    private static void executeInCommandLine(String command) {
        try {
            Thread.sleep(150);
            Runtime.getRuntime().exec(command);
            Thread.sleep(150);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public static String getResourceFile(String fileName) {
        ClassLoader classLoader = RUtils.class.getClassLoader();
        String fileName2 = classLoader.getResource(fileName).getFile();

        fileName2 = fileName2.substring(1);
        fileName2 = fileName2.replace("%20", " ");
        return fileName2;
    }

    /**
     * Parses a behavior tree file and returns the corresponding BehaviorTree
     * instance.
     *
     * @param <E>        - the type of the blackboard
     * @param treeFile   - the file name
     * @param blackboard - the blackboard
     * @return the parsed BehaviorTree instance, or null if parsing fails
     */
    public static <E> BehaviorTree<E> parseFile(String treeFile, E blackboard) {
        try (InputStream inputStream = RUtils.class.getResourceAsStream("/" + treeFile)) {
            BehaviorTreeParser<E> parser = new BehaviorTreeParser<E>(BehaviorTreeParser.DEBUG_HIGH);
            BehaviorTree<E> behaviorTree = parser.parse(inputStream, blackboard);
            return behaviorTree;
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
        return null;
    }

    /**
     * Steps the behavior tree based on the current status of the environment. If
     * the environment status is Running, the behavior tree will be stepped
     * 
     * @param behaviorTree - the tree
     */
    public static void step(BehaviorTree<?> behaviorTree) {
        if (Env.treeStatus == Env.BehaviorTreeStatus.Running) {
            behaviorTree.step();
        } else if (Env.treeStatus == Env.BehaviorTreeStatus.Stepping) {
            behaviorTree.step();
            Env.treeStatus = Env.BehaviorTreeStatus.Suspended;
        } else if (Env.treeStatus == Env.BehaviorTreeStatus.Suspended) {
            // Do nothing
        }
    }
}
