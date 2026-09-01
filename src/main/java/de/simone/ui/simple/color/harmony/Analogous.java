package de.simone.ui.simple.color.harmony;

import de.simone.ui.simple.color.AbstractColorHarmony;

public class Analogous extends AbstractColorHarmony {

    public Analogous() {
        super("Analogous");
    }

    @Override
    public float[] getAngles(float selectedAngle) {
        int diff = 30;
        return new float[]{
                selectedAngle + diff,
                selectedAngle - diff
        };
    }

    @Override
    public int getSelectedColorIndex() {
        return 1;
    }
}
