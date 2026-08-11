(define (problem turn-it-off)
    (:domain switch)

    (:init
        (switch-is-on)
    )

    (:goal
        (and
            (switch-is-off)
        )
    )
)