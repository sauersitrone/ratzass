package de.simone.command;

import java.util.ArrayList;

import com.badlogic.gdx.ai.btree.Task.Status;

import bwapi.Color;
import bwapi.CoordinateType;
import bwapi.Game;
import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.RBWListener;
import lombok.extern.java.Log;

/**
 * represent this app <-> starcraft bridge. This class is the main entry point
 * for all commands to be sent to the game.
 */
@Log
public class CommandQueue {

    public enum ResourceType {
        MINERAL,
        GAS
    }

    private static CommandQueue instance = null;

    private ArrayList<Command> commandQueue = new ArrayList<Command>();
    private ArrayList<CommandQueueListener> listeners = new ArrayList<CommandQueueListener>();

    private CommandQueue() {
        //
    }

    public static CommandQueue getInstance() {
        if (instance == null) {
            instance = new CommandQueue();
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    public void clear() {
        commandQueue.clear();
    }

    public void addCommandListener(CommandQueueListener listener) {
        listeners.add(listener);
    }

    public synchronized void processCommands() {
        Game bwapi = RBWListener.bwClient.getGame();

        for (Command command : commandQueue) {
            boolean success = true;

            Unit unit = null;
            if (command.unitId != -1)
                unit = bwapi.getUnit(command.unitId);

            Unit targetUnit = null;
            if (command.targetId != -1)
                targetUnit = bwapi.getUnit(command.targetId);

            int px = command.position.getX() * 32;
            int py = command.position.getY() * 32;
            TilePosition tilePosition = new TilePosition(px, py);

            switch (command.order) {
                case None:
                    break;
                case UnitCommandType.Attack_Move:
                    success = unit.attack(command.position);
                    break;
                case UnitCommandType.Attack_Unit:
                    success = unit.attack(targetUnit);
                    break;
                case UnitCommandType.Build:
                    bwapi.drawBox(CoordinateType.Map, px, py, px + 96, py + 64, Color.Cyan, false);
                    success = unit.build(command.unitType, tilePosition);

                    // ERROR with valid building placement
                    if (!success) {
                        // Terry: maybe implement message dispach to the source of this command
                    }
                    break;
                case UnitCommandType.Build_Addon:
                    success = unit.buildAddon(command.unitType);
                    break;
                case UnitCommandType.Train:
                    success = unit.train(command.unitType);
                    break;
                case UnitCommandType.Research:
                    success = unit.research(command.techType);
                    break;
                case UnitCommandType.Upgrade:
                    success = unit.upgrade(command.upgradeType);
                    break;
                case UnitCommandType.Set_Rally_Position:
                    success = unit.setRallyPoint(command.position);
                    break;
                case UnitCommandType.Set_Rally_Unit:
                    success = unit.setRallyPoint(targetUnit);
                    break;
                case UnitCommandType.Move:
                    success = unit.move(command.position);
                    break;
                case UnitCommandType.Patrol:
                    success = unit.patrol(command.position);
                    break;
                case UnitCommandType.Hold_Position:
                    success = unit.holdPosition();
                    break;
                case UnitCommandType.Stop:
                    success = unit.stop();
                    break;
                case UnitCommandType.Follow:
                    success = unit.follow(targetUnit);
                    break;
                case UnitCommandType.Gather:
                    success = unit.gather(targetUnit);
                    break;
                case UnitCommandType.Return_Cargo:
                    success = unit.returnCargo();
                    break;
                case UnitCommandType.Repair:
                    success = unit.repair(targetUnit);
                    break;
                case UnitCommandType.Burrow:
                    success = unit.burrow();
                    break;
                case UnitCommandType.Unburrow:
                    success = unit.unburrow();
                    break;
                case UnitCommandType.Cloak:
                    success = unit.cloak();
                    break;
                case UnitCommandType.Decloak:
                    success = unit.decloak();
                    break;
                case UnitCommandType.Siege:
                    success = unit.siege();
                    break;
                case UnitCommandType.Unsiege:
                    success = unit.unsiege();
                    break;
                case UnitCommandType.Lift:
                    success = unit.lift();
                    break;
                case UnitCommandType.Land:
                    success = unit.land(tilePosition);
                    break;
                case UnitCommandType.Load:
                    success = unit.load(targetUnit);
                    break;
                case UnitCommandType.Unload:
                    success = unit.unload(targetUnit);
                    break;
                case UnitCommandType.Unload_All:
                    success = unit.unloadAll();
                    break;
                case UnitCommandType.Unload_All_Position:
                    success = unit.unloadAll(command.position);
                    break;
                case UnitCommandType.Right_Click_Unit:
                    success = unit.rightClick(targetUnit);
                    break;
                case UnitCommandType.Right_Click_Position:
                    success = unit.rightClick(command.position);
                    break;
                case UnitCommandType.Halt_Construction:
                    success = unit.haltConstruction();
                    break;
                case UnitCommandType.Cancel_Construction:
                    success = unit.cancelConstruction();
                    break;
                case UnitCommandType.Cancel_Addon:
                    success = unit.cancelAddon();
                    break;
                case UnitCommandType.Cancel_Train:
                    success = unit.cancelTrain();
                    break;
                case UnitCommandType.Morph:
                    success = unit.morph(command.unitType);
                    break;
                case UnitCommandType.Cancel_Train_Slot:
                    success = unit.cancelTrain(1);
                    break;
                case UnitCommandType.Cancel_Morph:
                    success = unit.cancelMorph();
                    break;
                case UnitCommandType.Cancel_Research:
                    success = unit.cancelResearch();
                    break;
                case UnitCommandType.Cancel_Upgrade:
                    success = unit.cancelUpgrade();
                    break;
                case UnitCommandType.Use_Tech:
                    success = unit.useTech(command.techType);
                    break;
                case UnitCommandType.Use_Tech_Position:
                    success = unit.useTech(command.techType, command.position);
                    break;
                case UnitCommandType.Use_Tech_Unit:
                    success = unit.useTech(command.techType, targetUnit);
                    break;
            }
            if (!success) {
                logFail(command, "Starcraft API failed to execute command");
                listeners.forEach(listener -> listener.event(command));
            }
        }

        commandQueue.clear();
    }

    private synchronized Command addCommand(UnitCommandType command, int unitID, int targetUnit, Position position) {
        Command order = new Command(command, unitID, targetUnit, position);
        return addCommand(order);
    }

    private synchronized Command addCommand(Command command) {
        if (!commandQueue.contains(command)) {
            commandQueue.add(command);

            for (CommandQueueListener listener : listeners) {
                listener.event(command);
            }
        }
        return command;
    }

    public Command gather(ResourceType resourceType) {
        Command command = new Command(UnitCommandType.Gather, -1, -1, null);

        RUnit rUnit = UnitsCenter.getInstance().getUnit(UnitType.Terran_SCV);
        if (rUnit == null) {
            return logFail(command, "No SCV available to gather resources.");
        }
        return addCommand(UnitCommandType.Gather, rUnit.unitID, -1, null);
    }

    public void gather(int unitID, int targetID) {
        addCommand(UnitCommandType.Gather, unitID, targetID, null);
    }

    public void attackMove(int unitID, int x, int y) {
        addCommand(UnitCommandType.Attack_Move, unitID, -1, new Position(x * 32, y * 32));
    }

    /**
     * Tells the unit to attack another unit.
     * 
     * // virtual bool attackUnit(Unit* target) = 0;
     */
    public void attackUnit(int unitID, int targetID) {
        addCommand(UnitCommandType.Attack_Unit, unitID, targetID, null);
    }

    /**
     * Tells the unit to right click (move) to the specified location (in tile
     * coordinates).
     * 
     * // virtual bool rightClick(Position position) = 0;
     */
    public void rightClick(int unitID, int x, int y) {
        Position position = new Position(x * 32, y * 32);
        addCommand(UnitCommandType.Right_Click_Position, unitID, -1, position);
    }

    /**
     * Tells the unit to right click (move) on the specified target unit
     * (Includes resources).
     * 
     * // virtual bool rightClick(Unit* target) = 0;
     */
    public void rightClick(int unitID, int targetID) {
        addCommand(UnitCommandType.Right_Click_Unit, unitID, targetID, null);
    }

    public Command train(UnitType unitType) {
        RUnit rtrainer = StaffUtils.resolveTrainer(unitType);
        Command command = new Command(UnitCommandType.Train, -1, -1, null);

        if (rtrainer == null) {
            return logFail(command, "No building available to train unit: " + unitType);
        }
        return addCommand(UnitCommandType.Train, rtrainer.unitID, -1, null);
    }

    public void train(int unitID, UnitType unitType) {
        Command command = addCommand(UnitCommandType.Train, unitID, -1, null);
        command.unitType = unitType;
    }

    public Position getBuildPosition(UnitType unitType) {
        Position position = new Position(0, 0);
        return position;
    }

    public void build(int unitID, UnitType unitType, Position position) {
        Command command = addCommand(UnitCommandType.Build, unitID, -1, position);
        command.unitType = unitType;
    }

    /**
     * Tells the building to build the specified add on.
     * 
     * // virtual bool buildAddon(UnitType type) = 0;
     */
    public void buildAddon(int unitID, UnitType unitType) {
        Command command = addCommand(UnitCommandType.Build_Addon, unitID, -1, null);
        command.unitType = unitType;
    }

    /**
     * Tells the building to research the specified tech type.
     * 
     * // virtual bool research(TechType tech) = 0;
     */
    public void research(int unitID, TechType techType) {
        Command command = addCommand(UnitCommandType.Research, unitID, -1, null);
        command.techType = techType;
    }

    /**
     * Tells the building to upgrade the specified upgrade type.
     * 
     * // virtual bool upgrade(UpgradeType upgrade) = 0;
     */
    public void upgrade(int unitID, UpgradeType upgradeType) {
        Command command = addCommand(UnitCommandType.Upgrade, unitID, -1, null);
        command.upgradeType = upgradeType;
    }

    /**
     * Orders the unit to stop moving. The unit will chase enemies that enter its
     * vision.
     * 
     * // virtual bool stop() = 0;
     */
    public void stop(int unitID) {
        addCommand(UnitCommandType.Stop, unitID, -1, null);
    }

    public void holdPosition(int unitID) {
        addCommand(UnitCommandType.Hold_Position, unitID, -1, null);
    }

    public Command patrol(Position position) {
        Command command = new Command(UnitCommandType.Patrol, -1, -1, position);

        // select a unit to patrol
        RUnit rUnit = UnitsCenter.getInstance().getUnit(UnitType.Terran_Marine, UnitType.Terran_Wraith);
        if (rUnit == null)
            return logFail(command, "No unit available to patrol.");

        command.unitId = rUnit.unitID;
        return addCommand(command);
    }

    public void patrol(int unitID, Position position) {
        addCommand(UnitCommandType.Patrol, unitID, -1, position);
    }

    /**
     * Orders a unit to follow a target unit.
     * 
     * // virtual bool follow(Unit* target) = 0;
     */
    public void follow(int unitID, int targetID) {
        addCommand(UnitCommandType.Follow, unitID, targetID, null);
    }

    /**
     * Sets the rally location for a building.
     * 
     * // virtual bool setRallyPosition(Position target) = 0;
     */
    public void setRallyPosition(int unitID, Position position) {
        addCommand(UnitCommandType.Set_Rally_Position, unitID, -1, position);
    }

    /**
     * Sets the rally location for a building based on the target unit's current
     * position.
     * 
     * // virtual bool setRallyUnit(Unit* target) = 0;
     */
    public void setRallyUnit(int unitID, int targetID) {
        addCommand(UnitCommandType.Set_Rally_Unit, unitID, targetID, null);
    }

    /**
     * Instructs an SCV to repair a target unit.
     * 
     * // virtual bool repair(Unit* target) = 0;
     */
    public void repair(int unitID, int targetID) {
        addCommand(UnitCommandType.Repair, unitID, targetID, null);
    }

    /**
     * Orders a zerg unit to morph to a different unit type.
     * 
     * // virtual bool morph(UnitType type) = 0;
     */
    public void morph(int unitID, UnitType unitType) {
        Command command = addCommand(UnitCommandType.Morph, unitID, -1, null);
        command.unitType = unitType;
    }

    /**
     * Tells a zerg unit to burrow. Burrow must be upgraded for non-lurker units.
     * 
     * // virtual bool burrow() = 0;
     */
    public void burrow(int unitID) {
        addCommand(UnitCommandType.Burrow, unitID, -1, null);
    }

    /**
     * Tells a burrowed unit to unburrow.
     * 
     * // virtual bool unburrow() = 0;
     */
    public void unburrow(int unitID) {
        addCommand(UnitCommandType.Unburrow, unitID, -1, null);
    }

    /**
     * Orders a siege tank to siege.
     * 
     * // virtual bool siege() = 0;
     */
    public void siege(int unitID) {
        addCommand(UnitCommandType.Siege, unitID, -1, null);
    }

    /**
     * Orders a siege tank to un-siege.
     * 
     * // virtual bool unsiege() = 0;
     */
    public void unsiege(int unitID) {
        addCommand(UnitCommandType.Unsiege, unitID, -1, null);
    }

    /**
     * Tells a unit to cloak. Works for ghost and wraiths.
     * 
     * // virtual bool cloak() = 0;
     */
    public void cloak(int unitID) {
        addCommand(UnitCommandType.Cloak, unitID, -1, null);
    }

    /**
     * Tells a unit to decloak, works for ghosts and wraiths.
     * 
     * // virtual bool decloak() = 0;
     */
    public void decloak(int unitID) {
        addCommand(UnitCommandType.Decloak, unitID, -1, null);
    }

    /**
     * Commands a Terran building to lift off.
     * 
     * // virtual bool lift() = 0;
     */
    public void lift(int unitID) {
        addCommand(UnitCommandType.Lift, unitID, -1, null);
    }

    /**
     * Commands a terran building to land at the specified location.
     * 
     * // virtual bool land(TilePosition position) = 0;
     */
    public void land(int unitID, Position position) {
        addCommand(UnitCommandType.Land, unitID, -1, position);
    }

    /**
     * Orders the transport unit to load the target unit.
     * 
     * // virtual bool load(Unit* target) = 0;
     */
    public void load(int unitID, int targetID) {
        addCommand(UnitCommandType.Load, unitID, targetID, null);
    }

    /**
     * Orders a transport unit to unload the target unit at the current transport
     * location.
     * 
     * // virtual bool unload(Unit* target) = 0;
     */
    public void unload(int unitID, int targetID) {
        addCommand(UnitCommandType.Unload, unitID, targetID, null);
    }

    /**
     * Orders a transport to unload all units at the current location.
     * 
     * // virtual bool unloadAll() = 0;
     */
    public void unloadAll(int unitID) {
        addCommand(UnitCommandType.Unload_All, unitID, -1, null);
    }

    /**
     * Orders a unit to unload all units at the target location.
     * 
     * // virtual bool unloadAll(Position position) = 0;
     */
    public void unloadAll(int unitID, Position position) {
        addCommand(UnitCommandType.Unload_All_Position, unitID, -1, position);
    }

    /**
     * Orders a being to stop being constructed.
     * 
     * // virtual bool cancelConstruction() = 0;
     */
    public void cancelConstruction(int unitID) {
        addCommand(UnitCommandType.Cancel_Construction, unitID, -1, null);
    }

    /**
     * Tells an scv to pause construction on a building.
     * 
     * // virtual bool haltConstruction() = 0;
     */
    public void haltConstruction(int unitID) {
        addCommand(UnitCommandType.Halt_Construction, unitID, -1, null);
    }

    /**
     * Orders a zerg unit to stop morphing.
     * 
     * // virtual bool cancelMorph() = 0;
     */
    public void cancelMorph(int unitID) {
        addCommand(UnitCommandType.Cancel_Morph, unitID, -1, null);
    }

    /**
     * Tells a building to remove the last unit from its training queue.
     * 
     * // virtual bool cancelTrain() = 0;
     */
    public void cancelTrain(int unitID) {
        addCommand(UnitCommandType.Cancel_Train, unitID, -1, null);
    }

    /**
     * Tells a building to remove a specific unit from its queue.
     * 
     * // virtual bool cancelTrain(int slot) = 0;
     */
    public void cancelTrain(int unitID, int slot) {
        addCommand(UnitCommandType.Cancel_Train_Slot, unitID, slot, null);
    }

    /**
     * Orders a Terran building to stop constructing an add on.
     * 
     * // virtual bool cancelAddon() = 0;
     */
    public void cancelAddon(int unitID) {
        addCommand(UnitCommandType.Cancel_Addon, unitID, -1, null);
    }

    /***
     * Tells a building cancel a research in progress.
     * 
     * // virtual bool cancelResearch() = 0;
     */
    public void cancelResearch(int unitID) {
        addCommand(UnitCommandType.Cancel_Research, unitID, -1, null);
    }

    /***
     * Tells a building cancel an upgrade in progress.
     * 
     * // virtual bool cancelUpgrade() = 0;
     */
    public void cancelUpgrade(int unitID) {
        addCommand(UnitCommandType.Cancel_Upgrade, unitID, -1, null);
    }

    /**
     * Tells the unit to use the specified tech, (i.e. STEM PACKS)
     * 
     * // virtual bool useTech(TechType tech) = 0;
     */
    public void useTech(int unitID, TechType techType) {
        Command command = addCommand(UnitCommandType.Use_Tech, unitID, -1, null);
        command.techType = techType;
    }

    /**
     * Tells the unit to use tech at the target location.
     * 
     * Note: for AOE spells such as plague.
     * 
     * // virtual bool useTech(TechType tech, Position position) = 0;
     */
    public void useTech(int unitID, TechType techType, Position position) {
        addCommand(UnitCommandType.Use_Tech_Position, unitID, -1, position);
    }

    /**
     * Tells the unit to use tech on the target unit.
     * 
     * Note: for targeted spells such as irradiate.
     * 
     * // virtual bool useTech(TechType tech, Unit* target) = 0;
     */
    public void useTech(int unitID, TechType techType, int targetID) {
        addCommand(UnitCommandType.Use_Tech_Unit, unitID, targetID, null);
    }

    public Command logSuccess(Command command, String message) {
        log.info("SUCCEEDED: " + message);
        command.status = Status.SUCCEEDED;
        command.message = message;
        return command;
    }

    public static Command logFail(Command command, String message) {
        log.info("FAILED: " + message);
        command.status = Status.FAILED;
        command.message = message;
        return command;
    }
}
