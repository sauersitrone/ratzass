package de.simone.test.pddl;

import de.simone.RUtils;
import planners.ENHSP;

public class CommandCenter {

    private String domain;
    private String problem;
    private String planner;

    public void solve() {
        ENHSP p = new ENHSP(false);
        String[] args1 = { "-o", domain, "-f", problem, "-planner", planner };
        p.parseInput(args1);
        p.configurePlanner();
        if (p.parsingDomainAndProblem(args1))
            p.planning();
        else {
            System.out.println("Unsolvable Problem");
        }
    }

    private void resolveDomainAndProblem() {
        this.domain = RUtils.getResourceFile("./starcraft-domain.pddl");
        this.problem = RUtils.getResourceFile("./starcraft-build-marine.pddl");
        this.planner = "opt-blind";
    }

    public static void main(String[] args) {
        CommandCenter logistics = new CommandCenter();
        logistics.resolveDomainAndProblem();
        logistics.solve();
    }
}
