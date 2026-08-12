package de.simone;

import java.util.ArrayList;

import de.simone.command.RBehaviorTreeListener;

public class Ratzass {
    private static Ratzass instance;

    private ArrayList<RBehaviorTreeListener> listeners = new ArrayList<>();
    private boolean isRunning = false;

    private Ratzass() {
        //
    }

    public static Ratzass getInstance() {
        if (instance == null) {
            instance = new Ratzass();
        }
        return instance;
    }

    public void start() {

    }

    public void stop() {
        // destroy the Ratzass instance and set it to null
        isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void resume() {
        isRunning = true;
    }
    
    public void pause() {
        isRunning = false;
    }

    public void addTaskListener(RBehaviorTreeListener listener) {
        this.listeners.add(listener);
    }

}
