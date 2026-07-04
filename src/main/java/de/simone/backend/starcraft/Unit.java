/*
 *   Copyright (c) 2026 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.starcraft;

import de.simone.backend.TAEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Units")
public class Unit extends TAEntity {
    public String name;
    public String race;
    public int maxHitPoints;
    public int maxShields;
    public int maxEnergy;
    public boolean regeneratesHp;
    public String airWeaponName;
    public String groundWeaponName;
    public int armor;
    public String armorUpgrade;
    public String unitSize;
    public String requiredTech;
    public boolean requiresCreep;
    public boolean requiresPsi;
    public String cloakingTech;
    public boolean hasPermanentCloak;
    public int buildScore;
    public int buildTime;
    public int destroyScore;
    public int acceleration;
    public int haltDistance;
    public double topSpeed;
    public int turnRadius;
    public boolean canAttack;
    public boolean canBuildAddon;
    public boolean canMove;
    public boolean canProduce;
    public int width;
    public int height;
    public int dimensionDown;
    public int dimensionLeft;
    public int dimensionRight;
    public int dimensionUp;
    public int tileWidth;
    public int tileHeight;
    public int mineralPrice;
    public int gasPrice;
    public int spaceProvided;
    public int spaceRequired;
    public int supplyProvided;
    public int supplyRequired;
    public boolean isAddon;
    public boolean isBeacon;
    public boolean isBuilding;
    public boolean isBurrowable;
    public boolean isCloakable;
    public boolean isCritter;
    public boolean isDetector;
    public boolean isFlagBeacon;
    public boolean isFlyer;
    public boolean isFlyingBuilding;
    public boolean isHero;
    public boolean isInvincible;
    public boolean isMechanical;
    public boolean isMineralField;
    public boolean isNeutral;
    public boolean isOrganic;
    public boolean isPowerup;
    public boolean isRefinery;
    public boolean isResourceContainer;
    public boolean isResourceDepot;
    public boolean isRobotic;
    public boolean isSpecialBuilding;
    public boolean isSpell;
    public boolean isSpellcaster;
    public boolean isTwoUnitsInOneEgg;
    public boolean isWorker;
    public boolean producesCreep;
    public boolean producesLarva;
    public int seekRange;
    public int sightRange;
    public int maxGroundHits;
    public int maxAirHits;
    public String abilities;
    public String upgrades;
    @Column(columnDefinition = "TEXT")
    public String buildsWhat;
    public String researchesWhat;
    public String upgradesWhat;
    public String requiredUnits;
}
