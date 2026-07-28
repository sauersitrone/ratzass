package de.simone.combat;

public abstract class Order {
    public abstract void execute(MilitaryUnit unit); // Double dispatch: executes the order on the given unit
}
