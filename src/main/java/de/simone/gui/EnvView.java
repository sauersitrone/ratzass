package de.simone.gui;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import de.simone.Env;

public class EnvView extends JPanel {

    private JCheckBox autoRestart;
    private JCheckBox useManners;
    private JCheckBox fogOfWar;
    private JCheckBox quitOnGameEnd;
    private JCheckBox autoCamera;
    private JSlider speed;

    public EnvView() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        autoRestart = UIUtils.getCheckBox("Auto Restart", Env.autoRestart, e -> Env.autoRestart = autoRestart.isSelected());
        useManners = UIUtils.getCheckBox("Use Manners", Env.useManners, e -> Env.useManners = useManners.isSelected());
        fogOfWar = UIUtils.getCheckBox("Fog of War", Env.fogOfWar, e -> Env.fogOfWar = fogOfWar.isSelected());
        quitOnGameEnd = UIUtils.getCheckBox("Quit on Game End", Env.quitOnGameEnd, e -> Env.quitOnGameEnd = quitOnGameEnd.isSelected());
        autoCamera = UIUtils.getCheckBox("Auto Camera", Env.autoCamera, e -> Env.autoCamera = autoCamera.isSelected());
        speed = UIUtils.getSlider(0, 100, Env.speed, e -> Env.speed = speed.getValue());
        speed.setBorder(new TitledBorder("Game Speed"));

        add(autoRestart);
        add(useManners);
        add(fogOfWar);
        add(quitOnGameEnd);
        add(autoCamera);
        add(speed);
        setBorder(new TitledBorder("Controls"));

    }

}
