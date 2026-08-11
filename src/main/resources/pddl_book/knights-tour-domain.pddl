(define (domain knights-tour)

    (:requirements :negative-preconditions)

    (:predicates
        (at ?square)
        (visited ?square)
        (valid-move ?from ?to)
    )

    (:action move
        :parameters (?from ?to)
        :precondition (and (at ?from) (valid-move ?from ?to) (not(visited ?to)))
        :effect (and (not (at ?from)) (at ?to) (visited ?to))
    )
)