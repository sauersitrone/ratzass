package de.simone.ui.component.chart.renderer;

import org.jfree.chart.renderer.xy.StackedXYBarRenderer;

public class ChartStackedXYBarRenderer extends StackedXYBarRenderer {

    public ChartStackedXYBarRenderer() {
        setMargin(0.3);
    }

    @Override
    public String toString() {
        return "Stacked Bar";
    }
}
