package de.simone.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import de.simone.Env;
import de.simone.ui.system.Form;

public class EnvView extends Form {

    private JCheckBox autoRestart;
    private JCheckBox useManners;
    private JCheckBox fogOfWar;
    private JCheckBox quitOnGameEnd;
    private JCheckBox autoCamera;
    private JSlider speed;

    public EnvView() {
        setLayout(new BorderLayout());

        JPanel header = GuiUtils.getHeader("Environment Variables", "Displays environment variables.");

        autoRestart = GuiUtils.getCheckBox("Auto Restart", Env.autoRestart, e -> Env.autoRestart = autoRestart.isSelected());
        useManners = GuiUtils.getCheckBox("Use Manners", Env.useManners, e -> Env.useManners = useManners.isSelected());
        fogOfWar = GuiUtils.getCheckBox("Fog of War", Env.fogOfWar, e -> Env.fogOfWar = fogOfWar.isSelected());
        quitOnGameEnd = GuiUtils.getCheckBox("Quit on Game End", Env.quitOnGameEnd, e -> Env.quitOnGameEnd = quitOnGameEnd.isSelected());
        autoCamera = GuiUtils.getCheckBox("Auto Camera", Env.autoCamera, e -> Env.autoCamera = autoCamera.isSelected());
        speed = GuiUtils.getSlider(0, 100, Env.speed, e -> Env.speed = speed.getValue());
        speed.setBorder(new TitledBorder("Game Speed"));

        JPanel controls = GuiUtils.getInVerticalPanel(autoRestart, useManners, fogOfWar, quitOnGameEnd, autoCamera, speed);
        controls.setBorder(new TitledBorder("Controls"));

        add(header, BorderLayout.NORTH);
        add(controls, BorderLayout.CENTER);
    }

}
