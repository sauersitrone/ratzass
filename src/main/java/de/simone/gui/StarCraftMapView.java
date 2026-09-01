package de.simone.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.simone.Env;
import de.simone.ui.system.Form;

public class StarCraftMapView extends Form {

    private StarCraftTileMap starCraftTileMap;
    private JCheckBox drawIDsCB;
    private JCheckBox drawPingsCB;
    private JCheckBox drawPlayerUnitsCB;
    private JCheckBox drawEnemyUnitsCB;
    private JCheckBox drawNeutralUnitsCB;
    private JCheckBox drawResourcesCB;
    private JCheckBox drawStartSpotsCB;
    private JCheckBox drawRegionsCB;
    private JCheckBox fillRegionsCB;
    private JCheckBox drawChokepointsCB;

    public StarCraftMapView() {
        setLayout(new BorderLayout());
        this.starCraftTileMap = new StarCraftTileMap();

        JPanel header = GuiUtils.getHeader("Map Controls", "Controls for the StarCraft map.");

        drawIDsCB = GuiUtils.getCheckBox("draw IDs", Env.drawIDs, e -> Env.drawIDs = drawIDsCB.isSelected());
        drawPingsCB = GuiUtils.getCheckBox("draw Pings", Env.drawPings, e -> Env.drawPings = drawPingsCB.isSelected());
        drawPlayerUnitsCB = GuiUtils.getCheckBox("draw Player Units", Env.drawPlayerUnits,
                e -> Env.drawPlayerUnits = drawPlayerUnitsCB.isSelected());
        drawEnemyUnitsCB = GuiUtils.getCheckBox("draw Enemy Units", Env.drawEnemyUnits,
                e -> Env.drawEnemyUnits = drawEnemyUnitsCB.isSelected());
        drawNeutralUnitsCB = GuiUtils.getCheckBox("draw Neutral Units", Env.drawNeutralUnits,
                e -> Env.drawNeutralUnits = drawNeutralUnitsCB.isSelected());
        drawResourcesCB = GuiUtils.getCheckBox("draw Resources", Env.drawResources,
                e -> Env.drawResources = drawResourcesCB.isSelected());
        drawStartSpotsCB = GuiUtils.getCheckBox("draw Start Spots", Env.drawStartSpots,
                e -> Env.drawStartSpots = drawStartSpotsCB.isSelected());
        drawRegionsCB = GuiUtils.getCheckBox("draw Regions", Env.drawRegions,
                e -> Env.drawRegions = drawRegionsCB.isSelected());
        fillRegionsCB = GuiUtils.getCheckBox("fill Regions", Env.fillRegions,
                e -> Env.fillRegions = fillRegionsCB.isSelected());
        drawChokepointsCB = GuiUtils.getCheckBox("draw Chokepoints", Env.drawChokepoints,
                e -> Env.drawChokepoints = drawChokepointsCB.isSelected());

        JPanel controlPanel = GuiUtils.getControlPanel("Controls", drawIDsCB, drawPingsCB, drawPlayerUnitsCB,
                drawEnemyUnitsCB, drawNeutralUnitsCB, drawResourcesCB, drawStartSpotsCB, drawRegionsCB,
                fillRegionsCB, drawChokepointsCB);
        JPanel headerPanel = GuiUtils.getInVerticalPanel(header, controlPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(starCraftTileMap, BorderLayout.CENTER);
    }
}
