package de.simone;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.InvalidConfigurationException;
import fr.uga.pddl4j.planners.LogLevel;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.PlannerConfiguration;
import fr.uga.pddl4j.planners.SearchStrategy;
import fr.uga.pddl4j.planners.statespace.HSP;

public class Logistics {

    public void solve() {
        ClassLoader classLoader = Logistics.class.getClassLoader();
        String domain = classLoader.getResource("./starcraft-domain.pddl").getFile();
        String problem = classLoader.getResource("./starcraft-p01.pddl").getPath();

        domain = domain.substring(1);
        domain = domain.replace("%20", " ");
        problem = problem.substring(1);
        problem = problem.replace("%20", " ");
        
        PlannerConfiguration config = HSP.getDefaultConfiguration();
        config.setProperty(HSP.DOMAIN_SETTING, domain);
        config.setProperty(HSP.PROBLEM_SETTING, problem);
        config.setProperty(HSP.TIME_OUT_SETTING, 1000);
        config.setProperty(HSP.LOG_LEVEL_SETTING, LogLevel.DEBUG);
        config.setProperty(HSP.SEARCH_STRATEGIES_SETTING, SearchStrategy.Name.ASTAR);
        // config.setProperty(HSP.HEURISTIC_SETTING, StateHeuristic.Name.MAX);
        // config.setProperty(HSP.WEIGHT_HEURISTIC_SETTING, 1.2);

        Planner planner = Planner.getInstance(Planner.Name.HSP, config);
        // Runs the planner and print the solution
        try {
            System.out.println("Solving logistics problem...");
            Plan plan = planner.solve();
            System.out.println("Plan found with " + plan.size() + " actions:");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Logistics logistics = new Logistics();
        logistics.solve();
    }
}
