(define (problem knights-tour-problem)
    (:domain knights-tour)

    (:objects
        A1 A2 A3 A4 A5 A6 A7 A8 B1 B2 B3 B4 B5 B6 B7 B8 C1 C2 C3 C4 C5 C6 C7 C8 D1 D2 D3 D4 D5 D6 D7 D8 E1 E2 E3 E4 E5 E6 E7 E8 F1 F2 F3 F4 F5 F6 F7 F8 G1 G2 G3 G4 G5 G6 G7 G8 H1 H2 H3 H4 H5 H6 H7 H8
    )

    (:init
        (at A8)
        (visited A8)
        (valid-move A8 B6)
        (valid-move B6 A8)
        (valid-move A8 C7)
        (valid-move C7 A8)
        (valid-move B8 A6)
        (valid-move A6 B8)
        (valid-move B8 C6)
        ; TODO: there are more moves 
    )

    (:goal (and
        (visited A1) (visited A2) (visited A3) (visited A4) (visited A5) (visited A6) (visited A7) (visited A8)
        (visited B1) (visited B2) (visited B3) (visited B4) (visited B5) (visited B6) (visited B7) (visited B8)
        (visited C1) (visited C2) (visited C3) (visited C4) (visited C5) (visited C6) (visited C7) (visited C8)
        (visited D1) (visited D2) (visited D3) (visited D4) (visited D5) (visited D6) (visited D7) (visited D8)
        (visited E1) (visited E2) (visited E3) (visited E4) (visited E5) (visited E6) (visited E7) (visited E8)
        (visited F1) (visited F2) (visited F3) (visited F4) (visited F5) (visited F6) (visited F7) (visited F8)
        (visited G1) (visited G2) (visited G3) (visited G4) (visited G5) (visited G6) (visited G7) (visited G8)
        (visited H1) (visited H2) (visited H3) (visited H4) (visited H5) (visited H6) (visited H7) (visited H8)
    ))
)