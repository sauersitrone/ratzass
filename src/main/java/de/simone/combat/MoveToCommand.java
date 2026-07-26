package de.simone.combat;

public class MoveToCommand extends Order {
    private final int x, y;

    public MoveToCommand(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(MilitaryUnit target) {
        // The command tells the target what data to use, 
        // but the target decides HOW to move.
        target.moveTo(x, y); 
    }
}