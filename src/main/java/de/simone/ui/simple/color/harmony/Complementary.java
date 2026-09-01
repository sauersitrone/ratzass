package de.simone.ui.simple.color.harmony;

import de.simone.ui.simple.color.AbstractColorHarmony;

public class Complementary extends AbstractColorHarmony {

    public Complementary() {
        super("Complementary");
    }

    @Override
    public float[] getAngles(float selectedAngle) {
        return new float[]{selectedAngle + 180};
    }

    @Override
    public int getSelectedColorIndex() {
        return 0;
    }
}
