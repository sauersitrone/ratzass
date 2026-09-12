package de.simone.gui;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import de.simone.Env;
import de.simone.RBWListener;

public class ControlPanel extends JPanel {

    private JCheckBox autoRestart;
    private JCheckBox useManners;
    private JCheckBox fogOfWar;
    private JCheckBox quitOnGameEnd;
    private JCheckBox autoCamera;
    private JSlider speed;
    private JLabel codeSpeed;

    public ControlPanel()  {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        Timer timer = new Timer(1000, e -> codeSpeed.setText("Code Speed: " + RBWListener.codeSpeed + "ms"));
        timer.start();

        autoRestart = UIUtils.getPropertyCheckBox("Auto Restart", Env.autoRestart, e -> Env.autoRestart = autoRestart.isSelected());
        useManners = UIUtils.getPropertyCheckBox("Use Manners", Env.useManners, e -> Env.useManners = useManners.isSelected());
        fogOfWar = UIUtils.getPropertyCheckBox("Fog of War", Env.fogOfWar, e -> Env.fogOfWar = fogOfWar.isSelected());
        quitOnGameEnd = UIUtils.getPropertyCheckBox("Quit on Game End", Env.quitOnGameEnd, e -> Env.quitOnGameEnd = quitOnGameEnd.isSelected());
        autoCamera = UIUtils.getPropertyCheckBox("Auto Camera", Env.autoCamera, e -> Env.autoCamera = autoCamera.isSelected());
        speed = UIUtils.getSlider(0, 100, Env.speed, e -> Env.speed = speed.getValue());
        speed.setBorder(new TitledBorder("Game Speed"));
        codeSpeed = new JLabel("Code Speed: 0ms");
        
        add(autoRestart);
        add(useManners);
        add(fogOfWar);
        add(quitOnGameEnd);
        add(autoCamera);
        add(speed);
        add(codeSpeed);
        setBorder(new TitledBorder("Main controls"));

    }

}
