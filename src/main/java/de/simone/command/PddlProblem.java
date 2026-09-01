package de.simone.command;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import bwapi.Pair;
import bwapi.UnitType;

/**
 * Define the pddl problem to solve by a buildOrder.
 * 
 */
public class PddlProblem {
    private static final String PROBLEM_TEMPLATE = """
            (define (problem build-unit)
                (:domain starcraftx)

                (:objects
                    <objects>
                )

                (:init
                    <init>
                )

                (:goal
                    (and
                        <goal>
                    )
                )
            )
                    """;

    private List<String> objects = new ArrayList<>();
    private List<Pair<String, Integer>> init = new ArrayList<>();
    private List<Pair<String, Integer>> goal = new ArrayList<>();

    public boolean isTest = false;
    public List<Pair<UnitType, Integer>> unitsTest = new ArrayList<>();

    @SafeVarargs
    public PddlProblem(Pair<UnitType, Integer>... goals) {
        for (Pair<UnitType, Integer> goalStatement : goals) {
            goal.add(new Pair<String, Integer>(goalStatement.getKey().toString(), goalStatement.getValue()));
        }
    }

    private List<Pair<String, Integer>> removePrefixes(List<Pair<String, Integer>> list) {
        List<Pair<String, Integer>> updatedList = new ArrayList<>();
        for (Pair<String, Integer> pair : list) {
            String key = pair.getKey();
            key = key.equals(UnitType.Resource_Mineral_Field.toString()) ? "Mineral" : key;
            key = key.equals(UnitType.Resource_Vespene_Geyser.toString()) ? "Gas" : key;
            updatedList.add(new Pair<>(key, pair.getValue()));
        }
        return updatedList;
    }

    private void removeObjectPrefixes() {
        List<String> updatedObjects = new ArrayList<>();
        for (String object : objects) {
            String objName = "" + object;
            objName = objName.startsWith("Resource_") ? objName.substring(9) : objName;
            objName = objName.endsWith("_Field") ? objName.substring(0, objName.length() - 6) : objName;
            updatedObjects.add(objName);
        }
        objects = updatedObjects;
    }

    public String getPDDLProblem() {
        resolve();
        if (objects.isEmpty() || init.isEmpty() || goal.isEmpty()) {
            throw new IllegalStateException("PDDL problem is empty. Please add objects, init, and goal statements.");
        }

        String template = "" + PROBLEM_TEMPLATE;

        // Add objects marine_0 marine_1 - terran_marine
        removeObjectPrefixes();
        StringBuilder objectsBuilder = new StringBuilder();
        for (String object : objects) {
            objectsBuilder.append(object).append(" ");
        }
        template = template.replace("<objects>", objectsBuilder.toString().trim());

        // Add init (= (unit_quantity) 0)
        init = removePrefixes(init);
        StringBuilder initBuilder = new StringBuilder();
        for (Pair<String, Integer> pair : init) {
            String varName = pair.getKey() + "_quantity";
            initBuilder.append("(= (").append(varName).append(") ").append(pair.getValue())
                    .append(")\n\t");
        }
        template = template.replace("<init>", initBuilder.toString().trim());

        // Add goal (>= (unit_quantity) 1)
        goal = removePrefixes(goal);
        StringBuilder goalBuilder = new StringBuilder();
        for (Pair<String, Integer> goalStatement : goal) {
            String varName = goalStatement.getKey() + "_quantity";
            goalBuilder.append("(>= (").append(varName).append(") ").append(goalStatement.getValue())
                    .append(")\n\t\t");
        }
        template = template.replace("<goal>", goalBuilder.toString().trim());

        File problemFile = null;
        try {
            problemFile = File.createTempFile("pddl_problem_" + System.currentTimeMillis(), ".pddl");
            try (FileWriter writer = new FileWriter(problemFile)) {
                writer.write(template);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        if (isTest) {
            System.out.println("PDDL Problem ----------------------\n" + template);
            System.out.println("PDDL Problem path -----------------\n" + problemFile.getAbsolutePath());
        }

        return problemFile.getAbsolutePath();
    }

    /**
     * fill the object list with the unitType and quantity. This is used to generate
     * the PDDL problem file.
     * 
     * @param unitType - the type of the unit
     * @param quantity - the quantity of the unit
     */
    private void updateObjectList(UnitType unitType, int quantity) {
        if (unitType == null || !unitType.toString().startsWith("Terran_") || quantity <= 0) {
            return;
        }
        String objs = "";
        for (int i = 0; i < quantity; i++) {
            String pddlname = unitType.toString() + (i + 1);
            objs += pddlname + " ";
        }
        String unittype = unitType.toString();
        objs += "- " + unittype + "\n\t";
        this.objects.add(objs);
    }

    /**
     * Resolve the problem by filling the object list and init list.
     * - If isTest is true, it will use the unitsTest list to fill the object list
     * and init list.
     * - Otherwise, it will use the UnitsCenter to get the current units and their
     * quantities.
     */
    private void resolve() {
        if (isTest) {
            resolveTest();
        } else {
            resolveLive();
        }
    }

    private void resolveTest() {
        for (UnitType unitType : UnitType.values()) {
            if (unitType.toString().startsWith("Terran_")
                    || unitType == UnitType.Resource_Mineral_Field
                    || unitType == UnitType.Resource_Vespene_Geyser) {

                Pair<UnitType, Integer> unitTest = unitsTest.stream()
                        .filter(pair -> pair.getKey() == unitType)
                        .findFirst()
                        .orElse(new Pair<>(unitType, 0));
                int count = unitTest.getValue();
                updateObjectList(unitType, count);
                init.add(new Pair<String, Integer>(unitType.toString(), count));
            }
        }

    }

    private void resolveLive() {
        UnitsCenter unitsCenter = UnitsCenter.getInstance();

        for (UnitType unitType : UnitType.values()) {
            if (unitType.toString().startsWith("Terran_")
                    || unitType == UnitType.Resource_Mineral_Field
                    || unitType == UnitType.Resource_Vespene_Geyser) {
                int count = unitsCenter.getUnitCount(unitType);
                updateObjectList(unitType, count);
                init.add(new Pair<String, Integer>(unitType.toString(), count));
            }
        }
    }
}
