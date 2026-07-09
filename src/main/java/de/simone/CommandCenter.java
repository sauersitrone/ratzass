package de.simone;

import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.statespace.HSP;

public class CommandCenter {

    public CommandCenter() {
        ClassLoader classLoader = getClass().getClassLoader();
        HSP planner = new HSP();

        planner.setDomain(classLoader.getResource("terran-vs-random-domain.hddl").getPath());
        planner.setProblem(classLoader.getResource("terran-vs-random-problem.hddl").getPath());
    }

}
