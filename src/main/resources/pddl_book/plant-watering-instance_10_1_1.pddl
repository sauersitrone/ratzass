(define (problem instance_10_1_1)
    (:domain plant-watering)

    (:objects
        agent1 - agent
        tap1 - tap
        plant1 - plant
    )

    (:init
        (= (maxx) 10)
        (= (maxy) 10)
        (= (minx) 1)
        (= (miny) 1)
        (= (x agent1) 3)
        (= (y agent1) 1)
        (= (x plant1) 3)
        (= (y plant1) 3)
        (= (x tap1) 2)
        (= (y tap1) 2)
        (= (carrying) 0)
        (= (poured plant1) 0)
        (= (total_poured) 0)
        (= (total_loaded) 0)
        (= (max_int) 20)
    )
    (:goal
        (and
            (= (poured plant1) 9)
            (= (total_poured) (poured plant1))
        )
    )
)