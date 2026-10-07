package de.simone.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import bwapi.UnitCommandType;

public class StarCraftConstants {

    public static final int SCV_GATHERING_GAS = 2;
    public static final int MINERAL_LOAD = 5;
    public static final int GAS_LOAD = 4;
    public static int idGenerator = 1;

    // general status of a order (eg. buildOrder or combatOrder)
    public enum OrderStatus {
        Queued,
        Running, // only one order can be running at a time.
        Error,
        Completed
    }

    public enum OrderPriority {
        Normal,
        High
    }

    /**
     * Represents the different types of planned actions in the PDDL domain. Each
     * action corresponds to a specific task that the bot can plan and execute. see
     * the pddl file fomr more
     * PlannedAction
     */
    public enum PlannedAction {
        gather_Mineral,
        gather_Gas,
        train,
        build,
        upgrade,
        research
    }

    public enum SquadMoralStatus {
        Broken,
        Low,
        Steady,
        High,
        Fearless;
    }

    /**
     * A list of cool squad names that can be used to assign unique identifiers to
     * squads. The name are unique. only to add some flavor to the game. and i will
     * no run out of names anytime soon. :D
     */
    public static List<String> coolSquadNames = new ArrayList<String>();
    static {
        coolSquadNames.add("Alpha");
        coolSquadNames.add("Bravo");
        coolSquadNames.add("Charlie");
        coolSquadNames.add("Delta");
        coolSquadNames.add("Echo");
        coolSquadNames.add("Foxtrot");
        coolSquadNames.add("Golf");
        coolSquadNames.add("Hotel");
        coolSquadNames.add("India");
        coolSquadNames.add("Juliet");
        coolSquadNames.add("Kilo");
        coolSquadNames.add("Lima");
        coolSquadNames.add("Mike");
        coolSquadNames.add("November");
        coolSquadNames.add("Oscar");
        coolSquadNames.add("Papa");
        coolSquadNames.add("Quebec");
        coolSquadNames.add("Romeo");
        coolSquadNames.add("Sierra");
        coolSquadNames.add("Tango");
        coolSquadNames.add("Uniform");
        coolSquadNames.add("Victor");
        coolSquadNames.add("Whiskey");
        coolSquadNames.add("X-ray");
        coolSquadNames.add("Yankee");
        coolSquadNames.add("Zulu");
    }

    /**
     * List of quotes associated with different unit command types. Each entry pairs
     * a command type with a corresponding quote. only to make the bot fun to
     * interact with.
     */
    public static final List<Pair<UnitCommandType, String>> quotes = List.of(
            Pair.of(UnitCommandType.Attack_Move, "Squad, advance on my mark!"),
            Pair.of(UnitCommandType.Attack_Move, "Moving to engage!"),
            Pair.of(UnitCommandType.Attack_Unit, "Target identified; weapons free!"),
            Pair.of(UnitCommandType.Attack_Unit, "Engaging the target!"),
            Pair.of(UnitCommandType.Build, "Engineering team, start construction!"),
            Pair.of(UnitCommandType.Build, "Setting up the site now!"),
            Pair.of(UnitCommandType.Build_Addon, "Addon crew, get to work!"),
            Pair.of(UnitCommandType.Build_Addon, "Preparing the upgrade module!"),
            Pair.of(UnitCommandType.Train, "Recruits, report for duty!"),
            Pair.of(UnitCommandType.Train, "Training detail underway!"),
            Pair.of(UnitCommandType.Morph, "Beginning transformation!"),
            Pair.of(UnitCommandType.Morph, "Changing configuration now!"),
            Pair.of(UnitCommandType.Research, "Research team, begin the project!"),
            Pair.of(UnitCommandType.Research, "Starting research now!"),
            Pair.of(UnitCommandType.Upgrade, "Upgrade crew, make it happen!"),
            Pair.of(UnitCommandType.Upgrade, "Improving our systems now!"),
            Pair.of(UnitCommandType.Set_Rally_Position, "Rally point marked!"),
            Pair.of(UnitCommandType.Set_Rally_Position, "New rally coordinates received!"),
            Pair.of(UnitCommandType.Set_Rally_Unit, "We'll rally on that unit!"),
            Pair.of(UnitCommandType.Set_Rally_Unit, "Squad rally target assigned!"),
            Pair.of(UnitCommandType.Move, "On the move!"),
            Pair.of(UnitCommandType.Move, "Moving out, squad!"),
            Pair.of(UnitCommandType.Patrol, "Patrol route acknowledged!"),
            Pair.of(UnitCommandType.Patrol, "We'll keep watch along that route!"),
            Pair.of(UnitCommandType.Hold_Position, "Holding this position!"),
            Pair.of(UnitCommandType.Hold_Position, "This ground is ours!"),
            Pair.of(UnitCommandType.Stop, "Halting here!"),
            Pair.of(UnitCommandType.Stop, "Standing down and holding!"),
            Pair.of(UnitCommandType.Follow, "We'll stay on your six!"),
            Pair.of(UnitCommandType.Follow, "Following your lead!"),
            Pair.of(UnitCommandType.Gather, "Collecting resources now!"),
            Pair.of(UnitCommandType.Gather, "Gathering detail, moving out!"),
            Pair.of(UnitCommandType.Return_Cargo, "Cargo inbound!"),
            Pair.of(UnitCommandType.Return_Cargo, "Returning supplies to base!"),
            Pair.of(UnitCommandType.Repair, "Repair crew, on the job!"),
            Pair.of(UnitCommandType.Repair, "We'll get it back in shape!"),
            Pair.of(UnitCommandType.Burrow, "Going underground!"),
            Pair.of(UnitCommandType.Burrow, "Taking cover below!"),
            Pair.of(UnitCommandType.Unburrow, "Coming back up!"),
            Pair.of(UnitCommandType.Unburrow, "Surface team, move!"),
            Pair.of(UnitCommandType.Cloak, "Cloaking systems engaged!"),
            Pair.of(UnitCommandType.Cloak, "Disappearing from their scopes!"),
            Pair.of(UnitCommandType.Decloak, "Cloak disengaged!"),
            Pair.of(UnitCommandType.Decloak, "We're visible again!"),
            Pair.of(UnitCommandType.Siege, "Deploying siege mode!"),
            Pair.of(UnitCommandType.Siege, "Locking down this sector!"),
            Pair.of(UnitCommandType.Unsiege, "Siege mode disengaged!"),
            Pair.of(UnitCommandType.Unsiege, "Pack it up; we're moving!"),
            Pair.of(UnitCommandType.Lift, "Lifting off!"),
            Pair.of(UnitCommandType.Lift, "Taking this base airborne!"),
            Pair.of(UnitCommandType.Land, "Preparing to land!"),
            Pair.of(UnitCommandType.Land, "Setting down at the new site!"),
            Pair.of(UnitCommandType.Load, "Boarding now!"),
            Pair.of(UnitCommandType.Load, "Squad, load up!"),
            Pair.of(UnitCommandType.Unload, "Disembarking!"),
            Pair.of(UnitCommandType.Unload, "Clear the transport!"),
            Pair.of(UnitCommandType.Unload_All, "Everyone out, move!"),
            Pair.of(UnitCommandType.Unload_All, "Full squad disembark!"),
            Pair.of(UnitCommandType.Unload_All_Position, "Deploying at the marked position!"),
            Pair.of(UnitCommandType.Unload_All_Position, "Transport unloading at coordinates!"),
            Pair.of(UnitCommandType.Right_Click_Position, "Heading to the designated point!"),
            Pair.of(UnitCommandType.Right_Click_Position, "Coordinates received; moving!"),
            Pair.of(UnitCommandType.Right_Click_Unit, "Moving alongside that unit!"),
            Pair.of(UnitCommandType.Right_Click_Unit, "We'll assist that unit!"),
            Pair.of(UnitCommandType.Halt_Construction, "Construction halted!"),
            Pair.of(UnitCommandType.Halt_Construction, "Work crew, stand by!"),
            Pair.of(UnitCommandType.Cancel_Construction, "Cancel the build; clear the site!"),
            Pair.of(UnitCommandType.Cancel_Construction, "Construction called off!"),
            Pair.of(UnitCommandType.Cancel_Addon, "Addon work cancelled!"),
            Pair.of(UnitCommandType.Cancel_Addon, "Stand down, addon crew!"),
            Pair.of(UnitCommandType.Cancel_Train, "Training cancelled!"),
            Pair.of(UnitCommandType.Cancel_Train, "Recruits, return to your posts!"),
            Pair.of(UnitCommandType.Cancel_Train_Slot, "Cancel that training slot!"),
            Pair.of(UnitCommandType.Cancel_Train_Slot, "One recruit slot removed!"),
            Pair.of(UnitCommandType.Cancel_Morph, "Transformation cancelled!"),
            Pair.of(UnitCommandType.Cancel_Morph, "Reverting to the original configuration!"),
            Pair.of(UnitCommandType.Cancel_Research, "Research operation cancelled!"),
            Pair.of(UnitCommandType.Cancel_Research, "Research team, stand down!"),
            Pair.of(UnitCommandType.Cancel_Upgrade, "Upgrade order cancelled!"),
            Pair.of(UnitCommandType.Cancel_Upgrade, "Upgrade crew, halt work!"),
            Pair.of(UnitCommandType.Use_Tech, "Deploying special equipment!"),
            Pair.of(UnitCommandType.Use_Tech, "Special ability activated!"),
            Pair.of(UnitCommandType.Use_Tech_Position, "Deploying ability at the marked location!"),
            Pair.of(UnitCommandType.Use_Tech_Position, "Special deployment, coordinates confirmed!"),
            Pair.of(UnitCommandType.Use_Tech_Unit, "Using the ability on that target!"),
            Pair.of(UnitCommandType.Use_Tech_Unit, "Target locked; deploying special equipment!"),
            Pair.of(UnitCommandType.Place_COP, "Establishing the command post!"),
            Pair.of(UnitCommandType.Place_COP, "Command post placement confirmed!"),
            Pair.of(UnitCommandType.None, "No command received; standing by!"),
            Pair.of(UnitCommandType.None, "Awaiting orders!"),
            Pair.of(UnitCommandType.Unknown, "Command unclear; say again!"),
            Pair.of(UnitCommandType.Unknown, "Unable to identify that order!"));

}
