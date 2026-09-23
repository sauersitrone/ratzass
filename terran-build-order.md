# Terran Build Order Diagram

This diagram is derived from the `UnitType-terran.csv` data using each unit's `whatBuildsUnit` and `requiredUnits` fields. Arrows show a dependency or prerequisite relationship.

## Text tree view

```text
Terran Command Center
├── Terran SCV
├── Terran Supply Depot
├── Terran Engineering Bay
│   ├── Terran Infantry Armor (update)
│   ├── Terran Infantry Weapons (update)
│   └── Terran Missile Turret
├── Terran Refinery
├── Terran Barracks
│   ├── Terran Marine
│   ├── Terran Medic
│   ├── Terran Firebat
│   ├── Terran Academy
│   │   ├── U_238_Shells (update)
│   │   ├── Caduceus_Reactor (update)
│   │   └── Terran Ghost
│   ├── Terran Bunker
│   └── Terran Factory
│       ├── Terran Machine Shop
│       │   ├── Ion_Thrusters (update)
│       │   ├── Charon_Boosters (update)
│       │   └── Terran Siege Tank
│       ├── Terran Vulture
│       ├── Terran Goliath
│       ├── Terran Armory
│       │   ├── Terran Vehicle Plating (update)
│       │   ├── Terran Vehicle Weapons (update)
│       │   ├── Terran Ship Plating (update)
│       │   ├── Terran Ship Weapons (update)
│       │   └── Terran Valkyrie
│       └── Terran Starport
│           ├── Terran Control Tower
│           │   ├── Apollo_Reactor (update)
│           │   ├── Terran Wraith
│           │   ├── Terran Dropship
│           │   ├── Terran Science Vessel
│           │   └── Terran Battlecruiser
│           ├── Terran Science Facility
│           │   ├── Titan_Reactor (update)
│           │   ├── Terran Covert Ops
│           │   │   ├── Ocular_Implants (update)
│           │   │   ├── Moebius_Reactor (update)
│           │   │   └── Terran Ghost
│           │   └── Terran Physics Lab
│           │       ├── Colossus_Reactor (update)
│           │       └── Terran Battlecruiser
│           ├── Terran Wraith
│           ├── Terran Dropship
│           └── Terran Valkyrie
└── Terran Missile Turret (via Engineering Bay)
```

## Build-order interpretation

The CSV shows a clear Terran tech progression:

1. Command Center is the root production structure.
2. SCV and Supply Depot are the earliest essential pieces.
3. Barracks unlocks infantry production and can lead into Academy.
4. Factory unlocks vehicle production and the Machine Shop / Armory path.
5. Starport unlocks air units and adds Control Tower, Science Facility, and Physics Lab dependencies.
6. Science Facility unlocks Ghost and more advanced support tech.

## Recommended dependency chain

- Command Center -> SCV
- Command Center -> Barracks
- Barracks -> Academy
- Barracks -> Factory
- Factory -> Starport
- Starport -> Control Tower
- Science Facility -> Covert Ops / Physics Lab
- Factory -> Machine Shop -> Siege Tank
- Factory + Armory -> Goliath / Valkyrie

## Notes

- `whatBuildsUnit` indicates who constructs the unit or building.
- `requiredUnits` indicates prerequisite buildings (for example, `Terran_Starport=1|Terran_Control_Tower=1` for Dropship).
- This is a dependency diagram, not a strict chronological timeline for every possible build plan.
