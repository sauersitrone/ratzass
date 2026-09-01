package de.simone.ui.simple.color.harmony;

import de.simone.ui.simple.color.AbstractColorHarmony;

public class SplitComplementary extends AbstractColorHarmony {

    public SplitComplementary() {
        super("Split Complementary");
    }

    @Override
    public float[] getAngles(float selectedAngle) {
        int diff = 30;
        return new float[]{
                selectedAngle - 180 + diff,
                selectedAngle + 180 - diff
        };
    }

    @Override
    public int getSelectedColorIndex() {
        return 0;
    }
}
