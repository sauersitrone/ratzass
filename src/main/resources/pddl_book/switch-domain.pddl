(define (domain switch)

    (:requirements :strips)

    (:predicates
        (switch-is-on)
        (switch-is-off)
    )

    (:action switch-on
        :parameters ()
        :precondition (and (switch-is-off))
        :effect (and (switch-is-on) (not (switch-is-off)))
    )

    (:action switch-off
        :parameters ()
        :precondition (and (switch-is-on))
        :effect (and (switch-is-off) (not (switch-is-on)))
    )
)