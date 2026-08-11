(define (problem problem01)
    (:domain elevators)

    (:objects
        n1 n2 n3 n4 n5 - num
        p1 p2 p3 - passenger
        e1 e2 - elevator
    )

    (:init
        (next n1 n2)
        (next n2 n3)
        (next n3 n4)
        (next n4 n5)
        (lift-at e1 n1)
        (lift-at e2 n5)
        (passenger-at p1 n2)
        (passenger-at p2 n2)
        (passenger-at p3 n4)
        (requested p1 n1)
        (requested p2 n1)
        (requested p3 n1)
    )

    (:goal
        (and
            (in-maintenaince)
        )
    )
)