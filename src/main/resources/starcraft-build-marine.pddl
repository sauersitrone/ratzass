(define (problem build-marine)
    (:domain starcraftx)

    (:objects
        tcm1 - Terran_Command_Center
        tscv1 - Terran_SCV
    )
    (:init
        (= (Terran_Marine_quantity) 0)
        (= (Terran_SCV_quantity) 2)
        (= (Terran_Command_Center_quantity) 1)
        (= (Terran_Barracks_quantity) 0)
        (= (Terran_Refinery_quantity) 0)
        (= (Gas_quantity) 0)
        (= (Mineral_quantity) 0)
    )

    (:goal
        (and
            (>= (Terran_Marine_quantity) 0)
            (>= (Terran_SCV_quantity) 3)
            (>= (Terran_Command_Center_quantity) 0)
            (>= (Terran_Barracks_quantity) 0)
            (>= (Terran_Refinery_quantity) 0)
        )
    )
)