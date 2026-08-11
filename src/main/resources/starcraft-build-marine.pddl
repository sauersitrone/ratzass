(define (problem build-marine)
    (:domain starcraftx)

    (:objects
        tcm1 - terran_command_center
        tscv1 - terran_scv
    )
    (:init
        (= (terran_marine_quantity) 0)
        (= (terran_scv_quantity) 2)
        (= (terran_command_center_quantity) 1)
        (= (terran_barracks_quantity) 0)
        (= (terran_refinery_quantity) 0)
        (= (gas_quantity) 0)
        (= (mineral_quantity) 0)
    )

    (:goal
        (and
            (>= (terran_marine_quantity) 0)
            (>= (terran_scv_quantity) 3)
            (>= (terran_command_center_quantity) 0)
            (>= (terran_barracks_quantity) 0)
            (>= (terran_refinery_quantity) 0)
        )
    )
)