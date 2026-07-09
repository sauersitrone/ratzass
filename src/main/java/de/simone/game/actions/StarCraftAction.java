package de.simone.game.actions;

/**
 * The Abstract Superclass for all actions which can be performed in StarCraft.
 */
public abstract class StarCraftAction {

    public enum Status {
        PENDING,
        ABORTED,
        SUCCESS,
        FAILURE
    }

    public int unitID;
    public int targetUnitID;
    public Status status = Status.PENDING;

    public abstract void execute();

   public synchronized void setStatus(boolean status) {
      if (status) {
         this.status = Status.SUCCESS;
      } else {
         this.status = Status.FAILURE;
      }

   }
}
