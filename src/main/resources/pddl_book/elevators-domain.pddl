;Header and description

(define (domain elevators)
    (:requirements :strips :typing :conditional-effects :negative-preconditions :disjunctive-preconditions)

    (:types
        elevator passenger num - object
    )

    (:predicates
        (passenger-at ?person - passenger ?floor - num)
        (boarded ?person - passenger ?lift - elevator)
        (lift-at ?lift - elevator ?floor - num)
        (next ?n1 - num ?n2 - num)
        (requested ?person - passenger ?floor - num)
        (in-maintenaince)
        (above ?blw - num ?abv - num)
    )

    (:derived
        (above ?blw ?abv)
        (or (next ?blw ?abv)
            (exists
                (?z - num)
                (and (next ?blw ?z)
                    (above ?z ?abv))))
    )

    (:action move-up
        :parameters (?lift - elevator ?cur ?nxt - num)
        :precondition (and (lift-at ?lift ?cur) (next ?cur ?nxt))
        :effect (and (not (lift-at ?lift ?cur)) (lift-at ?lift ?nxt))
    )

    (:action move-down
        :parameters (?lift - elevator ?cur ?nxt - num)
        :precondition (and (lift-at ?lift ?cur) (next ?nxt ?cur))
        :effect (and (not (lift-at ?lift ?cur))
            (lift-at ?lift ?nxt))
    )

    (:action board
        :parameters (?person - passenger ?floor - num ?lift - elevator)
        :precondition (and (lift-at ?lift ?floor) (passenger-at ?person ?floor))
        :effect (and (boarded ?person ?lift))
    )

    (:action leave
        :parameters (?person - passenger ?floor - num ?lift - elevator)
        :precondition (and (lift-at ?lift ?floor) (boarded ?person ?lift))
        :effect (and (passenger-at ?person ?floor) (not (boarded ?person ?lift)))
    )

    (:action load
        :parameters (?floor - num ?lift - elevator)
        :precondition (and (lift-at ?lift ?floor))
        :effect (and (forall
                (?person - passenger)
                (when
                    (passenger-at ?person ?floor)
                    (and (not (passenger-at ?person ?floor))
                        (boarded ?person ?lift)))))
    )
    (:action unload
        :parameters (?floor - num ?lift - elevator)
        :precondition (and (lift-at ?lift ?floor))
        :effect (and (forall
                (?person - passenger)
                (when
                    (boarded ?person ?lift)
                    (and (passenger-at ?person ?floor)
                        (not (boarded ?person ?lift))))))
    )

    (:action stop
        :parameters (?floor - num ?lift - elevator)
        :precondition (and (lift-at ?lift ?floor)
            (exists
                (?person - passenger)
                (or (passenger-at ?person ?floor)
                    (boarded ?person ?lift))))
        :effect (forall
            (?person - passenger)
            (and (when
                    (passenger-at ?person ?floor)
                    (and (not (passenger-at ?person ?floor))
                        (boarded ?person ?lift)))
                (when
                    (boarded ?person ?lift)
                    (and (passenger-at ?person ?floor)
                        (not (boarded ?person ?lift))))))
    )

    (:action enter-maintenaince-mode
        :parameters (?lift - elevator ?floor - num ?person - passenger)
        :precondition (and (forall
                (?person - passenger)
                (and (not (boarded ?person ?lift))
                    (forall
                        (?flor - num)
                        (imply
                            (requested ?person ?floor)
                            (passenger-at ?person ?floor))
                    )
                )
            ))
        :effect (and (in-maintenaince))
    )

)