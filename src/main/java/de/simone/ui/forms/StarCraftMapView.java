package de.simone.ui.forms;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.simone.Config;
import de.simone.UIUtils;
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
    private JCheckBox drawAreasCB;
    private JCheckBox drawChokepointsCB;
    private JPanel header;
    private JPanel controlPanel;

    public StarCraftMapView() {
        setLayout(new BorderLayout());
        this.starCraftTileMap = new StarCraftTileMap();

        header = UIUtils.getHeader("Map Controls", "Controls for the StarCraft map.");

        drawIDsCB = UIUtils.getPropertyCheckBox("draw IDs", Config.drawIDs,
                e -> Config.drawIDs = drawIDsCB.isSelected());
        drawPingsCB = UIUtils.getPropertyCheckBox("draw Pings", Config.drawPings,
                e -> Config.drawPings = drawPingsCB.isSelected());
        drawPlayerUnitsCB = UIUtils.getPropertyCheckBox("draw Player Units", Config.drawPlayerUnits,
                e -> Config.drawPlayerUnits = drawPlayerUnitsCB.isSelected());
        drawEnemyUnitsCB = UIUtils.getPropertyCheckBox("draw Enemy Units", Config.drawEnemyUnits,
                e -> Config.drawEnemyUnits = drawEnemyUnitsCB.isSelected());
        drawNeutralUnitsCB = UIUtils.getPropertyCheckBox("draw Neutral Units", Config.drawNeutralUnits,
                e -> Config.drawNeutralUnits = drawNeutralUnitsCB.isSelected());
        drawResourcesCB = UIUtils.getPropertyCheckBox("draw Resources", Config.drawResources,
                e -> Config.drawResources = drawResourcesCB.isSelected());
        drawStartSpotsCB = UIUtils.getPropertyCheckBox("draw Start Spots", Config.drawStartSpots,
                e -> Config.drawStartSpots = drawStartSpotsCB.isSelected());
        drawRegionsCB = UIUtils.getPropertyCheckBox("draw Regions", Config.drawRegions,
                e -> Config.drawRegions = drawRegionsCB.isSelected());
        drawAreasCB = UIUtils.getPropertyCheckBox("draw Areas", Config.drawAreas,
                e -> Config.drawAreas = drawAreasCB.isSelected());
        drawChokepointsCB = UIUtils.getPropertyCheckBox("draw Chokepoints", Config.drawChokepoints,
                e -> Config.drawChokepoints = drawChokepointsCB.isSelected());

        controlPanel = UIUtils.getControlPanel("Controls", drawIDsCB, drawPingsCB, drawPlayerUnitsCB,
                drawEnemyUnitsCB, drawNeutralUnitsCB, drawResourcesCB, drawStartSpotsCB, drawRegionsCB,
                drawAreasCB, drawChokepointsCB );

        add(starCraftTileMap, BorderLayout.CENTER);
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }
}
