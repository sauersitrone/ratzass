package de.simone.game.actions;

public class RResource {

    public enum Type {
        UNDEFINED,
        TIME,
        SUPPLY,
        MINERAL,
        GAS
    }

    public Type type = Type.UNDEFINED;
    public int amount = 0;

    public RResource() {
        //
    }

    public RResource(Type type, int amount) {
        this.type = type;
        this.amount = amount;
    }
}
