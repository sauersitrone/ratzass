/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.FillBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.TooltipBuilder;
import com.github.appreciated.apexcharts.config.chart.Sparkline;
import com.github.appreciated.apexcharts.config.chart.Toolbar;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.PaddingBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.RadialBarBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.Name;
import com.github.appreciated.apexcharts.config.plotoptions.radialbar.builder.RadialBarDataLabelsBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.tooltip.X;
import com.github.appreciated.apexcharts.config.tooltip.builder.FixedBuilder;
import com.github.appreciated.apexcharts.helper.Series;

public class SitroneStickers {

    private Toolbar toolbar;

    public SitroneStickers() {
        toolbar = new Toolbar();
        toolbar.setShow(false);
    }

    public ApexCharts getAreaSticker(String label, String color, Series<Double> series) {
        X x = new X();
        x.setShow(false);
        ApexChartsBuilder builder = new ApexChartsBuilder();
        builder
                .withChart(
                        ChartBuilder.get()
                                .withType(Type.AREA)
                                .withSparkline(new Sparkline(true))
                                .withToolbar(ToolbarBuilder.get().withShow(false).build())
                                .withZoom(ZoomBuilder.get().withEnabled(false).build())
                                .build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.STRAIGHT).withWidth(2.0).build())
                .withDataLabels(DataLabelsBuilder.get().withEnabled(false).build())
                .withFill(FillBuilder.get().withOpacity(1.0).build())
                .withGrid(
                        GridBuilder.get()
                                .withShow(false)
                                .withPadding(PaddingBuilder.get().withRight(0.0).withLeft(0.0).build())
                                .build())
                .withTooltip(
                        TooltipBuilder.get()
                                .withX(x)
                                .withFixed(FixedBuilder.get().withEnabled(true).withPosition("right").build())
                                .build())
                // .withTooltip(TooltipBuilder.get().withX(x).build())

                // .withTooltip(
                // TooltipBuilder.get()
                // .withCustom(
                // "function(struct) {\n"
                // + " return (\n"
                // + " '<div style=\"padding-left: 4px; padding-right: 4px\">' +\n"
                // + " \"<span>\"+\n"
                // + " struct.series[struct.seriesIndex][struct.dataPointIndex]+\n"
                // + " \"</span>\" +\n"
                // + " \"</div>\"\n"
                // + " );\n"
                // + "}")
                // .build())
                .withSeries(series)
                .withTitle(
                        TitleSubtitleBuilder.get()
                                .withText("Engagement")
                                // .withOffsetX(10.0)
                                .withStyle(
                                        com.github.appreciated.apexcharts.config.subtitle.builder.StyleBuilder.get()
                                                // .withColor("gray")
                                                .withFontSize("24px")
                                                .build())
                                .build())
        // .withSubtitle(TitleSubtitleBuilder.get().withText("Wie engagiert der Adult
        // mit dem System
        // ist.").build())
        ;
        if (color != null)
            builder.withColors(color);

        return builder.build();
    }

    public ApexCharts getRadialSticker(String label, String color, Double serie) {
        ApexChartsBuilder builder = new ApexChartsBuilder();
        Name name = new Name();
        name.setOffsetY(10D);
        builder
                .withChart(ChartBuilder.get().withType(Type.RADIALBAR).withOffsetY(-10D).build())
                .withLabels(label)
                .withColors(color)
                // .withDataLabels(DataLabelsBuilder.get().withEnabled(true).build())
                .withPlotOptions(
                        PlotOptionsBuilder.get()
                                .withRadialBar(
                                        RadialBarBuilder.get().withStartAngle(-135D).withEndAngle(135D)
                                                .withDataLabels(RadialBarDataLabelsBuilder.get().withName(name).build())
                                                // .withHollow(
                                                // HollowBuilder.get()
                                                // .withSize("50%")
                                                // .withImage("/icons/icons8-run-96.png")
                                                // .withWidth(140.0)
                                                // .withHeight(40.0)
                                                // .withClipped(false)
                                                .build())
                                .build())
                // .build())
                .withSeries(serie).withFill(FillBuilder.get().withType("gradient").build());

        ApexCharts charts = builder.build();
        return charts;
    }
}
