package de.simone.combat;

class Soldier extends MilitaryUnit {
    private final String name;

    public Soldier(String name) { this.name = name; }

    @Override
    public void handleOrder(Order order) {
        order.execute(this); // Double dispatch: executes the specific order on itself
    }

    @Override
    public void moveTo(int x, int y) {
        System.out.println(name + " is pathfinding to coordinates (" + x + ", " + y + ").");
    }

    @Override
    public void attack(String enemy) {
        System.out.println(name + " fires weapon at " + enemy + "!");
    }
}
