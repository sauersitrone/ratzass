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
@Table(name = "Techs")
public class Tech extends TAEntity {
    public String name;
    public String race;
    public int mineralPrice;
    public int gasPrice;
    public String orderName;
    public int abilityEnergyCost;
    public String weaponName;
    public String requiredBuildingName;
    public int researchTime;
    public String researchBuildingName;
    public boolean canTargetTerrain;
    public boolean canTargetUnit;
    @Column(columnDefinition = "TEXT")
    public String techUser;
}
