(define (problem build-marine)
    (:domain starcraft)

    (:init
        (has-unit Terran_Marine)
        (has-mineral mineral)
    )

    (:goal
        (and
            (exists (?m - Terran_Marine)
                (has-unit ?m)
            )
        )
    )
)
