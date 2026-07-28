package de.simone.combat;

public class Squad extends MilitaryUnit {
    private final String squadId;

    public Squad(String squadId) {
        this.squadId = squadId;
    }

    public void addMember(MilitaryUnit unit) {
        members.add(unit);
    }

    @Override
    public void handleOrder(Order order) {
        System.out.println("Squad " + squadId + " received order. Routing to members...");
        for (MilitaryUnit member : members) {
            member.handleOrder(order); // Pass the exact same command object down
        }
    }

    @Override
    public void moveTo(int x, int y) {
        /* Optional: Squad-level formation logic before routing */ }

    @Override
    public void attack(String enemy) {
        /* Optional: Squad-level tactical logic */ }
}