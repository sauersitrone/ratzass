package de.simone;

import bwapi.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnitTypeBuildOrder {

    public static void main(String[] args) throws IOException {
        String outputPath = "UnitType-terran.csv";

        for (UnitType type : UnitType.values()) {
            List<String> fields = new ArrayList<>();

            // only terrain
            if (type.getRace() != Race.Terran) {
                continue;
            }

            fields.add(type.name());

            Pair<UnitType, Integer> wb = type.whatBuilds();
            fields.add(wb.getKey().name());
            fields.add(String.valueOf(wb.getValue()));

            Map<UnitType, Integer> ru = type.requiredUnits();
            fields.add(ru.entrySet().stream()
                    .map(e -> e.getKey().name() + "=" + e.getValue())
                    .collect(Collectors.joining("|")));

            fields.add(type.requiredTech().name());
            fields.add(type.cloakingTech().name());

            fields.add(type.abilities().stream().map(TechType::name).collect(Collectors.joining("|")));
            fields.add(type.upgrades().stream().map(UpgradeType::name).collect(Collectors.joining("|")));
            fields.add(type.armorUpgrade().name());

            fields.add(String.valueOf(type.maxHitPoints()));
            fields.add(String.valueOf(type.maxShields()));
            fields.add(String.valueOf(type.maxEnergy()));
            fields.add(String.valueOf(type.armor()));
            fields.add(String.valueOf(type.mineralPrice()));
            fields.add(String.valueOf(type.gasPrice()));
            fields.add(String.valueOf(type.buildTime()));
            fields.add(String.valueOf(type.supplyRequired()));
            fields.add(String.valueOf(type.supplyProvided()));
            fields.add(String.valueOf(type.spaceRequired()));
            fields.add(String.valueOf(type.spaceProvided()));
            fields.add(String.valueOf(type.buildScore()));
            fields.add(String.valueOf(type.destroyScore()));

            fields.add(type.size().name());
            fields.add(String.valueOf(type.tileWidth()));
            fields.add(String.valueOf(type.tileHeight()));
            fields.add(String.valueOf(type.dimensionLeft()));
            fields.add(String.valueOf(type.dimensionUp()));
            fields.add(String.valueOf(type.dimensionRight()));
            fields.add(String.valueOf(type.dimensionDown()));
            fields.add(String.valueOf(type.width()));
            fields.add(String.valueOf(type.height()));
            fields.add(String.valueOf(type.seekRange()));
            fields.add(String.valueOf(type.sightRange()));

            fields.add(type.groundWeapon().name());
            fields.add(String.valueOf(type.maxGroundHits()));
            fields.add(type.airWeapon().name());
            fields.add(String.valueOf(type.maxAirHits()));

            fields.add(String.valueOf(type.topSpeed()));
            fields.add(String.valueOf(type.acceleration()));
            fields.add(String.valueOf(type.haltDistance()));
            fields.add(String.valueOf(type.turnRadius()));

            fields.add(String.valueOf(type.canProduce()));
            fields.add(String.valueOf(type.canAttack()));
            fields.add(String.valueOf(type.canMove()));
            fields.add(String.valueOf(type.isFlyer()));
            fields.add(String.valueOf(type.regeneratesHP()));
            fields.add(String.valueOf(type.isSpellcaster()));
            fields.add(String.valueOf(type.hasPermanentCloak()));
            fields.add(String.valueOf(type.isInvincible()));
            fields.add(String.valueOf(type.isOrganic()));
            fields.add(String.valueOf(type.isMechanical()));
            fields.add(String.valueOf(type.isRobotic()));
            fields.add(String.valueOf(type.isDetector()));
            fields.add(String.valueOf(type.isResourceContainer()));
            fields.add(String.valueOf(type.isResourceDepot()));
            fields.add(String.valueOf(type.isRefinery()));
            fields.add(String.valueOf(type.isWorker()));
            fields.add(String.valueOf(type.requiresPsi()));
            fields.add(String.valueOf(type.requiresCreep()));
            fields.add(String.valueOf(type.isTwoUnitsInOneEgg()));
            fields.add(String.valueOf(type.isBurrowable()));
            fields.add(String.valueOf(type.isCloakable()));
            fields.add(String.valueOf(type.isBuilding()));
            fields.add(String.valueOf(type.isAddon()));
            fields.add(String.valueOf(type.isFlyingBuilding()));
            fields.add(String.valueOf(type.isNeutral()));
            fields.add(String.valueOf(type.isHero()));
            fields.add(String.valueOf(type.isPowerup()));
            fields.add(String.valueOf(type.isBeacon()));
            fields.add(String.valueOf(type.isFlagBeacon()));
            fields.add(String.valueOf(type.isSpecialBuilding()));
            fields.add(String.valueOf(type.isSpell()));
            fields.add(String.valueOf(type.producesCreep()));
            fields.add(String.valueOf(type.producesLarva()));
            fields.add(String.valueOf(type.isMineralField()));
            fields.add(String.valueOf(type.isCritter()));
            fields.add(String.valueOf(type.canBuildAddon()));

            fields.add(type.buildsWhat().stream().map(UnitType::name).collect(Collectors.joining("|")));
            fields.add(type.researchesWhat().stream().map(TechType::name).collect(Collectors.joining("|")));
            fields.add(type.upgradesWhat().stream().map(UpgradeType::name).collect(Collectors.joining("|")));

        }
    }

}
