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

        JPanel header = UIUtils.getHeader("Map Controls", "Controls for the StarCraft map.");

        drawIDsCB = UIUtils.getPropertyCheckBox("draw IDs", Env.drawIDs, e -> Env.drawIDs = drawIDsCB.isSelected());
        drawPingsCB = UIUtils.getPropertyCheckBox("draw Pings", Env.drawPings, e -> Env.drawPings = drawPingsCB.isSelected());
        drawPlayerUnitsCB = UIUtils.getPropertyCheckBox("draw Player Units", Env.drawPlayerUnits,
                e -> Env.drawPlayerUnits = drawPlayerUnitsCB.isSelected());
        drawEnemyUnitsCB = UIUtils.getPropertyCheckBox("draw Enemy Units", Env.drawEnemyUnits,
                e -> Env.drawEnemyUnits = drawEnemyUnitsCB.isSelected());
        drawNeutralUnitsCB = UIUtils.getPropertyCheckBox("draw Neutral Units", Env.drawNeutralUnits,
                e -> Env.drawNeutralUnits = drawNeutralUnitsCB.isSelected());
        drawResourcesCB = UIUtils.getPropertyCheckBox("draw Resources", Env.drawResources,
                e -> Env.drawResources = drawResourcesCB.isSelected());
        drawStartSpotsCB = UIUtils.getPropertyCheckBox("draw Start Spots", Env.drawStartSpots,
                e -> Env.drawStartSpots = drawStartSpotsCB.isSelected());
        drawRegionsCB = UIUtils.getPropertyCheckBox("draw Regions", Env.drawRegions,
                e -> Env.drawRegions = drawRegionsCB.isSelected());
        fillRegionsCB = UIUtils.getPropertyCheckBox("fill Regions", Env.fillRegions,
                e -> Env.fillRegions = fillRegionsCB.isSelected());
        drawChokepointsCB = UIUtils.getPropertyCheckBox("draw Chokepoints", Env.drawChokepoints,
                e -> Env.drawChokepoints = drawChokepointsCB.isSelected());

        JPanel controlPanel = UIUtils.getControlPanel("Controls", drawIDsCB, drawPingsCB, drawPlayerUnitsCB,
                drawEnemyUnitsCB, drawNeutralUnitsCB, drawResourcesCB, drawStartSpotsCB, drawRegionsCB,
                fillRegionsCB, drawChokepointsCB);
        JPanel headerPanel = UIUtils.getInVerticalPanel(header, controlPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(starCraftTileMap, BorderLayout.CENTER);
    }
}
