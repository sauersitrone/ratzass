package de.simone.command;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import bwapi.UnitType;
import de.simone.StarCraftException;

/**
 * Define the pddl problem to solve by a buildOrder.
 * 
 */
public class RPDDLProblem {
    private static final String PROBLEM_TEMPLATE = """
            (define (problem build-unit)
                (:domain starcraftx)

                (:objects
                    <objects>
                )

                (:init
                    ; Mineral & Gas allways are considered constant for the initial state. 
                    ; the solver must compute the solution  with no resources. if e.g a merine is needed, and we 
                    ; habe 10 mineral units, the solver will compute that to create the marine, is needed only 40 units, 
                    ; which is incorrect (in reality)
                    (= (Gas_quantity) 0)
                    (= (Mineral_quantity) 0)
                    (= (Supply_quantity) 0)
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
    public boolean printProblem = false;
    public List<Pair<UnitType, Integer>> unitsTest = new ArrayList<>();

    @SafeVarargs
    public RPDDLProblem(Pair<UnitType, Integer>... goals) {
        for (Pair<UnitType, Integer> goalStatement : goals) {
            goal.add(Pair.of(goalStatement.getKey().toString(), goalStatement.getValue()));
        }
    }

    private List<Pair<String, Integer>> removePrefixes(List<Pair<String, Integer>> list) {
        List<Pair<String, Integer>> updatedList = new ArrayList<>();
        for (Pair<String, Integer> pair : list) {
            String key = pair.getKey();
            updatedList.add(Pair.of(key, pair.getValue()));
        }
        return updatedList;
    }

    private void removeObjectPrefixes() {
        List<String> updatedObjects = new ArrayList<>();
        for (String object : objects) {
            String objName = "" + object;
            objName = objName.endsWith("_Field") ? objName.substring(0, objName.length() - 6) : objName;
            updatedObjects.add(objName);
        }
        objects = updatedObjects;
    }

    public String getPDDLProblem() {
        resolve();
        if (objects.isEmpty() || init.isEmpty() || goal.isEmpty()) {
            throw new StarCraftException("PDDL problem is empty. Please add objects, init, and goal statements.");
        }

        String template = "" + PROBLEM_TEMPLATE;

        // Add objects. e.g: marine_0 marine_1 - terran_marine
        removeObjectPrefixes();
        StringBuilder objectsBuilder = new StringBuilder();
        for (String object : objects) {
            objectsBuilder.append(object).append(" ");
        }
        template = template.replace("<objects>", objectsBuilder.toString().trim());

        // Add init. e.g: (= (unit_quantity) 0)
        // init = removePrefixes(init);
        StringBuilder initBuilder = new StringBuilder();
        for (Pair<String, Integer> pair : init) {
            String varName = pair.getKey() + "_quantity";
            initBuilder.append("(= (").append(varName).append(") ").append(pair.getValue())
                    .append(")\n\t");
        }
        template = template.replace("<init>", initBuilder.toString().trim());

        // Add goal. e.g: (>= (unit_quantity) 1)
        // goal = removePrefixes(goal);
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

        if (printProblem) {
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
        printProblem = true;
        for (UnitType unitType : UnitType.values()) {
            if (unitType.toString().startsWith("Terran_")) {
                Pair<UnitType, Integer> unitTest = unitsTest.stream()
                        .filter(pair -> pair.getKey() == unitType)
                        .findFirst()
                        .orElse(Pair.of(unitType, 0));
                int count = unitTest.getValue();
                updateObjectList(unitType, count);
                init.add(Pair.of(unitType.toString(), count));
            }
        }

    }

    private void resolveLive() {
        for (UnitType unitType : UnitType.values()) {
            if (unitType.toString().startsWith("Terran_")) {
                int count = UnitsCenter.getUnitCount(unitType);
                updateObjectList(unitType, count);
                init.add(Pair.of(unitType.toString(), count));
            }
        }
    }
}
