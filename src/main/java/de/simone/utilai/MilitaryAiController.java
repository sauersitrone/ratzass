package de.simone.utilai;

import java.io.FileReader;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import bwapi.Position;
import bwapi.TilePosition;
import bwem.ChokePoint;
import de.simone.RUtils;
import de.simone.command.CombatCenter;
import de.simone.command.RBWListener;

public class MilitaryAiController {

    private Rules rules;
    private final RulesEngine rulesEngine;

    public MilitaryAiController() {
        this.rulesEngine = new DefaultRulesEngine();
        loadRules();
    }

    // Kann auch zur Laufzeit aufgerufen werden, um Regeln live neu zu laden!
    public void loadRules() {
        this.rules = new Rules();

        // TODO: to make the exmaple easy, asume my base is close to a corner

        Rule rule = new RuleBuilder()
                .name("chokePointProximity")
                .priority(1)
                .description("Utility of building defenses based on the base/checkpoint proximity")
                .when(facts -> facts.get("chokePointCenter") != null)
                .then(facts -> {
                    Position startLocation = facts.get("startLocation");
                    Position mapCenter = facts.get("mapCenter");
                    Position chokePointCenter = facts.get("chokePointCenter");
                    double d1 = startLocation.getDistance(chokePointCenter);
                    double d2 = mapCenter.getDistance(chokePointCenter);
                    double risk = d1 / d2;
                    ActionScores actionScores = facts.get("defenceScores");
                    actionScores.addScore(risk);
                })
                .build();
        rules.register(rule);

        rule = new RuleBuilder()
                .name("ChokePointRadius")
                .priority(1)
                .description("utility to build bunkers near choke points based on the radious to the choke point")
                .when(facts -> facts.get("chokePointCenter") != null)
                .then(facts -> {
                    int radius = facts.get("chokePointRadious");
                    ActionScores actionScores = facts.get("defenceScores");
                    double risk = TilePosition.SIZE_IN_PIXELS * 10 / radius;
                    actionScores.addScore(Math.clamp(risk, 0, 1));
                })
                .build();
        rules.register(rule);
    }

    public void loadRulesYml() {
        try {
            MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
            String file = RUtils.getResourceFile("doctrines.yml");
            this.rules = ruleFactory.createRules(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void evaluateSituation(String currentTerrain, double currentDanger) {
        Facts facts = new Facts();
        ActionScores scores = new ActionScores();
        facts.put("defenceScores", scores);

        TilePosition startLoc = RBWListener.game.self().getStartLocation();
        facts.put("startLocation", startLoc.toPosition());

        Position mCenter = RBWListener.bwMap.getCenter();
        facts.put("mapCenter", mCenter);
        facts.put("baseToMapCenterDistance", startLoc.toPosition().getDistance(mCenter));

        ChokePoint chokePoint = CombatCenter.getChokePoint(startLoc);
        if (chokePoint != null) {
            facts.put("chokePointCenter", chokePoint.getCenter().toPosition());
            double dis = chokePoint.getCenter().getDistance(startLoc.toWalkPosition());
            facts.put("distanceToChokePoint", dis);
            int cpRadious = CombatCenter.getChokePointRadious(chokePoint);
            facts.put("chokePointRadious", cpRadious);
        }

        // 3. Engine feuern
        rulesEngine.fire(this.rules, facts);

        // 4. Auswertung des Utility-Hybriden
        System.out.println("--- KI Entscheidung ---");
        System.out.println("Verteidigungs-Score: " + scores.getScore());

        if (scores.getScore() < 0.5) {
            System.out.println("AKTION: Baue Verteidigungsanlagen!");
        } else {
            System.out.println("AKTION: Keine Verteidigung nötig/sinnvoll.");
        }
    }
}
