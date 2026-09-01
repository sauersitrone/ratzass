(define (domain starcraftx)

    (:requirements :strips :typing :numeric-fluents)

    (:types
        unit_type quantity resources - object
        Gas Mineral - resources
        Terran_Command_Center Terran_Marine Terran_SCV Terran_Ghost Terran_Vulture Terran_Goliath Terran_Goliath_Turret Terran_Siege_Tank_Tank_Mode Terran_Siege_Tank_Tank_Mode_Turret Terran_Wraith Terran_Science_Vessel Hero_Gui_Montag Terran_Dropship Terran_Battlecruiser Terran_Vulture_Spider_Mine Terran_Nuclear_Missile Terran_Civilian Hero_Sarah_Kerrigan Hero_Alan_Schezar Hero_Alan_Schezar_Turret Hero_Jim_Raynor_Vulture Hero_Jim_Raynor_Marine Hero_Tom_Kazansky Hero_Magellan Hero_Edmund_Duke_Tank_Mode Hero_Edmund_Duke_Tank_Mode_Turret Hero_Edmund_Duke_Siege_Mode Hero_Edmund_Duke_Siege_Mode_Turret Hero_Arcturus_Mengsk Hero_Hyperion Hero_Norad_II Terran_Siege_Tank_Siege_Mode Terran_Siege_Tank_Siege_Mode_Turret Terran_Firebat Spell_Scanner_Sweep Terran_Medic Terran_Valkyrie Hero_Samir_Duran Hero_Alexei_Stukov Hero_Gerard_DuGalle Terran_Comsat_Station Terran_Nuclear_Silo Terran_Supply_Depot Terran_Refinery Terran_Barracks Terran_Academy Terran_Factory Terran_Starport Terran_Control_Tower Terran_Science_Facility Terran_Covert_Ops Terran_Physics_Lab Unused_Terran1 Terran_Machine_Shop Unused_Terran2 Terran_Engineering_Bay Terran_Armory Terran_Missile_Turret Terran_Bunker Special_Crashed_Norad_II Special_Ion_Cannon Special_Psi_Disrupter Unused_Marker Special_Beacon Special_Flag_Beacon Special_Power_Generator - unit_type
    )

    (:predicates
        (has_unit ?unit - unit_type)
    )

    (:functions
        (Mineral_quantity)
        (Gas_quantity)
        (Terran_Command_Center_quantity)
        (Terran_SCV_quantity)
        (Terran_Marine_quantity)
        (Terran_Barracks_quantity)
        (Terran_Refinery_quantity)
        (Terran_Ghost_quantity)
        (Terran_Vulture_quantity)
        (Terran_Goliath_quantity)
        (Terran_Goliath_Turret_quantity)
        (Terran_Siege_Tank_Tank_Mode_quantity)
        (Terran_Siege_Tank_Tank_Mode_Turret_quantity)
        (Terran_Wraith_quantity)
        (Terran_Science_Vessel_quantity)
        (Hero_Gui_Montag_quantity)
        (Terran_Dropship_quantity)
        (Terran_Battlecruiser_quantity)
        (Terran_Vulture_Spider_Mine_quantity)
        (Terran_Nuclear_Missile_quantity)
        (Terran_Civilian_quantity)
        (Hero_Sarah_Kerrigan_quantity)
        (Hero_Alan_Schezar_quantity)
        (Hero_Alan_Schezar_Turret_quantity)
        (Hero_Jim_Raynor_Vulture_quantity)
        (Hero_Jim_Raynor_Marine_quantity)
        (Hero_Tom_Kazansky_quantity)
        (Hero_Magellan_quantity)
        (Hero_Edmund_Duke_Tank_Mode_quantity)
        (Hero_Edmund_Duke_Tank_Mode_Turret_quantity)
        (Hero_Edmund_Duke_Siege_Mode_quantity)
        (Hero_Edmund_Duke_Siege_Mode_Turret_quantity)
        (Hero_Arcturus_Mengsk_quantity)
        (Hero_Hyperion_quantity)
        (Hero_Norad_II_quantity)
        (Terran_Siege_Tank_Siege_Mode_quantity)
        (Terran_Siege_Tank_Siege_Mode_Turret_quantity)
        (Terran_Firebat_quantity)
        (Spell_Scanner_Sweep_quantity)
        (Terran_Medic_quantity)
        (Terran_Valkyrie_quantity)
        (Hero_Samir_Duran_quantity)
        (Hero_Alexei_Stukov_quantity)
        (Hero_Gerard_DuGalle_quantity)
        (Terran_Comsat_Station_quantity)
        (Terran_Nuclear_Silo_quantity)
        (Terran_Supply_Depot_quantity)
        (Terran_Academy_quantity)
        (Terran_Factory_quantity)
        (Terran_Starport_quantity)
        (Terran_Control_Tower_quantity)
        (Terran_Science_Facility_quantity)
        (Terran_Covert_Ops_quantity)
        (Terran_Physics_Lab_quantity)
        (Unused_Terran1_quantity)
        (Terran_Machine_Shop_quantity)
        (Unused_Terran2_quantity)
        (Terran_Engineering_Bay_quantity)
        (Terran_Armory_quantity)
        (Terran_Missile_Turret_quantity)
        (Terran_Bunker_quantity)
        (Special_Crashed_Norad_II_quantity)
        (Special_Ion_Cannon_quantity)
        (Special_Psi_Disrupter_quantity)
        (Unused_Marker_quantity)
        (Special_Beacon_quantity)
        (Special_Flag_Beacon_quantity)
        (Special_Power_Generator_quantity)
        (Terran_Infantry_Armor_quantity)
    )

    ; NOTE:
    ; the actions name are separated by dashes to use as char separator. see BuildOrder.setPlan() for more details.
    (:action gather-Mineral
        :parameters ()
        :precondition (and (>= (Terran_SCV_quantity) 1) (>= (Terran_Command_Center_quantity) 1))
        :effect (and (increase (Mineral_quantity) 5)
        )
    )

    (:action gather-Gas
        :parameters ()
        :precondition (and (>= (Terran_SCV_quantity) 1) (>= (Terran_Refinery_quantity) 1))
        :effect (and (increase (Gas_quantity) 4)
        )
    )

    (:action build-Terran_SCV
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Terran_Command_Center_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (increase (Terran_SCV_quantity) 1))
    )

    (:action build-Terran_Marine
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Terran_Barracks_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (increase (Terran_Marine_quantity) 1))
    )

    (:action build-Terran_Barracks
        :parameters ()
        :precondition (and
            (>= (Mineral_quantity) 150)
            (>= (Terran_SCV_quantity) 1)
            (>= (Terran_Command_Center_quantity) 1)
        )
        :effect (and (decrease (Mineral_quantity) 150) (increase (Terran_Barracks_quantity) 1))
    )

    (:action build-Terran_Refinery
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (increase (Terran_Refinery_quantity) 1))
    )

    (:action build-Terran_Ghost
        :parameters ()
        :precondition (and
            (>= (Mineral_quantity) 25)
            (>= (Gas_quantity) 75)
            (>= (Terran_Academy_quantity) 1)
            (>= (Terran_Covert_Ops_quantity) 1)
            (>= (Terran_Barracks_quantity) 1)
        )
        :effect (and (decrease (Mineral_quantity) 25) (decrease (Gas_quantity) 75) (increase (Terran_Ghost_quantity) 1))
    )

    (:action build-Terran_Vulture
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 75) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 75) (increase (Terran_Vulture_quantity) 1))
    )

    (:action build-Terran_Goliath
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 50) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 50) (increase (Terran_Goliath_quantity) 1))
    )

    (:action build-Terran_Goliath_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase (Terran_Goliath_Turret_quantity) 1))
    )

    (:action build-Terran_Siege_Tank_Tank_Mode
        :parameters ()
        :precondition (and
            (>= (Mineral_quantity) 150)
            (>= (Gas_quantity) 100)
            (>= (Terran_Factory_quantity) 1)
        )
        :effect (and
            (decrease (Mineral_quantity) 150)
            (decrease (Gas_quantity) 100)
            (increase
                (Terran_Siege_Tank_Tank_Mode_quantity)
                1))
    )

    (:action build-Terran_Siege_Tank_Tank_Mode_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase
                (Terran_Siege_Tank_Tank_Mode_Turret_quantity)
                1))
    )

    (:action build-Terran_Wraith
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 150) (>= (Gas_quantity) 100) (>= (Terran_Starport_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 150) (decrease (Gas_quantity) 100) (increase (Terran_Wraith_quantity) 1))
    )

    (:action build-Terran_Science_Vessel
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 225) (>= (Terran_Starport_quantity) 1) (>= (Terran_Control_Tower_quantity) 1) (>= (Terran_Science_Facility_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 225) (increase (Terran_Science_Vessel_quantity) 1))
    )

    (:action build-Hero_Gui_Montag
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 50))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 50) (increase (Hero_Gui_Montag_quantity) 1))
    )

    (:action build-Terran_Dropship
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 100) (>= (Terran_Starport_quantity) 1) (>= (Terran_Control_Tower_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 100) (increase (Terran_Dropship_quantity) 1))
    )

    (:action build-Terran_Battlecruiser
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 400) (>= (Gas_quantity) 300) (>= (Terran_Starport_quantity) 1) (>= (Terran_Control_Tower_quantity) 1) (>= (Terran_Physics_Lab_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 400) (decrease (Gas_quantity) 300) (increase (Terran_Battlecruiser_quantity) 1))
    )

    (:action build-Terran_Vulture_Spider_Mine
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (increase (Terran_Vulture_Spider_Mine_quantity) 1))
    )

    (:action build-Terran_Nuclear_Missile
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 200) (>= (Terran_Nuclear_Silo_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 200) (decrease (Gas_quantity) 200) (increase (Terran_Nuclear_Missile_quantity) 1))
    )

    (:action build-Terran_Civilian
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 0))
        :effect (and (decrease (Mineral_quantity) 0) (increase (Terran_Civilian_quantity) 1))
    )

    (:action build-Hero_Sarah_Kerrigan
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 150))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 150) (increase (Hero_Sarah_Kerrigan_quantity) 1))
    )

    (:action build-Hero_Alan_Schezar
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 100))
        :effect (and (decrease (Mineral_quantity) 200) (decrease (Gas_quantity) 100) (increase (Hero_Alan_Schezar_quantity) 1))
    )

    (:action build-Hero_Alan_Schezar_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase
                (Hero_Alan_Schezar_Turret_quantity)
                1))
    )

    (:action build-Hero_Jim_Raynor_Vulture
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 150))
        :effect (and (decrease (Mineral_quantity) 150) (increase (Hero_Jim_Raynor_Vulture_quantity) 1))
    )

    (:action build-Hero_Jim_Raynor_Marine
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50))
        :effect (and (decrease (Mineral_quantity) 50) (increase (Hero_Jim_Raynor_Marine_quantity) 1))
    )

    (:action build-Hero_Tom_Kazansky
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 400) (>= (Gas_quantity) 200))
        :effect (and (decrease (Mineral_quantity) 400) (decrease (Gas_quantity) 200) (increase (Hero_Tom_Kazansky_quantity) 1))
    )

    (:action build-Hero_Magellan
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 600))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 600) (increase (Hero_Magellan_quantity) 1))
    )

    (:action build-Hero_Edmund_Duke_Tank_Mode
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 300) (>= (Gas_quantity) 200))
        :effect (and (decrease (Mineral_quantity) 300) (decrease (Gas_quantity) 200) (increase (Hero_Edmund_Duke_Tank_Mode_quantity) 1))
    )

    (:action build-Hero_Edmund_Duke_Tank_Mode_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase
                (Hero_Edmund_Duke_Tank_Mode_Turret_quantity)
                1))
    )

    (:action build-Hero_Edmund_Duke_Siege_Mode
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 300) (>= (Gas_quantity) 200))
        :effect (and (decrease (Mineral_quantity) 300) (decrease (Gas_quantity) 200) (increase
                (Hero_Edmund_Duke_Siege_Mode_quantity)
                1))
    )

    (:action build-Hero_Edmund_Duke_Siege_Mode_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase
                (Hero_Edmund_Duke_Siege_Mode_Turret_quantity)
                1))
    )

    (:action build-Hero_Arcturus_Mengsk
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 800) (>= (Gas_quantity) 600))
        :effect (and (decrease (Mineral_quantity) 800) (decrease (Gas_quantity) 600) (increase (Hero_Arcturus_Mengsk_quantity) 1))
    )

    (:action build-Hero_Hyperion
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 800) (>= (Gas_quantity) 600))
        :effect (and (decrease (Mineral_quantity) 800) (decrease (Gas_quantity) 600) (increase (Hero_Hyperion_quantity) 1))
    )

    (:action build-Hero_Norad_II
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 800) (>= (Gas_quantity) 600))
        :effect (and (decrease (Mineral_quantity) 800) (decrease (Gas_quantity) 600) (increase (Hero_Norad_II_quantity) 1))
    )

    (:action build-Terran_Siege_Tank_Siege_Mode
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 150) (>= (Gas_quantity) 100) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 150) (decrease (Gas_quantity) 100) (increase
                (Terran_Siege_Tank_Siege_Mode_quantity)
                1))
    )

    (:action build-Terran_Siege_Tank_Siege_Mode_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase
                (Terran_Siege_Tank_Siege_Mode_Turret_quantity)
                1))
    )

    (:action build-Terran_Firebat
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 25) (>= (Terran_Academy_quantity) 1) (>= (Terran_Barracks_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 25) (increase (Terran_Firebat_quantity) 1))
    )

    (:action build-Spell_Scanner_Sweep
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 0))
        :effect (and (decrease (Mineral_quantity) 0) (increase (Spell_Scanner_Sweep_quantity) 1))
    )

    (:action build-Terran_Medic
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 25) (>= (Terran_Academy_quantity) 1) (>= (Terran_Barracks_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 25) (increase (Terran_Medic_quantity) 1))
    )

    (:action build-Terran_Valkyrie
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 250) (>= (Gas_quantity) 125) (>= (Terran_Starport_quantity) 1) (>= (Terran_Control_Tower_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 250) (decrease (Gas_quantity) 125) (increase (Terran_Valkyrie_quantity) 1))
    )

    (:action build-Hero_Samir_Duran
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 75))
        :effect (and (decrease (Mineral_quantity) 200) (decrease (Gas_quantity) 75) (increase (Hero_Samir_Duran_quantity) 1))
    )

    (:action build-Hero_Alexei_Stukov
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 75))
        :effect (and (decrease (Mineral_quantity) 200) (decrease (Gas_quantity) 75) (increase (Hero_Alexei_Stukov_quantity) 1))
    )

    (:action build-Hero_Gerard_DuGalle
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 800) (>= (Gas_quantity) 600))
        :effect (and (decrease (Mineral_quantity) 800) (decrease (Gas_quantity) 600) (increase (Hero_Gerard_DuGalle_quantity) 1))
    )

    (:action build-Terran_Command_Center
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 400) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 400) (increase (Terran_Command_Center_quantity) 1))
    )

    (:action build-Terran_Comsat_Station
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50) (>= (Terran_Academy_quantity) 1) (>= (Terran_Command_Center_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Terran_Comsat_Station_quantity) 1))
    )

    (:action build-Terran_Nuclear_Silo
        :parameters ()
        :precondition (and
            (>= (Mineral_quantity) 100)
            (>= (Gas_quantity) 100)
            (>= (Terran_Covert_Ops_quantity) 1)
            (>= (Terran_Command_Center_quantity) 1)
            (>= (Terran_Science_Facility_quantity) 1)
        )
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 100) (increase (Terran_Nuclear_Silo_quantity) 1))
    )

    (:action build-Terran_Supply_Depot
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (increase (Terran_Supply_Depot_quantity) 1))
    )

    (:action build-Terran_Academy
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 150) (>= (Terran_Barracks_quantity) 1) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 150) (increase (Terran_Academy_quantity) 1))
    )

    (:action build-Terran_Factory
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 100) (>= (Terran_Barracks_quantity) 1) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 200) (decrease (Gas_quantity) 100) (increase (Terran_Factory_quantity) 1))
    )

    (:action build-Terran_Starport
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 150) (>= (Gas_quantity) 100) (>= (Terran_SCV_quantity) 1) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 150) (decrease (Gas_quantity) 100) (increase (Terran_Starport_quantity) 1))
    )

    (:action build-Terran_Control_Tower
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50) (>= (Terran_Starport_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Terran_Control_Tower_quantity) 1))
    )

    (:action build-Terran_Science_Facility
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 150) (>= (Terran_Starport_quantity) 1) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 150) (increase (Terran_Science_Facility_quantity) 1))
    )

    (:action build-Terran_Covert_Ops
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50) (>= (Terran_Science_Facility_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Terran_Covert_Ops_quantity) 1))
    )

    (:action build-Terran_Physics_Lab
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50) (>= (Terran_Science_Facility_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Terran_Physics_Lab_quantity) 1))
    )

    (:action build-Unused_Terran1
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase (Unused_Terran1_quantity) 1))
    )

    (:action build-Terran_Machine_Shop
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Terran_Machine_Shop_quantity) 1))
    )

    (:action build-Unused_Terran2
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1) (>= (Gas_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 1) (decrease (Gas_quantity) 1) (increase (Unused_Terran2_quantity) 1))
    )

    (:action build-Terran_Engineering_Bay
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 125) (>= (Terran_SCV_quantity) 1) (>= (Terran_Command_Center_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 125) (increase (Terran_Engineering_Bay_quantity) 1))
    )

    (:action build-Terran_Armory
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Gas_quantity) 50) (>= (Terran_SCV_quantity) 1) (>= (Terran_Factory_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (decrease (Gas_quantity) 50) (increase (Terran_Armory_quantity) 1))
    )

    (:action build-Terran_Missile_Turret
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 75) (>= (Terran_Engineering_Bay_quantity) 1) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 75) (increase (Terran_Missile_Turret_quantity) 1))
    )

    (:action build-Terran_Bunker
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Terran_Barracks_quantity) 1) (>= (Terran_SCV_quantity) 1))
        :effect (and (decrease (Mineral_quantity) 100) (increase (Terran_Bunker_quantity) 1))
    )

    (:action build-Special_Crashed_Norad_II
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 800) (>= (Gas_quantity) 600))
        :effect (and 
            (decrease (Mineral_quantity) 800) 
            (decrease (Gas_quantity) 600)
            (increase (Special_Crashed_Norad_II_quantity) 1)
            )
    )

    (:action build-Special_Ion_Cannon
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200))
        :effect (and (decrease (Mineral_quantity) 200) (increase (Special_Ion_Cannon_quantity) 1))
    )

    (:action build-Special_Psi_Disrupter
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 1000) (>= (Gas_quantity) 400))
        :effect (and (decrease (Mineral_quantity) 1000) (decrease (Gas_quantity) 400) (increase (Special_Psi_Disrupter_quantity) 1))
    )

    (:action build-Unused_Marker
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Unused_Marker_quantity) 1))
    )

    (:action build-Special_Beacon
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Special_Beacon_quantity) 1))
    )

    (:action build-Special_Flag_Beacon
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 50) (>= (Gas_quantity) 50))
        :effect (and (decrease (Mineral_quantity) 50) (decrease (Gas_quantity) 50) (increase (Special_Flag_Beacon_quantity) 1))
    )

    (:action build-Special_Power_Generator
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 200) (>= (Gas_quantity) 50))
        :effect (and
            (decrease (Mineral_quantity) 200)
            (decrease (Gas_quantity) 50)
            (increase (Special_Power_Generator_quantity) 1)
        )
    )
    (:action upgrade-Terran_Infantry_Armor
        :parameters ()
        :precondition (and (>= (Mineral_quantity) 100) (>= (Terran_Engineering_Bay_quantity) 1) (<=(Terran_Infantry_Armor_quantity) 3))
        :effect (and (decrease (Mineral_quantity) 100) (increase (Terran_Infantry_Armor_quantity) 1))
    )
    
)