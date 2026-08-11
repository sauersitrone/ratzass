(define (domain starcraftx)

    (:requirements :strips :typing :numeric-fluents)

    (:types
        unit_type quantity resources - object
        gas mineral - resources
        command_center marine scv ghost vulture goliath goliath_turret siege_tank_tank_mode siege_tank_tank_mode_turret scv wraith science_vessel hero_gui_montag dropship battlecruiser vulture_spider_mine nuclear_missile civilian hero_sarah_kerrigan hero_alan_schezar hero_alan_schezar_turret hero_jim_raynor_vulture hero_jim_raynor_marine hero_tom_kazansky hero_magellan hero_edmund_duke_tank_mode hero_edmund_duke_tank_mode_turret hero_edmund_duke_siege_mode hero_edmund_duke_siege_mode_turret hero_arcturus_mengsk hero_hyperion hero_norad_ii siege_tank_siege_mode siege_tank_siege_mode_turret firebat spell_scanner_sweep medic valkyrie hero_samir_duran hero_alexei_stukov hero_gerard_dugalle command_center comsat_station nuclear_silo supply_depot refinery barracks academy factory starport control_tower science_facility covert_ops physics_lab unused_terran1 machine_shop unused_terran2 engineering_bay armory missile_turret bunker special_crashed_norad_ii special_ion_cannon special_psi_disrupter unused_marker special_beacon special_flag_beacon special_power_generator - unit_type
    )

    (:predicates
        (has_unit ?unit - unit_type)
    )

    (:functions
        (mineral_quantity)
        (gas_quantity)
        (command_center_quantity)
        (scv_quantity)
        (marine_quantity)
        (barracks_quantity)
        (refinery_quantity)
        (ghost_quantity)
        (vulture_quantity)
        (goliath_quantity)
        (goliath_turret_quantity)
        (siege_tank_tank_mode_quantity)
        (siege_tank_tank_mode_turret_quantity)
        (wraith_quantity)
        (science_vessel_quantity)
        (hero_gui_montag_quantity)
        (dropship_quantity)
        (battlecruiser_quantity)
        (vulture_spider_mine_quantity)
        (nuclear_missile_quantity)
        (civilian_quantity)
        (hero_sarah_kerrigan_quantity)
        (hero_alan_schezar_quantity)
        (hero_alan_schezar_turret_quantity)
        (hero_jim_raynor_vulture_quantity)
        (hero_jim_raynor_marine_quantity)
        (hero_tom_kazansky_quantity)
        (hero_magellan_quantity)
        (hero_edmund_duke_tank_mode_quantity)
        (hero_edmund_duke_tank_mode_turret_quantity)
        (hero_edmund_duke_siege_mode_quantity)
        (hero_edmund_duke_siege_mode_turret_quantity)
        (hero_arcturus_mengsk_quantity)
        (hero_hyperion_quantity)
        (hero_norad_ii_quantity)
        (siege_tank_siege_mode_quantity)
        (siege_tank_siege_mode_turret_quantity)
        (firebat_quantity)
        (spell_scanner_sweep_quantity)
        (medic_quantity)
        (valkyrie_quantity)
        (hero_samir_duran_quantity)
        (hero_alexei_stukov_quantity)
        (hero_gerard_dugalle_quantity)
        (comsat_station_quantity)
        (nuclear_silo_quantity)
        (supply_depot_quantity)
        (academy_quantity)
        (factory_quantity)
        (starport_quantity)
        (control_tower_quantity)
        (science_facility_quantity)
        (covert_ops_quantity)
        (physics_lab_quantity)
        (unused_terran1_quantity)
        (machine_shop_quantity)
        (unused_terran2_quantity)
        (engineering_bay_quantity)
        (armory_quantity)
        (missile_turret_quantity)
        (bunker_quantity)
        (special_crashed_norad_ii_quantity)
        (special_ion_cannon_quantity)
        (special_psi_disrupter_quantity)
        (unused_marker_quantity)
        (special_beacon_quantity)
        (special_flag_beacon_quantity)
        (special_power_generator_quantity)
    )

    (:action gather_mineral
        :parameters ()
        :precondition (and (>= (scv_quantity) 1) (>= (command_center_quantity) 1))
        :effect (and (increase (mineral_quantity) 5)
        )
    )

    (:action gather_gas
        :parameters ()
        :precondition (and (>= (scv_quantity) 1) (>= (refinery_quantity) 1))
        :effect (and (increase (gas_quantity) 4)
        )
    )

    (:action build_scv
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (command_center_quantity) 1))
        :effect (and (increase (scv_quantity) 1))
    )

    (:action build_marine
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (barracks_quantity) 1))
        :effect (and (increase (marine_quantity) 1))
    )

    (:action build_barracks
        :parameters ()
        :precondition (and
            (>= (mineral_quantity) 150)
            (>= (scv_quantity) 1)
            (>= (command_center_quantity) 1)
        )
        :effect (and (decrease (mineral_quantity) 150) (increase (barracks_quantity) 1))
    )

    (:action build_refinery
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (increase (refinery_quantity) 1))
    )

    (:action build_ghost
        :parameters ()
        :precondition (and
            (>= (mineral_quantity) 25)
            (>= (gas_quantity) 75)
            (>= (academy_quantity) 1)
            (>= (covert_ops_quantity) 1)
            (>= (barracks_quantity) 1)
        )
        :effect (and (decrease (mineral_quantity) 25) (decrease (gas_quantity) 75) (increase (ghost_quantity) 1))
    )

    (:action build_vulture
        :parameters ()
        :precondition (and (>= (mineral_quantity) 75) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 75) (increase (vulture_quantity) 1))
    )

    (:action build_goliath
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 50) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 50) (increase (goliath_quantity) 1))
    )

    (:action build_goliath_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase (goliath_turret_quantity) 1))
    )

    (:action build_siege_tank_tank_mode
        :parameters ()
        :precondition (and
            (>= (mineral_quantity) 150)
            (>= (gas_quantity) 100)
            (>= (factory_quantity) 1)
        )
        :effect (and
            (decrease (mineral_quantity) 150)
            (decrease (gas_quantity) 100)
            (increase
                (siege_tank_tank_mode_quantity)
                1))
    )

    (:action build_siege_tank_tank_mode_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase
                (siege_tank_tank_mode_turret_quantity)
                1))
    )

    (:action build_wraith
        :parameters ()
        :precondition (and (>= (mineral_quantity) 150) (>= (gas_quantity) 100) (>= (starport_quantity) 1))
        :effect (and (decrease (mineral_quantity) 150) (decrease (gas_quantity) 100) (increase (wraith_quantity) 1))
    )

    (:action build_science_vessel
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 225) (>= (starport_quantity) 1) (>= (control_tower_quantity) 1) (>= (science_facility_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 225) (increase (science_vessel_quantity) 1))
    )

    (:action build_hero_gui_montag
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 50))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 50) (increase (hero_gui_montag_quantity) 1))
    )

    (:action build_dropship
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 100) (>= (starport_quantity) 1) (>= (control_tower_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 100) (increase (dropship_quantity) 1))
    )

    (:action build_battlecruiser
        :parameters ()
        :precondition (and (>= (mineral_quantity) 400) (>= (gas_quantity) 300) (>= (starport_quantity) 1) (>= (control_tower_quantity) 1) (>= (physics_lab_quantity) 1))
        :effect (and (decrease (mineral_quantity) 400) (decrease (gas_quantity) 300) (increase (battlecruiser_quantity) 1))
    )

    (:action build_vulture_spider_mine
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (increase (vulture_spider_mine_quantity) 1))
    )

    (:action build_nuclear_missile
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 200) (>= (nuclear_silo_quantity) 1))
        :effect (and (decrease (mineral_quantity) 200) (decrease (gas_quantity) 200) (increase (nuclear_missile_quantity) 1))
    )

    (:action build_civilian
        :parameters ()
        :precondition (and (>= (mineral_quantity) 0))
        :effect (and (decrease (mineral_quantity) 0) (increase (civilian_quantity) 1))
    )

    (:action build_hero_sarah_kerrigan
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 150))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 150) (increase (hero_sarah_kerrigan_quantity) 1))
    )

    (:action build_hero_alan_schezar
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 100))
        :effect (and (decrease (mineral_quantity) 200) (decrease (gas_quantity) 100) (increase (hero_alan_schezar_quantity) 1))
    )

    (:action build_hero_alan_schezar_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase
                (hero_alan_schezar_turret_quantity)
                1))
    )

    (:action build_hero_jim_raynor_vulture
        :parameters ()
        :precondition (and (>= (mineral_quantity) 150))
        :effect (and (decrease (mineral_quantity) 150) (increase (hero_jim_raynor_vulture_quantity) 1))
    )

    (:action build_hero_jim_raynor_marine
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50))
        :effect (and (decrease (mineral_quantity) 50) (increase (hero_jim_raynor_marine_quantity) 1))
    )

    (:action build_hero_tom_kazansky
        :parameters ()
        :precondition (and (>= (mineral_quantity) 400) (>= (gas_quantity) 200))
        :effect (and (decrease (mineral_quantity) 400) (decrease (gas_quantity) 200) (increase (hero_tom_kazansky_quantity) 1))
    )

    (:action build_hero_magellan
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 600))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 600) (increase (hero_magellan_quantity) 1))
    )

    (:action build_hero_edmund_duke_tank_mode
        :parameters ()
        :precondition (and (>= (mineral_quantity) 300) (>= (gas_quantity) 200))
        :effect (and (decrease (mineral_quantity) 300) (decrease (gas_quantity) 200) (increase (hero_edmund_duke_tank_mode_quantity) 1))
    )

    (:action build_hero_edmund_duke_tank_mode_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase
                (hero_edmund_duke_tank_mode_turret_quantity)
                1))
    )

    (:action build_hero_edmund_duke_siege_mode
        :parameters ()
        :precondition (and (>= (mineral_quantity) 300) (>= (gas_quantity) 200))
        :effect (and (decrease (mineral_quantity) 300) (decrease (gas_quantity) 200) (increase
                (hero_edmund_duke_siege_mode_quantity)
                1))
    )

    (:action build_hero_edmund_duke_siege_mode_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase
                (hero_edmund_duke_siege_mode_turret_quantity)
                1))
    )

    (:action build_hero_arcturus_mengsk
        :parameters ()
        :precondition (and (>= (mineral_quantity) 800) (>= (gas_quantity) 600))
        :effect (and (decrease (mineral_quantity) 800) (decrease (gas_quantity) 600) (increase (hero_arcturus_mengsk_quantity) 1))
    )

    (:action build_hero_hyperion
        :parameters ()
        :precondition (and (>= (mineral_quantity) 800) (>= (gas_quantity) 600))
        :effect (and (decrease (mineral_quantity) 800) (decrease (gas_quantity) 600) (increase (hero_hyperion_quantity) 1))
    )

    (:action build_hero_norad_ii
        :parameters ()
        :precondition (and (>= (mineral_quantity) 800) (>= (gas_quantity) 600))
        :effect (and (decrease (mineral_quantity) 800) (decrease (gas_quantity) 600) (increase (hero_norad_ii_quantity) 1))
    )

    (:action build_siege_tank_siege_mode
        :parameters ()
        :precondition (and (>= (mineral_quantity) 150) (>= (gas_quantity) 100) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 150) (decrease (gas_quantity) 100) (increase
                (siege_tank_siege_mode_quantity)
                1))
    )

    (:action build_siege_tank_siege_mode_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase
                (siege_tank_siege_mode_turret_quantity)
                1))
    )

    (:action build_firebat
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 25) (>= (academy_quantity) 1) (>= (barracks_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 25) (increase (firebat_quantity) 1))
    )

    (:action build_spell_scanner_sweep
        :parameters ()
        :precondition (and (>= (mineral_quantity) 0))
        :effect (and (decrease (mineral_quantity) 0) (increase (spell_scanner_sweep_quantity) 1))
    )

    (:action build_medic
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 25) (>= (academy_quantity) 1) (>= (barracks_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 25) (increase (medic_quantity) 1))
    )

    (:action build_valkyrie
        :parameters ()
        :precondition (and (>= (mineral_quantity) 250) (>= (gas_quantity) 125) (>= (starport_quantity) 1) (>= (control_tower_quantity) 1))
        :effect (and (decrease (mineral_quantity) 250) (decrease (gas_quantity) 125) (increase (valkyrie_quantity) 1))
    )

    (:action build_hero_samir_duran
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 75))
        :effect (and (decrease (mineral_quantity) 200) (decrease (gas_quantity) 75) (increase (hero_samir_duran_quantity) 1))
    )

    (:action build_hero_alexei_stukov
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 75))
        :effect (and (decrease (mineral_quantity) 200) (decrease (gas_quantity) 75) (increase (hero_alexei_stukov_quantity) 1))
    )

    (:action build_hero_gerard_dugalle
        :parameters ()
        :precondition (and (>= (mineral_quantity) 800) (>= (gas_quantity) 600))
        :effect (and (decrease (mineral_quantity) 800) (decrease (gas_quantity) 600) (increase (hero_gerard_dugalle_quantity) 1))
    )

    (:action build_command_center
        :parameters ()
        :precondition (and (>= (mineral_quantity) 400) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 400) (increase (command_center_quantity) 1))
    )

    (:action build_comsat_station
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50) (>= (academy_quantity) 1) (>= (command_center_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (comsat_station_quantity) 1))
    )

    (:action build_nuclear_silo
        :parameters ()
        :precondition (and
            (>= (mineral_quantity) 100)
            (>= (gas_quantity) 100)
            (>= (covert_ops_quantity) 1)
            (>= (command_center_quantity) 1)
            (>= (science_facility_quantity) 1)
        )
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 100) (increase (nuclear_silo_quantity) 1))
    )

    (:action build_supply_depot
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (increase (supply_depot_quantity) 1))
    )

    (:action build_academy
        :parameters ()
        :precondition (and (>= (mineral_quantity) 150) (>= (barracks_quantity) 1) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 150) (increase (academy_quantity) 1))
    )

    (:action build_factory
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 100) (>= (barracks_quantity) 1) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 200) (decrease (gas_quantity) 100) (increase (factory_quantity) 1))
    )

    (:action build_starport
        :parameters ()
        :precondition (and (>= (mineral_quantity) 150) (>= (gas_quantity) 100) (>= (scv_quantity) 1) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 150) (decrease (gas_quantity) 100) (increase (starport_quantity) 1))
    )

    (:action build_control_tower
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50) (>= (starport_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (control_tower_quantity) 1))
    )

    (:action build_science_facility
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 150) (>= (starport_quantity) 1) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 150) (increase (science_facility_quantity) 1))
    )

    (:action build_covert_ops
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50) (>= (science_facility_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (covert_ops_quantity) 1))
    )

    (:action build_physics_lab
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50) (>= (science_facility_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (physics_lab_quantity) 1))
    )

    (:action build_unused_terran1
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase (unused_terran1_quantity) 1))
    )

    (:action build_machine_shop
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (machine_shop_quantity) 1))
    )

    (:action build_unused_terran2
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1) (>= (gas_quantity) 1))
        :effect (and (decrease (mineral_quantity) 1) (decrease (gas_quantity) 1) (increase (unused_terran2_quantity) 1))
    )

    (:action build_engineering_bay
        :parameters ()
        :precondition (and (>= (mineral_quantity) 125) (>= (scv_quantity) 1) (>= (command_center_quantity) 1))
        :effect (and (decrease (mineral_quantity) 125) (increase (engineering_bay_quantity) 1))
    )

    (:action build_armory
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (gas_quantity) 50) (>= (scv_quantity) 1) (>= (factory_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (decrease (gas_quantity) 50) (increase (armory_quantity) 1))
    )

    (:action build_missile_turret
        :parameters ()
        :precondition (and (>= (mineral_quantity) 75) (>= (engineering_bay_quantity) 1) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 75) (increase (missile_turret_quantity) 1))
    )

    (:action build_bunker
        :parameters ()
        :precondition (and (>= (mineral_quantity) 100) (>= (barracks_quantity) 1) (>= (scv_quantity) 1))
        :effect (and (decrease (mineral_quantity) 100) (increase (bunker_quantity) 1))
    )

    (:action build_special_crashed_norad_ii
        :parameters ()
        :precondition (and (>= (mineral_quantity) 800) (>= (gas_quantity) 600))
        :effect (and 
            (decrease (mineral_quantity) 800) 
            (decrease (gas_quantity) 600)
            (increase (special_crashed_norad_ii_quantity) 1)
            )
    )

    (:action build_special_ion_cannon
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200))
        :effect (and (decrease (mineral_quantity) 200) (increase (special_ion_cannon_quantity) 1))
    )

    (:action build_special_psi_disrupter
        :parameters ()
        :precondition (and (>= (mineral_quantity) 1000) (>= (gas_quantity) 400))
        :effect (and (decrease (mineral_quantity) 1000) (decrease (gas_quantity) 400) (increase (special_psi_disrupter_quantity) 1))
    )

    (:action build_unused_marker
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (unused_marker_quantity) 1))
    )

    (:action build_special_beacon
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (special_beacon_quantity) 1))
    )

    (:action build_special_flag_beacon
        :parameters ()
        :precondition (and (>= (mineral_quantity) 50) (>= (gas_quantity) 50))
        :effect (and (decrease (mineral_quantity) 50) (decrease (gas_quantity) 50) (increase (special_flag_beacon_quantity) 1))
    )

    (:action build_special_power_generator
        :parameters ()
        :precondition (and (>= (mineral_quantity) 200) (>= (gas_quantity) 50))
        :effect (and
            (decrease (mineral_quantity) 200)
            (decrease (gas_quantity) 50)
            (increase (special_power_generator_quantity) 1)
        )
    )
)