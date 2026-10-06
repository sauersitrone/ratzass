package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import bwapi.Game;
import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitFilter;
import bwapi.UnitType;
import bwapi.UpgradeType;
import de.simone.RBWListener;
import de.simone.command.StarCraftConstants.OrderStatus;

/**
 * represent this app <-> starcraft bridge. This class is the main entry point
 * for all commands to be sent to the game.
 */
public class CommandQueue {
    public static enum ResourceType {
        Mineral,
        Gas
    }

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

            RBWListener.lastCommand = command;
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
                case UnitCommandType.Unknown:
                case UnitCommandType.Place_COP:
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
                    if (unit.getTrainingQueueCount() == 5) {
                        command.message = "Trainer is full";
                        ignored = true;
                    } else {
                        success = unit.train(command.unitType); // <-----------------------
                    }
                    break;
                case UnitCommandType.Research:
                    success = unit.research(command.techType); // ------------------------------
                    break;
                case UnitCommandType.Upgrade:
                    success = unit.upgrade(command.upgradeType); // ------------------------------
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

            if (!success) {
                command.message = "Minerals:" + RBWListener.currentMinerals +
                        ", Gas:" + RBWListener.currentGas +
                        ", Supply:" + RBWListener.currentSupplyLeft;
                command.trys++;
            }

            if (command.trys > 20 && !success) {
                command.status = OrderStatus.Error;
                listeners.forEach(listener -> listener.update(commands));
                return;
            }

            command.status = success ? OrderStatus.Completed : OrderStatus.Queued;

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

    private static void addCommand(Command command) {
        // fail save to avoid duplicate commands in the queue.
        // Optional<Command> optional = commands.stream()
        //         .filter(c -> c.order == command.order && c.status == OrderStatus.Queued).findFirst();
        // if (optional.isPresent())
        //     throw new StarCraftException("Duplicate command " + command.order + " in queue. Command: " + command);

        commands.add(command);
        listeners.forEach(listener -> listener.update(commands));
    }

    public static Command gather(Unit unit, ResourceType resourceType) {
        Command command = new Command(UnitCommandType.Gather, -1, -1, null);

        // select closest resource
        Unit resourceUnit = null;
        if (resourceType == ResourceType.Mineral) {
            resourceUnit = RBWListener.game.getClosestUnit(unit.getPosition(), UnitFilter.IsMineralField);
        } else {
            resourceUnit = RBWListener.game.getClosestUnit(unit.getPosition(), UnitFilter.IsRefinery);
        }

        command.unitId = unit.getID();
        command.targetId = resourceUnit.getID();
        addCommand(command);
        return command;
    }

    /**
     * Tells the unit to attack another unit.
     * 
     * // virtual bool attackUnit(Unit* target) = 0;
     */
    public static void attackUnit(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Attack_Unit, unitID, targetID, null));
    }

    /**
     * Tells the unit to right click (move) to the specified location
     * 
     */
    public static void rightClick(Squad squad, Position position) {
        for (DogTag tag : squad.getAliveMembers()) {
            addCommand(new Command(UnitCommandType.Right_Click_Position, tag.unit.getID(), -1, position));
        }
    }

    /**
     * Tells the unit to right click (move) on the specified target unit
     * (Includes resources).
     * 
     * // virtual bool rightClick(Unit* target) = 0;
     */
    public static void rightClick(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Right_Click_Unit, unitID, targetID, null));
    }

    public static Command train(UnitType unitType) {
        Command command = new Command(UnitCommandType.Train, -1, -1, null);
        command.unitType = unitType;

        // fail save to forece the corrent pddl domain action
        if (unitType.isBuilding()) {
            setFail(command, "the UnitType " + command.unitType + " is not a trainable unit.");
            return command;
        }

        // resolve facility
        Unit trainer = UnitsCenter.resolveTrainer(unitType);
        if (trainer == null) {
            setFail(command, "No Facility available to train " + unitType);
            return command;
        }
        command.unitId = trainer.getID();
        command.unitType = unitType;
        addCommand(command);
        return command;
    }

    public static Command build(UnitType unitType) {
        Command command = new Command(UnitCommandType.Build, -1, -1, null);
        command.unitType = unitType;

        // fail save to force the corrent pddl domain action
        if (!unitType.isBuilding()) {
            setFail(command, "the UnitType " + command.unitType + " is not a building.");
            return command;
        }

        // look for a free SCV to build the unit
        Unit unit = UnitsCenter.getFreeTerranSCV();
        if (unit == null) {
            // make a note but dont alter the status
            command.message = "No SCV available to build " + command.unitType;
            return command;
        }
        command.unitId = unit.getID();

        // if the unit to build is a refinery, find the closest geyser
        if (unitType == UnitType.Terran_Refinery) {
            List<Unit> geysers = RBWListener.game.getGeysers();
            Unit closestGeyser = UnitsCenter.getClosestUnit(geysers, unit);
            if (closestGeyser == null) {
                setFail(command, "No Geyser available to build " + command.unitType);
                return command;
            }
            command.tilePosition = closestGeyser.getTilePosition();
            addCommand(command);
            return command;
        }

        // if the unit to build is a bunker, find a suitable location.
        if (unitType == UnitType.Terran_Bunker) {
            TilePosition buildPosition = CombatCenter.getBunkerLocation();
            if (buildPosition != null) {
                command.tilePosition = buildPosition;
                addCommand(command);
                return command;
            }
        }

        // standard build location if no special location is found
        TilePosition tilePosition = RBWListener.game.self().getStartLocation();
        tilePosition = RBWListener.game.getBuildLocation(command.unitType, tilePosition);
        command.tilePosition = tilePosition;
        addCommand(command);

        return command;
    }

    /**
     * Tells the building to build the specified add on.
     * 
     */
    public static void buildAddon(int unitID, UnitType unitType) {
        Command command = new Command(UnitCommandType.Build_Addon, unitID, -1, null);
        command.unitType = unitType;
        addCommand(command);
    }

    /**
     * Tells the building to research the specified tech type.
     * 
     */
    public static Command research(TechType techType) {
        Command command = new Command(UnitCommandType.Research, -1, -1, null);
        command.techType = techType;

        // resolve research facility
        Unit dTag = UnitsCenter.resolveResearch(techType);
        if (dTag == null) {
            setFail(command, "No Facility available to research " + techType);
            return command;
        }
        command.unitId = dTag.getID();
        addCommand(command);
        return command;
    }

    /**
     * Tells the building to upgrade the specified upgrade type.
     * 
     */
    public static Command upgrade(UpgradeType upgradeType) {
        Command command = new Command(UnitCommandType.Upgrade, -1, -1, null);
        command.upgradeType = upgradeType;

        // resolve upgrade facility
        Unit dTag = UnitsCenter.resolveUpgrade(upgradeType);
        if (dTag == null) {
            setFail(command, "No Facility available to upgrade " + upgradeType);
            return command;
        }
        command.unitId = dTag.getID();
        addCommand(command);
        return command;
    }

    /**
     * Orders the unit to stop moving. The unit will chase enemies that enter its
     * vision.
     * 
     */
    public static void stop(int unitID) {
        addCommand(new Command(UnitCommandType.Stop, unitID, -1, null));
    }

    public static void holdPosition(int unitID) {
        addCommand(new Command(UnitCommandType.Hold_Position, unitID, -1, null));
    }

    /**
     * Orders a unit to follow a target unit.
     * 
     */
    public static void follow(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Follow, unitID, targetID, null));
    }

    /**
     * Sets the rally location for a building.
     * 
     */
    public static void setRallyPosition(int unitID, Position position) {
        addCommand(new Command(UnitCommandType.Set_Rally_Position, unitID, -1, position));
    }

    /**
     * Sets the rally location for a building based on the target unit's current
     * position.
     * 
     * 
     */
    public static void setRallyUnit(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Set_Rally_Unit, unitID, targetID, null));
    }

    /**
     * Instructs an SCV to repair a target unit.
     * 
     */
    public static Command repair(int targetID) {
        Command command = new Command(UnitCommandType.Repair, -1, targetID, null);

        // look for a free SCV to build the unit
        Unit unit = UnitsCenter.getFreeTerranSCV();
        if (unit == null) {
            // make a note but dont alter the status
            command.message = "No SCV available to build " + command.unitType;
            return command;
        }
        command.unitId = unit.getID();

        addCommand(command);
        return command;
    }

    /**
     * Orders a zerg unit to morph to a different unit type.
     * 
     * 
     */
    public static void morph(int unitID, UnitType unitType) {
        Command command = new Command(UnitCommandType.Morph, unitID, -1, null);
        command.unitType = unitType;
        addCommand(command);
    }

    /**
     * Tells a zerg unit to burrow. Burrow must be upgraded for non-lurker units.
     * 
     * 
     */
    public static void burrow(int unitID) {
        addCommand(new Command(UnitCommandType.Burrow, unitID, -1, null));
    }

    /**
     * Tells a burrowed unit to unburrow.
     * 
     * 
     */
    public static void unburrow(int unitID) {
        addCommand(new Command(UnitCommandType.Unburrow, unitID, -1, null));
    }

    /**
     * Orders a siege tank to siege.
     * 
     * 
     */
    public static void siege(int unitID) {
        addCommand(new Command(UnitCommandType.Siege, unitID, -1, null));
    }

    /**
     * Orders a siege tank to un-siege.
     * 
     * 
     */
    public static void unsiege(int unitID) {
        addCommand(new Command(UnitCommandType.Unsiege, unitID, -1, null));
    }

    /**
     * Tells a unit to cloak. Works for ghost and wraiths.
     * 
     * 
     */
    public static void cloak(int unitID) {
        addCommand(new Command(UnitCommandType.Cloak, unitID, -1, null));
    }

    /**
     * Tells a unit to decloak, works for ghosts and wraiths.
     * 
     * 
     */
    public static void decloak(int unitID) {
        addCommand(new Command(UnitCommandType.Decloak, unitID, -1, null));
    }

    /**
     * Commands a Terran building to lift off.
     * 
     * 
     */
    public static void lift(int unitID) {
        addCommand(new Command(UnitCommandType.Lift, unitID, -1, null));
    }

    /**
     * Commands a terran building to land at the specified location.
     * 
     * 
     */
    public static void land(int unitID, Position position) {
        addCommand(new Command(UnitCommandType.Land, unitID, -1, position));
    }

    /**
     * Orders the transport unit to load the target unit.
     * 
     * 
     */
    public static void load(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Load, unitID, targetID, null));
    }

    /**
     * Orders a transport unit to unload the target unit at the current transport
     * location.
     * 
     */
    public static void unload(int unitID, int targetID) {
        addCommand(new Command(UnitCommandType.Unload, unitID, targetID, null));
    }

    /**
     * Orders a transport to unload all units at the current location.
     * 
     * 
     */
    public static void unloadAll(int unitID) {
        addCommand(new Command(UnitCommandType.Unload_All, unitID, -1, null));
    }

    /**
     * Orders a unit to unload all units at the target location.
     * 
     * 
     */
    public static void unloadAll(int unitID, Position position) {
        addCommand(new Command(UnitCommandType.Unload_All_Position, unitID, -1, position));
    }

    /**
     * Orders a being to stop being constructed.
     * 
     */
    public static void cancelConstruction(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Construction, unitID, -1, null));
    }

    /**
     * Tells an scv to pause construction on a building.
     * 
     */
    public static void haltConstruction(int unitID) {
        addCommand(new Command(UnitCommandType.Halt_Construction, unitID, -1, null));
    }

    /**
     * Orders a zerg unit to stop morphing.
     * 
     */
    public static void cancelMorph(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Morph, unitID, -1, null));
    }

    /**
     * Tells a building to remove the last unit from its training queue.
     * 
     * 
     */
    public static void cancelTrain(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Train, unitID, -1, null));
    }

    /**
     * Tells a building to remove a specific unit from its queue.
     * 
     * 
     */
    public static void cancelTrain(int unitID, int slot) {
        addCommand(new Command(UnitCommandType.Cancel_Train_Slot, unitID, slot, null));
    }

    /**
     * Orders a Terran building to stop constructing an add on.
     * 
     * 
     */
    public static void cancelAddon(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Addon, unitID, -1, null));
    }

    /***
     * Tells a building cancel a research in progress.
     * 
     * 
     */
    public static void cancelResearch(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Research, unitID, -1, null));
    }

    /***
     * Tells a building cancel an upgrade in progress.
     * 
     * 
     */
    public static void cancelUpgrade(int unitID) {
        addCommand(new Command(UnitCommandType.Cancel_Upgrade, unitID, -1, null));
    }

    /**
     * Tells the unit to use the specified tech, (i.e. STEM PACKS)
     * 
     * 
     */
    public static void useTech(int unitID, TechType techType) {
        Command command = new Command(UnitCommandType.Use_Tech, unitID, -1, null);
        command.techType = techType;
        addCommand(command);
    }

    /**
     * Tells the unit to use tech at the target location.
     * 
     * Note: for AOE spells such as plague.
     * 
     * 
     */
    public static void useTech(int unitID, TechType techType, Position position) {
        addCommand(new Command(UnitCommandType.Use_Tech_Position, unitID, -1, position));
    }

    /**
     * Tells the unit to use tech on the target unit.
     * 
     * Note: for targeted spells such as irradiate.
     * 
     * 
     */
    public static void useTech(int unitID, TechType techType, int targetID) {
        addCommand(new Command(UnitCommandType.Use_Tech_Unit, unitID, targetID, null));
    }

    private static Command setSuccess(Command command, String message) {
        command.status = OrderStatus.Completed;
        command.message = message;
        return command;
    }

    private static Command setFail(Command command, String message) {
        command.status = OrderStatus.Error;
        command.message = message;
        return command;
    }
}
