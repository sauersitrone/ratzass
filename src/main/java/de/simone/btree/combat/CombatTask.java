package de.simone.btree.combat;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import de.simone.command.CombatCenter;
import de.simone.command.Squad;

public abstract class CombatTask extends LeafTask<Squad> {
    float startTime;
    float timeout = 1;

    // i need some how fire an event to the GUI that this task is being executed, so
    // that the GUI can highlight it
    public Task<Squad> getControl() {
        return control;
    }

    protected void sendCommunication(String text) {
        Squad squad = getObject();
        CombatCenter.sendCommunication(squad, text);
    }

    /**
     * return true if the time has exceeded the timeout value from the time, the
     * start method was executed.
     * 
     * @return true or false
     */
    protected boolean isTimeOut() {
        float del = GdxAI.getTimepiece().getTime() - startTime;
        return del >= timeout;
    }

    @Override
    public void start() {
        startTime = GdxAI.getTimepiece().getTime();
    }

    @Override
    protected Task<Squad> copyTo(Task<Squad> task) {
        return task;
    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        name = name.replace("Task", "");
        name = name.replace("Condition", "");
        return name;
    }
}
