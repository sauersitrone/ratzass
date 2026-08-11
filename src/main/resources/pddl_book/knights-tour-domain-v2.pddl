(define (domain knights-tour-v2)

    (:requirements :negative-preconditions)

    (:predicates
        (at ?col ?row)
        (visited ?col ?row)
        (diff-by-one ?x ?y)
        (diff-by-two ?x ?y)
    )

    (:action move-2col-1row
        :parameters (?from-col ?to-col ?from-row ?to-row)
        :precondition (and (at ?from-col ?from-row) (diff-by-two ?from-col ?to-col) (diff-by-one ?from-row ?to-row) (not (visited ?to-col ?to-row)))
        :effect (and (not (at ?from-col ?from-row)) (at ?to-col ?to-row) (visited ?to-col ?to-row))
    )

    (:action move-2row-1col
        :parameters (?from-col ?to-col ?from-row ?to-row)
        :precondition (and (at ?from-col ?from-row) (diff-by-two ?from-row ?to-row) (diff-by-one ?from-col ?to-col) (not (visited ?to-col ?to-row)))
        :effect (and (not (at ?from-col ?from-row)) (at ?to-col ?to-row) (visited ?to-col ?to-row))
    )
)