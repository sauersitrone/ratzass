(define (domain starcraft)

    (:requirements :strips :typing :fluents)

    (:types
        unit_type building geyser mineral - object
        gas mineral - resources
        Terran_Marine Terran_Ghost Terran_Vulture Terran_Goliath Terran_Goliath_Turret Terran_Siege_Tank_Tank_Mode Terran_Siege_Tank_Tank_Mode_Turret Terran_SCV Terran_Wraith Terran_Science_Vessel Hero_Gui_Montag Terran_Dropship Terran_Battlecruiser Terran_Vulture_Spider_Mine Terran_Nuclear_Missile Terran_Civilian Hero_Sarah_Kerrigan Hero_Alan_Schezar Hero_Alan_Schezar_Turret Hero_Jim_Raynor_Vulture Hero_Jim_Raynor_Marine Hero_Tom_Kazansky Hero_Magellan Hero_Edmund_Duke_Tank_Mode Hero_Edmund_Duke_Tank_Mode_Turret Hero_Edmund_Duke_Siege_Mode Hero_Edmund_Duke_Siege_Mode_Turret Hero_Arcturus_Mengsk Hero_Hyperion Hero_Norad_II Terran_Siege_Tank_Siege_Mode Terran_Siege_Tank_Siege_Mode_Turret Terran_Firebat Spell_Scanner_Sweep Terran_Medic Terran_Valkyrie Hero_Samir_Duran Hero_Alexei_Stukov Hero_Gerard_DuGalle Terran_Command_Center Terran_Comsat_Station Terran_Nuclear_Silo Terran_Supply_Depot Terran_Refinery Terran_Barracks Terran_Academy Terran_Factory Terran_Starport Terran_Control_Tower Terran_Science_Facility Terran_Covert_Ops Terran_Physics_Lab Unused_Terran1 Terran_Machine_Shop Unused_Terran2 Terran_Engineering_Bay Terran_Armory Terran_Missile_Turret Terran_Bunker Special_Crashed_Norad_II Special_Ion_Cannon Special_Psi_Disrupter Unused_Terran_Marker Special_Terran_Beacon Special_Terran_Flag_Beacon Special_Power_Generator - unit_type
    )

    (:predicates
        (has-unit ?unit - unit_type)
        (has-resource ?resource - resources)
        (has-mineral ?mineral - mineral)
        (has-gas ?gas - gas)
        (can-build ?unit - unit_type)
        (can-build-building ?building - building)
    )

    (:constants
        Terran_Academy Terran_Barracks - unit_type
    )

    (:functions
        (unit-cost ?unit - unit_type)
        (building-cost ?building - building)
        (resource-amount ?resource - resources)
        (mineral-amount ?mineral - mineral)
        (gas-amount ?gas - gas)
        (Terran_Marine-amount ?unit - Terran_Marine)
        (Terran_Ghost-amount ?unit - Terran_Ghost)
        (Terran_Vulture-amount ?unit - Terran_Vulture)
        (Terran_Goliath-amount ?unit - Terran_Goliath)
        (Terran_Goliath_Turret-amount ?unit - Terran_Goliath_Turret)
        (Terran_Siege_Tank_Tank_Mode-amount ?unit - Terran_Siege_Tank_Tank_Mode)
        (Terran_Siege_Tank_Tank_Mode_Turret-amount ?unit - Terran_Siege_Tank_Tank_Mode_Turret)
        (Terran_SCV-amount ?unit - Terran_SCV)
        (Terran_Wraith-amount ?unit - Terran_Wraith)
        (Terran_Science_Vessel-amount ?unit - Terran_Science_Vessel)
        (Hero_Gui_Montag-amount ?unit - Hero_Gui_Montag)
        (Terran_Dropship-amount ?unit - Terran_Dropship)
        (Terran_Battlecruiser-amount ?unit - Terran_Battlecruiser)
        (Terran_Vulture_Spider_Mine-amount ?unit - Terran_Vulture_Spider_Mine)
        (Terran_Nuclear_Missile-amount ?unit - Terran_Nuclear_Missile)
        (Terran_Civilian-amount ?unit - Terran_Civilian)
        (Hero_Sarah_Kerrigan-amount ?unit - Hero_Sarah_Kerrigan)
        (Hero_Alan_Schezar-amount ?unit - Hero_Alan_Schezar)
        (Hero_Alan_Schezar_Turret-amount ?unit - Hero_Alan_Schezar_Turret)
        (Hero_Jim_Raynor_Vulture-amount ?unit - Hero_Jim_Raynor_Vulture)
        (Hero_Jim_Raynor_Marine-amount ?unit - Hero_Jim_Raynor_Marine)
        (Hero_Tom_Kazansky-amount ?unit - Hero_Tom_Kazansky)
        (Hero_Magellan-amount ?unit - Hero_Magellan)
        (Hero_Edmund_Duke_Tank_Mode-amount ?unit - Hero_Edmund_Duke_Tank_Mode)
        (Hero_Edmund_Duke_Tank_Mode_Turret-amount ?unit - Hero_Edmund_Duke_Tank_Mode_Turret)
        (Hero_Edmund_Duke_Siege_Mode-amount ?unit - Hero_Edmund_Duke_Siege_Mode)
        (Hero_Edmund_Duke_Siege_Mode_Turret-amount ?unit - Hero_Edmund_Duke_Siege_Mode_Turret)
        (Hero_Arcturus_Mengsk-amount ?unit - Hero_Arcturus_Mengsk)
        (Hero_Hyperion-amount ?unit - Hero_Hyperion)
        (Hero_Norad_II-amount ?unit - Hero_Norad_II)
        (Terran_Siege_Tank_Siege_Mode-amount ?unit - Terran_Siege_Tank_Siege_Mode)
        (Terran_Siege_Tank_Siege_Mode_Turret-amount ?unit - Terran_Siege_Tank_Siege_Mode_Turret)
        (Terran_Firebat-amount ?unit - Terran_Firebat)
        (Spell_Scanner_Sweep-amount ?unit - Spell_Scanner_Sweep)
        (Terran_Medic-amount ?unit - Terran_Medic)
        (Terran_Valkyrie-amount ?unit - Terran_Valkyrie)
        (Hero_Samir_Duran-amount ?unit - Hero_Samir_Duran)
        (Hero_Alexei_Stukov-amount ?unit - Hero_Alexei_Stukov)
        (Hero_Gerard_DuGalle-amount ?unit - Hero_Gerard_DuGalle)
        (Terran_Command_Center-amount ?unit - Terran_Command_Center)
        (Terran_Comsat_Station-amount ?unit - Terran_Comsat_Station)
        (Terran_Nuclear_Silo-amount ?unit - Terran_Nuclear_Silo)
        (Terran_Supply_Depot-amount ?unit - Terran_Supply_Depot)
        (Terran_Refinery-amount ?unit - Terran_Refinery)
        (Terran_Barracks-amount ?unit - Terran_Barracks)
        (Terran_Academy-amount ?unit - Terran_Academy)
        (Terran_Factory-amount ?unit - Terran_Factory)
        (Terran_Starport-amount ?unit - Terran_Starport)
        (Terran_Control_Tower-amount ?unit - Terran_Control_Tower)
        (Terran_Science_Facility-amount ?unit - Terran_Science_Facility)
        (Terran_Covert_Ops-amount ?unit - Terran_Covert_Ops)
        (Terran_Physics_Lab-amount ?unit - Terran_Physics_Lab)
        (Unused_Terran1-amount ?unit - Unused_Terran1)
        (Terran_Machine_Shop-amount ?unit - Terran_Machine_Shop)
        (Unused_Terran2-amount ?unit - Unused_Terran2)
        (Terran_Engineering_Bay-amount ?unit - Terran_Engineering_Bay)
        (Terran_Armory-amount ?unit - Terran_Armory)
        (Terran_Missile_Turret-amount ?unit - Terran_Missile_Turret)
        (Terran_Bunker-amount ?unit - Terran_Bunker)
        (Special_Crashed_Norad_II-amount ?unit - Special_Crashed_Norad_II)
        (Special_Ion_Cannon-amount ?unit - Special_Ion_Cannon)
        (Special_Psi_Disrupter-amount ?unit - Special_Psi_Disrupter)
        (Unused_Terran_Marker-amount ?unit - Unused_Terran_Marker)
        (Special_Terran_Beacon-amount ?unit - Special_Terran_Beacon)
        (Special_Terran_Flag_Beacon-amount ?unit - Special_Terran_Flag_Beacon)
        (Special_Power_Generator-amount ?unit - Special_Power_Generator)
    )

    (:action gather-mineral
        :parameters (?unit - Terran_SCV)
        :precondition (and (has-unit ?unit))
        :effect (and (increase (mineral-amount) 5))
    )

    (:action gather-gas
        :parameters (?unit - Terran_SCV)
        :precondition (and (has-unit ?unit))
        :effect (and (increase (gas-amount) 4))
    )

    (:action build-terran-marine
        :parameters (?unit - Terran_Marine ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (has-unit Terran_Barracks))
        :effect (and (increase (Terran_Marine-amount ?unit) 1))
    )

    (:action build-terran-ghost
        :parameters (?unit - Terran_Ghost ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 25) (>= (gas-amount ?gas) 75) (has-building Terran_Academy) (has-building Terran_Covert_Ops) (has-building Terran_Barracks))
        :effect (and (decrease (mineral-amount) 25) (decrease (gas-amount) 75) (increase (Terran_Ghost-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-vulture
        :parameters (?unit - Terran_Vulture ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 75) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 75) (increase (Terran_Vulture-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-goliath
        :parameters (?unit - Terran_Goliath ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 50) (has-building Terran_Factory) (has-building Terran_Armory))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 50) (increase (Terran_Goliath-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-goliath-turret
        :parameters (?unit - Terran_Goliath_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase (Terran_Goliath_Turret-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-siege-tank-tank-mode
        :parameters (?unit - Terran_Siege_Tank_Tank_Mode ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (>= (gas-amount ?gas) 100) (has-building Terran_Machine_Shop) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 150) (decrease (gas-amount) 100) (increase
                (Terran_Siege_Tank_Tank_Mode-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-siege-tank-tank-mode-turret
        :parameters (?unit - Terran_Siege_Tank_Tank_Mode_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase
                (Terran_Siege_Tank_Tank_Mode_Turret-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-scv
        :parameters (?unit - Terran_SCV ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (has-building Terran_Command_Center))
        :effect (and (decrease (mineral-amount) 50) (increase (Terran_SCV-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-wraith
        :parameters (?unit - Terran_Wraith ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (>= (gas-amount ?gas) 100) (has-building Terran_Starport))
        :effect (and (decrease (mineral-amount) 150) (decrease (gas-amount) 100) (increase (Terran_Wraith-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-science-vessel
        :parameters (?unit - Terran_Science_Vessel ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 225) (has-building Terran_Starport) (has-building Terran_Control_Tower) (has-building Terran_Science_Facility))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 225) (increase (Terran_Science_Vessel-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-gui-montag
        :parameters (?unit - Hero_Gui_Montag ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 50))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 50) (increase (Hero_Gui_Montag-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-dropship
        :parameters (?unit - Terran_Dropship ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 100) (has-building Terran_Starport) (has-building Terran_Control_Tower))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 100) (increase (Terran_Dropship-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-battlecruiser
        :parameters (?unit - Terran_Battlecruiser ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 400) (>= (gas-amount ?gas) 300) (has-building Terran_Starport) (has-building Terran_Control_Tower) (has-building Terran_Physics_Lab))
        :effect (and (decrease (mineral-amount) 400) (decrease (gas-amount) 300) (increase (Terran_Battlecruiser-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-vulture-spider-mine
        :parameters (?unit - Terran_Vulture_Spider_Mine ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1))
        :effect (and (decrease (mineral-amount) 1) (increase
                (Terran_Vulture_Spider_Mine-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-nuclear-missile
        :parameters (?unit - Terran_Nuclear_Missile ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 200) (has-building Terran_Nuclear_Silo))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 200) (increase (Terran_Nuclear_Missile-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-civilian
        :parameters (?unit - Terran_Civilian ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 0))
        :effect (and (decrease (mineral-amount) 0) (increase (Terran_Civilian-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-sarah-kerrigan
        :parameters (?unit - Hero_Sarah_Kerrigan ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 150))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 150) (increase (Hero_Sarah_Kerrigan-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-alan-schezar
        :parameters (?unit - Hero_Alan_Schezar ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 100))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 100) (increase (Hero_Alan_Schezar-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-alan-schezar-turret
        :parameters (?unit - Hero_Alan_Schezar_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase
                (Hero_Alan_Schezar_Turret-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-jim-raynor-vulture
        :parameters (?unit - Hero_Jim_Raynor_Vulture ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150))
        :effect (and (decrease (mineral-amount) 150) (increase
                (Hero_Jim_Raynor_Vulture-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-jim-raynor-marine
        :parameters (?unit - Hero_Jim_Raynor_Marine ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50))
        :effect (and (decrease (mineral-amount) 50) (increase (Hero_Jim_Raynor_Marine-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-tom-kazansky
        :parameters (?unit - Hero_Tom_Kazansky ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 400) (>= (gas-amount ?gas) 200))
        :effect (and (decrease (mineral-amount) 400) (decrease (gas-amount) 200) (increase (Hero_Tom_Kazansky-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-magellan
        :parameters (?unit - Hero_Magellan ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 600) (increase (Hero_Magellan-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-edmund-duke-tank-mode
        :parameters (?unit - Hero_Edmund_Duke_Tank_Mode ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 300) (>= (gas-amount ?gas) 200))
        :effect (and (decrease (mineral-amount) 300) (decrease (gas-amount) 200) (increase
                (Hero_Edmund_Duke_Tank_Mode-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-edmund-duke-tank-mode-turret
        :parameters (?unit - Hero_Edmund_Duke_Tank_Mode_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase
                (Hero_Edmund_Duke_Tank_Mode_Turret-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-edmund-duke-siege-mode
        :parameters (?unit - Hero_Edmund_Duke_Siege_Mode ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 300) (>= (gas-amount ?gas) 200))
        :effect (and (decrease (mineral-amount) 300) (decrease (gas-amount) 200) (increase
                (Hero_Edmund_Duke_Siege_Mode-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-edmund-duke-siege-mode-turret
        :parameters (?unit - Hero_Edmund_Duke_Siege_Mode_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase
                (Hero_Edmund_Duke_Siege_Mode_Turret-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-hero-arcturus-mengsk
        :parameters (?unit - Hero_Arcturus_Mengsk ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 800) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 800) (decrease (gas-amount) 600) (increase (Hero_Arcturus_Mengsk-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-hyperion
        :parameters (?unit - Hero_Hyperion ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 800) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 800) (decrease (gas-amount) 600) (increase (Hero_Hyperion-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-norad-ii
        :parameters (?unit - Hero_Norad_II ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 800) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 800) (decrease (gas-amount) 600) (increase (Hero_Norad_II-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-siege-tank-siege-mode
        :parameters (?unit - Terran_Siege_Tank_Siege_Mode ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (>= (gas-amount ?gas) 100) (has-building Terran_Machine_Shop) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 150) (decrease (gas-amount) 100) (increase
                (Terran_Siege_Tank_Siege_Mode-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-siege-tank-siege-mode-turret
        :parameters (?unit - Terran_Siege_Tank_Siege_Mode_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase
                (Terran_Siege_Tank_Siege_Mode_Turret-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-firebat
        :parameters (?unit - Terran_Firebat ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 25) (has-building Terran_Academy) (has-building Terran_Barracks))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 25) (increase (Terran_Firebat-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-spell-scanner-sweep
        :parameters (?unit - Spell_Scanner_Sweep ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 0))
        :effect (and (decrease (mineral-amount) 0) (increase (Spell_Scanner_Sweep-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-medic
        :parameters (?unit - Terran_Medic ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 25) (has-building Terran_Academy) (has-building Terran_Barracks))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 25) (increase (Terran_Medic-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-valkyrie
        :parameters (?unit - Terran_Valkyrie ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 250) (>= (gas-amount ?gas) 125) (has-building Terran_Starport) (has-building Terran_Control_Tower) (has-building Terran_Armory))
        :effect (and (decrease (mineral-amount) 250) (decrease (gas-amount) 125) (increase (Terran_Valkyrie-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-samir-duran
        :parameters (?unit - Hero_Samir_Duran ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 75))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 75) (increase (Hero_Samir_Duran-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-alexei-stukov
        :parameters (?unit - Hero_Alexei_Stukov ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 75))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 75) (increase (Hero_Alexei_Stukov-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-hero-gerard-dugalle
        :parameters (?unit - Hero_Gerard_DuGalle ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 800) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 800) (decrease (gas-amount) 600) (increase (Hero_Gerard_DuGalle-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-command-center
        :parameters (?unit - Terran_Command_Center ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 400) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 400) (increase (Terran_Command_Center-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-comsat-station
        :parameters (?unit - Terran_Comsat_Station ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50) (has-building Terran_Academy) (has-building Terran_Command_Center))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Terran_Comsat_Station-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-nuclear-silo
        :parameters (?unit - Terran_Nuclear_Silo ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 100) (has-building Terran_Covert_Ops) (has-building Terran_Command_Center) (has-building Terran_Science_Facility))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 100) (increase (Terran_Nuclear_Silo-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-supply-depot
        :parameters (?unit - Terran_Supply_Depot ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 100) (increase (Terran_Supply_Depot-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-refinery
        :parameters (?unit - Terran_Refinery ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 100) (increase (Terran_Refinery-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-barracks
        :parameters (?unit - Terran_Barracks ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (has-unit Terran_SCV) (has-building Terran_Command_Center))
        :effect (and (decrease (mineral-amount) 150) (increase (Terran_Barracks-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-academy
        :parameters (?unit - Terran_Academy ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (has-building Terran_Barracks) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 150) (increase (Terran_Academy-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-factory
        :parameters (?unit - Terran_Factory ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 100) (has-building Terran_Barracks) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 100) (increase (Terran_Factory-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-starport
        :parameters (?unit - Terran_Starport ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 150) (>= (gas-amount ?gas) 100) (has-unit Terran_SCV) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 150) (decrease (gas-amount) 100) (increase (Terran_Starport-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-control-tower
        :parameters (?unit - Terran_Control_Tower ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50) (has-building Terran_Starport))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Terran_Control_Tower-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-science-facility
        :parameters (?unit - Terran_Science_Facility ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 150) (has-building Terran_Starport) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 150) (increase
                (Terran_Science_Facility-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-terran-covert-ops
        :parameters (?unit - Terran_Covert_Ops ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50) (has-building Terran_Science_Facility))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Terran_Covert_Ops-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-physics-lab
        :parameters (?unit - Terran_Physics_Lab ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50) (has-building Terran_Science_Facility))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Terran_Physics_Lab-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-unused-terran1
        :parameters (?unit - Unused_Terran1 ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase (Unused_Terran1-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-machine-shop
        :parameters (?unit - Terran_Machine_Shop ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Terran_Machine_Shop-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-unused-terran2
        :parameters (?unit - Unused_Terran2 ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1) (>= (gas-amount ?gas) 1))
        :effect (and (decrease (mineral-amount) 1) (decrease (gas-amount) 1) (increase (Unused_Terran2-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-engineering-bay
        :parameters (?unit - Terran_Engineering_Bay ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 125) (has-unit Terran_SCV) (has-building Terran_Command_Center))
        :effect (and (decrease (mineral-amount) 125) (increase (Terran_Engineering_Bay-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-armory
        :parameters (?unit - Terran_Armory ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (>= (gas-amount ?gas) 50) (has-unit Terran_SCV) (has-building Terran_Factory))
        :effect (and (decrease (mineral-amount) 100) (decrease (gas-amount) 50) (increase (Terran_Armory-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-missile-turret
        :parameters (?unit - Terran_Missile_Turret ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 75) (has-building Terran_Engineering_Bay) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 75) (increase (Terran_Missile_Turret-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-terran-bunker
        :parameters (?unit - Terran_Bunker ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 100) (has-building Terran_Barracks) (has-unit Terran_SCV))
        :effect (and (decrease (mineral-amount) 100) (increase (Terran_Bunker-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-special-crashed-norad-ii
        :parameters (?unit - Special_Crashed_Norad_II ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 800) (>= (gas-amount ?gas) 600))
        :effect (and (decrease (mineral-amount) 800) (decrease (gas-amount) 600) (increase
                (Special_Crashed_Norad_II-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-special-ion-cannon
        :parameters (?unit - Special_Ion_Cannon ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200))
        :effect (and (decrease (mineral-amount) 200) (increase (Special_Ion_Cannon-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-special-psi-disrupter
        :parameters (?unit - Special_Psi_Disrupter ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 1000) (>= (gas-amount ?gas) 400))
        :effect (and (decrease (mineral-amount) 1000) (decrease (gas-amount) 400) (increase (Special_Psi_Disrupter-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-unused-terran-marker
        :parameters (?unit - Unused_Terran_Marker ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Unused_Terran_Marker-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-special-terran-beacon
        :parameters (?unit - Special_Terran_Beacon ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase (Special_Terran_Beacon-amount ?unit) 1) (has-unit ?unit))
    )

    (:action build-special-terran-flag-beacon
        :parameters (?unit - Special_Terran_Flag_Beacon ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 50) (>= (gas-amount ?gas) 50))
        :effect (and (decrease (mineral-amount) 50) (decrease (gas-amount) 50) (increase
                (Special_Terran_Flag_Beacon-amount ?unit)
                1) (has-unit ?unit))
    )

    (:action build-special-power-generator
        :parameters (?unit - Special_Power_Generator ?mineral - mineral ?gas - gas)
        :precondition (and (>= (mineral-amount ?mineral) 200) (>= (gas-amount ?gas) 50))
        :effect (and (decrease (mineral-amount) 200) (decrease (gas-amount) 50) (increase
                (Special_Power_Generator-amount ?unit)
                1) (has-unit ?unit))
    )
)