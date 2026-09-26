package de.simone.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bwapi.Game;
import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitFilter;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwem.BWMap;
import bwem.ChokePoint;
import de.simone.RBWListener;
import de.simone.command.StarCraftConstants.OrderStatus;
import lombok.extern.java.Log;

/**
 * represent this app <-> starcraft bridge. This class is the main entry point
 * for all commands to be sent to the game.
 */
@Log
public class CommandQueue {
    public static enum ResourceType {
        Mineral,
        Gas
    }

    public static Command currentCommand;
    private static ArrayList<Command> commands = new ArrayList<Command>();
    private static ArrayList<CommandQueueListener> listeners = new ArrayList<CommandQueueListener>();

    public static void addListener(CommandQueueListener listener) {
        listeners.add(listener);
    }

    /**
     * Call by RBWListener on every x frames to dispatch all queued commands to the
     * starcraft game.
     */
    public static void dispatchCommands() {
        Game game = RBWListener.bwClient.getGame();

        for (Command command : commands) {
            if (command.status != OrderStatus.Queued)
                continue;

            currentCommand = command;
            boolean success = true;
            boolean ignored = false;

            Unit unit = null;
            if (command.unitId != -1)
                unit = game.getUnit(command.unitId);

            Unit targetUnit = null;
            if (command.targetId != -1)
                targetUnit = game.getUnit(command.targetId);

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
                    success = unit.build(command.unitType, command.tilePosition); // <-----------------------
                    break;
                case UnitCommandType.Build_Addon:
                    success = unit.buildAddon(command.unitType);
                    break;
                case UnitCommandType.Train:
                    // the eassy way to check if a trainner can accept more units in to check if it
                    // throws an IndexOutOfBoundsException. if so, silend ignore the command and the
                    // command.status remain Queued until more room are available
                    try {
                        success = unit.train(command.unitType);
                    } catch (IndexOutOfBoundsException e) {
                        command.message = "Trainer is full";
                        ignored = true;
                    }
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
                    success = unit.land(command.tilePosition);
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

            // silent ignore
            if (ignored) {
                listeners.forEach(listener -> listener.update(commands));
                return;
            }

            command.status = success ? OrderStatus.Completed : OrderStatus.Error;
            if (command.status == OrderStatus.Error) {
                command.message = "Minerals:" + RBWListener.currentMinerals +
                        ", Gas:" + RBWListener.currentGas +
                        ", Supply:" + RBWListener.currentSupplyLeft;

            }

            listeners.forEach(listener -> listener.update(commands));
        }
    }

    public static List<Command> addCommand(UnitCommandType command, Squad squad, Position position) {
        List<Command> addedCommands = new ArrayList<>();
        for (DogTag tag : squad.getAliveMembers()) {
            Command command2 = new Command(command, tag.unit.getID(), position);
            addCommand(command2);
            addedCommands.add(command2);
        }
        return addedCommands;
    }

    private static Command addCommand(UnitCommandType command, int unitID, int targetUnit, Position position) {
        Command command2 = new Command(command, unitID, targetUnit, position);
        addCommand(command2);
        return command2;
    }

    private static void addCommand(Command command) {
        // iff exist the same command with status pending, return silently
        Optional<Command> optional = commands.stream()
                .filter(c -> c.order == command.order && c.status == OrderStatus.Queued).findFirst();
        if (optional.isPresent())
            return;

        commands.add(command);
        listeners.forEach(listener -> listener.update(commands));
    }

    public static Command gather(ResourceType resourceType) {
        Command command = new Command(UnitCommandType.Gather, -1, -1, null);

        // select idle SCV
        Unit dTag = UnitsCenter.getIdleTerranSCV();
        if (dTag == null) {
            return logFail(command, "No SCV available to gather resources.");
        }

        // select closest resource
        Unit resourceUnit = null;
        if (resourceType == ResourceType.Mineral) {
            resourceUnit = RBWListener.game.getClosestUnit(dTag.getPosition(), UnitFilter.IsMineralField);
        } else {
            resourceUnit = RBWListener.game.getClosestUnit(dTag.getPosition(), UnitFilter.IsRefinery);
        }

        return addCommand(UnitCommandType.Gather, dTag.getID(), resourceUnit.getID(), null);
    }

    public static void gather(int unitID, int targetID) {
        addCommand(UnitCommandType.Gather, unitID, targetID, null);
    }

    /**
     * Tells the unit to attack another unit.
     * 
     * // virtual bool attackUnit(Unit* target) = 0;
     */
    public static void attackUnit(int unitID, int targetID) {
        addCommand(UnitCommandType.Attack_Unit, unitID, targetID, null);
    }

    /**
     * Tells the unit to right click (move) to the specified location
     * 
     */
    public static void rightClick(Squad squad, Position position) {
        for (DogTag tag : squad.getAliveMembers()) {
            addCommand(UnitCommandType.Right_Click_Position, tag.unit.getID(), -1, position);
        }
    }

    /**
     * Tells the unit to right click (move) on the specified target unit
     * (Includes resources).
     * 
     * // virtual bool rightClick(Unit* target) = 0;
     */
    public static void rightClick(int unitID, int targetID) {
        addCommand(UnitCommandType.Right_Click_Unit, unitID, targetID, null);
    }

    public static Command train(UnitType unitType) {
        Command command = new Command(UnitCommandType.Train, -1, -1, null);
        command.unitType = unitType;

        // fail save to forece the corrent pddl domain action
        if (unitType.isBuilding()) {
            logFail(command, "the UnitType " + command.unitType + " is not a trainable unit.");
            return command;
        }

        // resolve facility
        Unit dTag = UnitsCenter.resolveTrainer(unitType);
        if (dTag == null) {
            logFail(command, "No Facility available to train " + unitType);
            return command;
        }
        command.unitId = dTag.getID();
        command.unitType = unitType;
        addCommand(command);
        return command;
    }

    public static Command build(UnitType unitType) {
        Command command = addCommand(UnitCommandType.Build, -1, -1, null);
        command.unitType = unitType;

        // fail save to force the corrent pddl domain action
        if (!unitType.isBuilding()) {
            logFail(command, "the UnitType " + command.unitType + " is not a building.");
            return command;
        }

        // look for a free SCV to build the unit
        Unit unit = UnitsCenter.getFreeTerranSCV();
        if (unit == null) {
            // make a note but dont alter the status
            command.message = "No SCV available to build " + command.unitType;
            // logFail(command, "No SCV available to build " + command.unitType);
            return command;
        }
        command.unitId = unit.getID();

        // if the unit to build is a refinery, find the closest geyser
        if (unitType == UnitType.Terran_Refinery) {
            List<Unit> geysers = RBWListener.game.getGeysers();
            Unit closestGeyser = UnitsCenter.getClosest(geysers, unit);
            if (closestGeyser == null) {
                logFail(command, "No Geyser available to build " + command.unitType);
                return command;
            }

            command.tilePosition = closestGeyser.getTilePosition();
            addCommand(command);

            return command;
        }

        // if the unit to build is a bunker, find a suitable location.
        // if (unitType == UnitType.Terran_Bunker) {
        //     BWMap map = RBWListener.bwem.getMap();
        //     TilePosition startPosition = RBWListener.game.self().getStartLocation();

        //     // check chokepoints
        //     List<ChokePoint> chokePoints = map.getChokePoints();
        //     ChokePoint chokePoint = null;
        //     double distance = Integer.MAX_VALUE;
        //     for (ChokePoint cp : chokePoints) {
        //         TilePosition cpPosition = cp.getCenter().toTilePosition();
        //         double dist = startPosition.getDistance(cpPosition);
        //         if (dist < distance) {
        //             chokePoint = cp;
        //             distance = dist;
        //         }
        //     }

        //     if (chokePoint != null) {
        //         command.tilePosition = chokePoint.getCenter().toTilePosition();
        //         addCommand(command);
        //         return command;
        //     }

            // if no chokepoint, fall back to the start location
            // command.message = "No chokepoint available to build " + command.unitType;
        // }

        // if the unit to build is a building, find a suitable location
        TilePosition tilePosition = RBWListener.game.self().getStartLocation();
        tilePosition = RBWListener.game.getBuildLocation(command.unitType, tilePosition);
        command.tilePosition = tilePosition;
        addCommand(command);

        return command;
    }

    /**
     * Tells the building to build the specified add on.
     * 
     * // virtual bool buildAddon(UnitType type) = 0;
     */
    public static void buildAddon(int unitID, UnitType unitType) {
        Command command = addCommand(UnitCommandType.Build_Addon, unitID, -1, null);
        command.unitType = unitType;
    }

    /**
     * Tells the building to research the specified tech type.
     * 
     * // virtual bool research(TechType tech) = 0;
     */
    public static void research(int unitID, TechType techType) {
        Command command = addCommand(UnitCommandType.Research, unitID, -1, null);
        command.techType = techType;
    }

    /**
     * Tells the building to upgrade the specified upgrade type.
     * 
     * // virtual bool upgrade(UpgradeType upgrade) = 0;
     */
    public static void upgrade(int unitID, UpgradeType upgradeType) {
        Command command = addCommand(UnitCommandType.Upgrade, unitID, -1, null);
        command.upgradeType = upgradeType;
    }

    /**
     * Orders the unit to stop moving. The unit will chase enemies that enter its
     * vision.
     * 
     * // virtual bool stop() = 0;
     */
    public static void stop(int unitID) {
        addCommand(UnitCommandType.Stop, unitID, -1, null);
    }

    public static void holdPosition(int unitID) {
        addCommand(UnitCommandType.Hold_Position, unitID, -1, null);
    }

    /**
     * Orders a unit to follow a target unit.
     * 
     * // virtual bool follow(Unit* target) = 0;
     */
    public static void follow(int unitID, int targetID) {
        addCommand(UnitCommandType.Follow, unitID, targetID, null);
    }

    /**
     * Sets the rally location for a building.
     * 
     * // virtual bool setRallyPosition(Position target) = 0;
     */
    public static void setRallyPosition(int unitID, Position position) {
        addCommand(UnitCommandType.Set_Rally_Position, unitID, -1, position);
    }

    /**
     * Sets the rally location for a building based on the target unit's current
     * position.
     * 
     * // virtual bool setRallyUnit(Unit* target) = 0;
     */
    public static void setRallyUnit(int unitID, int targetID) {
        addCommand(UnitCommandType.Set_Rally_Unit, unitID, targetID, null);
    }

    /**
     * Instructs an SCV to repair a target unit.
     * 
     * // virtual bool repair(Unit* target) = 0;
     */
    public static void repair(int unitID, int targetID) {
        addCommand(UnitCommandType.Repair, unitID, targetID, null);
    }

    /**
     * Orders a zerg unit to morph to a different unit type.
     * 
     * // virtual bool morph(UnitType type) = 0;
     */
    public static void morph(int unitID, UnitType unitType) {
        Command command = addCommand(UnitCommandType.Morph, unitID, -1, null);
        command.unitType = unitType;
    }

    /**
     * Tells a zerg unit to burrow. Burrow must be upgraded for non-lurker units.
     * 
     * // virtual bool burrow() = 0;
     */
    public static void burrow(int unitID) {
        addCommand(UnitCommandType.Burrow, unitID, -1, null);
    }

    /**
     * Tells a burrowed unit to unburrow.
     * 
     * // virtual bool unburrow() = 0;
     */
    public static void unburrow(int unitID) {
        addCommand(UnitCommandType.Unburrow, unitID, -1, null);
    }

    /**
     * Orders a siege tank to siege.
     * 
     * // virtual bool siege() = 0;
     */
    public static void siege(int unitID) {
        addCommand(UnitCommandType.Siege, unitID, -1, null);
    }

    /**
     * Orders a siege tank to un-siege.
     * 
     * // virtual bool unsiege() = 0;
     */
    public static void unsiege(int unitID) {
        addCommand(UnitCommandType.Unsiege, unitID, -1, null);
    }

    /**
     * Tells a unit to cloak. Works for ghost and wraiths.
     * 
     * // virtual bool cloak() = 0;
     */
    public static void cloak(int unitID) {
        addCommand(UnitCommandType.Cloak, unitID, -1, null);
    }

    /**
     * Tells a unit to decloak, works for ghosts and wraiths.
     * 
     * // virtual bool decloak() = 0;
     */
    public static void decloak(int unitID) {
        addCommand(UnitCommandType.Decloak, unitID, -1, null);
    }

    /**
     * Commands a Terran building to lift off.
     * 
     * // virtual bool lift() = 0;
     */
    public static void lift(int unitID) {
        addCommand(UnitCommandType.Lift, unitID, -1, null);
    }

    /**
     * Commands a terran building to land at the specified location.
     * 
     * // virtual bool land(TilePosition position) = 0;
     */
    public static void land(int unitID, Position position) {
        addCommand(UnitCommandType.Land, unitID, -1, position);
    }

    /**
     * Orders the transport unit to load the target unit.
     * 
     * // virtual bool load(Unit* target) = 0;
     */
    public static void load(int unitID, int targetID) {
        addCommand(UnitCommandType.Load, unitID, targetID, null);
    }

    /**
     * Orders a transport unit to unload the target unit at the current transport
     * location.
     * 
     * // virtual bool unload(Unit* target) = 0;
     */
    public static void unload(int unitID, int targetID) {
        addCommand(UnitCommandType.Unload, unitID, targetID, null);
    }

    /**
     * Orders a transport to unload all units at the current location.
     * 
     * // virtual bool unloadAll() = 0;
     */
    public static void unloadAll(int unitID) {
        addCommand(UnitCommandType.Unload_All, unitID, -1, null);
    }

    /**
     * Orders a unit to unload all units at the target location.
     * 
     * // virtual bool unloadAll(Position position) = 0;
     */
    public static void unloadAll(int unitID, Position position) {
        addCommand(UnitCommandType.Unload_All_Position, unitID, -1, position);
    }

    /**
     * Orders a being to stop being constructed.
     * 
     * // virtual bool cancelConstruction() = 0;
     */
    public static void cancelConstruction(int unitID) {
        addCommand(UnitCommandType.Cancel_Construction, unitID, -1, null);
    }

    /**
     * Tells an scv to pause construction on a building.
     * 
     * // virtual bool haltConstruction() = 0;
     */
    public static void haltConstruction(int unitID) {
        addCommand(UnitCommandType.Halt_Construction, unitID, -1, null);
    }

    /**
     * Orders a zerg unit to stop morphing.
     * 
     * // virtual bool cancelMorph() = 0;
     */
    public static void cancelMorph(int unitID) {
        addCommand(UnitCommandType.Cancel_Morph, unitID, -1, null);
    }

    /**
     * Tells a building to remove the last unit from its training queue.
     * 
     * // virtual bool cancelTrain() = 0;
     */
    public static void cancelTrain(int unitID) {
        addCommand(UnitCommandType.Cancel_Train, unitID, -1, null);
    }

    /**
     * Tells a building to remove a specific unit from its queue.
     * 
     * // virtual bool cancelTrain(int slot) = 0;
     */
    public static void cancelTrain(int unitID, int slot) {
        addCommand(UnitCommandType.Cancel_Train_Slot, unitID, slot, null);
    }

    /**
     * Orders a Terran building to stop constructing an add on.
     * 
     * // virtual bool cancelAddon() = 0;
     */
    public static void cancelAddon(int unitID) {
        addCommand(UnitCommandType.Cancel_Addon, unitID, -1, null);
    }

    /***
     * Tells a building cancel a research in progress.
     * 
     * // virtual bool cancelResearch() = 0;
     */
    public static void cancelResearch(int unitID) {
        addCommand(UnitCommandType.Cancel_Research, unitID, -1, null);
    }

    /***
     * Tells a building cancel an upgrade in progress.
     * 
     * // virtual bool cancelUpgrade() = 0;
     */
    public static void cancelUpgrade(int unitID) {
        addCommand(UnitCommandType.Cancel_Upgrade, unitID, -1, null);
    }

    /**
     * Tells the unit to use the specified tech, (i.e. STEM PACKS)
     * 
     * // virtual bool useTech(TechType tech) = 0;
     */
    public static void useTech(int unitID, TechType techType) {
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
    public static void useTech(int unitID, TechType techType, Position position) {
        addCommand(UnitCommandType.Use_Tech_Position, unitID, -1, position);
    }

    /**
     * Tells the unit to use tech on the target unit.
     * 
     * Note: for targeted spells such as irradiate.
     * 
     * // virtual bool useTech(TechType tech, Unit* target) = 0;
     */
    public static void useTech(int unitID, TechType techType, int targetID) {
        addCommand(UnitCommandType.Use_Tech_Unit, unitID, targetID, null);
    }

    public static Command logSuccess(Command command, String message) {
        log.info("SUCCEEDED: " + message);
        command.status = OrderStatus.Completed;
        command.message = message;
        return command;
    }

    public static Command logFail(Command command, String message) {
        log.info("FAILED: " + message);
        command.status = OrderStatus.Error;
        command.message = message;
        return command;
    }
}
