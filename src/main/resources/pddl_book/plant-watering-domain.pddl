; an agent in a grid domain that aims to water plants carrying water from tap to plants
(define (domain plant-watering)

    (:requirements :typing :numeric-fluents)
    (:types
        thing location - object
        agent plant tap - thing
    )

    (:functions
        (maxx) ; grid domain
        (maxy) ; grid domain
        (minx) ; grid domain
        (miny) ; grid domain
        (x ?t - thing) ; x coordinate of a thing t
        (y ?t - thing) ; y coordinate of a thing t
        (carrying) ; the amount of water being carryied
        (poured ?p - plant) ; the mounts of water poured to the plant p
        (total_poured) ; total water poured by the agent
        (total_loaded) ; total water loaded by the agent
        (max_int) ;???
    )

    (:action move_up
        :parameters (?a - agent)
        :precondition (and
            (<= (+ (y ?a) 1)(maxy))
        )
        :effect (and
            (increase (y ?a) 1)
        )
    )
    (:action move_down
        :parameters (?a - agent)
        :precondition (and
            (>= (- (y ?a) 1) (miny))
        )
        :effect (and
            (decrease (y ?a) 1)
        )
    )

    (:action movet_right
        :parameters (?a - agent)
        :precondition (and
            (<= (+ (x ?a) 1) (maxx))
        )
        :effect (and
            (increase (x ?a) 1)
        )
    )

    (:action move_left
        :parameters (?a - agent)
        :precondition (and
            (>= (- (x ?a) 1) (minx))
        )
        :effect (and
            (decrease (x ?a) 1)
        )
    )

    (:action move_up_left
        :parameters (?a - agent)
        :precondition (and (>= (- (x ?a) 1) (minx)) (<= (+ (y ?a) 1) (maxy)))
        :effect (and
            (increase (y ?a) 1) (decrease (x ?a) 1))
    )

    (:action move_up_right
        :parameters (?a - agent)
        :precondition (and (<= (+ (x ?a) 1) (maxx)) (<= (+ (y ?a) 1) (maxy)))
        :effect (and
            (increase (y ?a) 1) (increase (x ?a) 1))
    )

    (:action move_down_left
        :parameters (?a - agent)
        :precondition (and (>= (- (x ?a) 1) (minx)) (>= (- (y ?a) 1) (miny)))
        :effect (and
            (decrease (x ?a) 1) (decrease (y ?a) 1))
    )

    (:action move_down_right
        :parameters (?a - agent)
        :precondition (and (<= (+ (x ?a) 1) (maxx)) (>= (- (y ?a) 1) (miny)))
        :effect (and
            (decrease (y ?a) 1) (increase (x ?a) 1))
    )

    ; load 1 unit of water from the tap
    (:action load
        :parameters (?a - agent ?t - tap)
        :precondition (and
            (= (x ?a) (x ?t))
            (= (y ?a) (y ?t))
            (<= (+ (total_loaded) 1) (max_int))
            (<= (+ (carrying) 1) (max_int))
        )
        :effect (and
            (increase (carrying) 1)
            (increase (total_loaded) 1)
        )
    )
    (:action pour
        :parameters (?a - agent ?p - plant)
        :precondition (and
            (= (x ?a) (x ?p))
            (= (y ?a) (y ?p))
            (>= (carrying) 1)
            (<= (+ (total_poured) 1) (max_int))
            (<= (+ (poured ?p) 1) (max_int))
        )
        :effect (and
            (decrease (carrying) 1)
            (increase (poured ?p) 1)
            (increase (total_poured) 1)
        )
    )

)