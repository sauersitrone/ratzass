package de.simone;

import bwapi.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponTypeExporter {

    public static void main(String[] args) throws IOException {
        String outputPath = "WeaponType.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println(
                    "name,id,tech,whatUses,damageAmount,damageBonus,damageCooldown,damageFactor," +
                            "upgradeType,damageType,explosionType," +
                            "minRange,maxRange,innerSplashRadius,medianSplashRadius,outerSplashRadius," +
                            "targetsAir,targetsGround,targetsMechanical,targetsOrganic," +
                            "targetsNonBuilding,targetsNonRobotic,targetsTerrain,targetsOrgOrMech,targetsOwn");

            for (WeaponType type : WeaponType.values()) {
                List<String> fields = new ArrayList<>();

                fields.add(type.name());
                fields.add(String.valueOf(-1)); // fields.add(String.valueOf(type.id));
                fields.add(type.getTech().name());
                fields.add(type.whatUses().name());
                fields.add(String.valueOf(type.damageAmount()));
                fields.add(String.valueOf(type.damageBonus()));
                fields.add(String.valueOf(type.damageCooldown()));
                fields.add(String.valueOf(type.damageFactor()));
                fields.add(type.upgradeType().name());
                fields.add(type.damageType().name());
                fields.add(type.explosionType().name());
                fields.add(String.valueOf(type.minRange()));
                fields.add(String.valueOf(type.maxRange()));
                fields.add(String.valueOf(type.innerSplashRadius()));
                fields.add(String.valueOf(type.medianSplashRadius()));
                fields.add(String.valueOf(type.outerSplashRadius()));
                fields.add(String.valueOf(type.targetsAir()));
                fields.add(String.valueOf(type.targetsGround()));
                fields.add(String.valueOf(type.targetsMechanical()));
                fields.add(String.valueOf(type.targetsOrganic()));
                fields.add(String.valueOf(type.targetsNonBuilding()));
                fields.add(String.valueOf(type.targetsNonRobotic()));
                fields.add(String.valueOf(type.targetsTerrain()));
                fields.add(String.valueOf(type.targetsOrgOrMech()));
                fields.add(String.valueOf(type.targetsOwn()));

                writer.println(fields.stream().map(WeaponTypeExporter::csvEscape).collect(Collectors.joining(",")));
            }
        }

        System.out.println("Exported " + WeaponType.values().length + " weapon types to " + outputPath);
    }

    private static String csvEscape(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
