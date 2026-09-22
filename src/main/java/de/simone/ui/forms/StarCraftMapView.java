package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.simone.Config;
import de.simone.UIUtils;
import de.simone.ui.system.Form;
import raven.modal.Toast;

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
    private JCheckBox liveSyncCB;
    private JButton saveMapButton;
    private JButton loadMapButton;
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
        fillRegionsCB = UIUtils.getPropertyCheckBox("fill Regions", Config.fillRegions,
                e -> Config.fillRegions = fillRegionsCB.isSelected());
        drawChokepointsCB = UIUtils.getPropertyCheckBox("draw Chokepoints", Config.drawChokepoints,
                e -> Config.drawChokepoints = drawChokepointsCB.isSelected());
        liveSyncCB = UIUtils.getPropertyCheckBox("Live Sync", starCraftTileMap.isLiveSync(),
                e -> starCraftTileMap.setLiveSync(liveSyncCB.isSelected()));

        saveMapButton = new JButton("Save Map...");
        saveMapButton.addActionListener(e -> saveMap());
        loadMapButton = new JButton("Load Map...");
        loadMapButton.addActionListener(e -> loadMap());

        controlPanel = UIUtils.getControlPanel("Controls", drawIDsCB, drawPingsCB, drawPlayerUnitsCB,
                drawEnemyUnitsCB, drawNeutralUnitsCB, drawResourcesCB, drawStartSpotsCB, drawRegionsCB,
                fillRegionsCB, drawChokepointsCB, liveSyncCB, saveMapButton, loadMapButton);

        add(starCraftTileMap, BorderLayout.CENTER);
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }

    private void saveMap() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tiled map (*.tmx)", "tmx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".tmx")) {
            file = new File(file.getParentFile(), file.getName() + ".tmx");
        }
        try {
            starCraftTileMap.saveMap(file);
            UIUtils.showToast(this, Toast.Type.SUCCESS, "Map saved successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save map: " + ex.getMessage(),
                    "Save Map", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMap() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tiled map (*.tmx)", "tmx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            starCraftTileMap.loadMap(chooser.getSelectedFile());
            liveSyncCB.setSelected(starCraftTileMap.isLiveSync());
            UIUtils.showToast(this, Toast.Type.SUCCESS, "Map loaded successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load map: " + ex.getMessage(),
                    "Load Map", JOptionPane.ERROR_MESSAGE);
        }
    }
}
