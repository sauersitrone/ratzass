;Header and description

(define (domain linehaul-with-costs)

    (:requirements :strips :typing :numeric-fluents)

    (:types
        location truck quantity - object
        refrigerated_truck - truck
    )

    (:predicates
        (at ?t - truck ?l - location)
        (free_capacity ?t - truck ?q - quantity)
        (demand_chilled_goods ?l - location ?q - quantity)
        (demand_ambient_goods ?l - location ?q - quantity)
        (plus1 ?q1 ?q2 - quantity)
    )

    (:functions
        (distance ?l1 ?l2 - location)
        (cost_per_km ?t - truck)
        (total_cost)
    )

    (:action deliver_ambient
        :parameters (?t - truck ?l - location ?d ?d_less_one ?c ?c_less_one - quantity)
        :precondition (and (at ?t ?l)
            (demand_ambient_goods ?l ?d)
            (free_capacity ?t ?c)
            (plus1 ?d_less_one ?d) ; only true if x={?d,?c}, x > n0
            (plus1 ?c_less_one ?c)) ; and x = x_less_one + 1
        :effect (and (not (demand_ambient_goods ?l ?d))
            (demand_ambient_goods ?l ?d_less_one)
            (not (free_capacity ?t ?c))
            (free_capacity ?t ?c_less_one))
    )
    (:action deliver_chilled
        ;; Note type restriction on ?t: it must be a refrigerated truck.
        :parameters (?t - refrigerated_truck ?l - location ?d ?d_less_one ?c ?c_less_one - quantity)
        :precondition (and (at ?t ?l)
            (demand_chilled_goods ?l ?d)
            (free_capacity ?t ?c)
            (plus1 ?d_less_one ?d) ; only true if x={?d,?c}, x > n0
            (plus1 ?c_less_one ?c)) ; and x = x_less_one + 1
        :effect (and (not (demand_chilled_goods ?l ?d))
            (demand_chilled_goods ?l ?d_less_one)
            (not (free_capacity ?t ?c))
            (free_capacity ?t ?c_less_one))
    )

    (:action drive
        :parameters (?t - truck ?from ?to - location)
        :precondition (and (at ?t ?from))
        :effect (and
            (not (at ?t ?from))
            (at ?t ?to)
            (increase
                (total_cost)
                (* (distance ?from ?to) (cost_per_km ?t))))
    )

)