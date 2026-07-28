package de.simone.combat;

import java.util.ArrayList;
import java.util.List;

public abstract class MilitaryUnit {

    public int id;
    public int x;
    public int y;

    public final List<MilitaryUnit> members = new ArrayList<>();

    public abstract void handleOrder(Order order); // Receives the command object

    public abstract void moveTo(int x, int y); // Actual implementation logic

    public abstract void attack(String enemy);
}
