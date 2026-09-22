package de.simone.ui.forms;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import de.simone.Config;
import de.simone.RBWListener;
import de.simone.UIUtils;

public class ControlPanel extends JPanel {

    private JButton restartGame;
    private JButton pauseResumeGame;
    private JCheckBox fogOfWar;
    private JCheckBox userInput;
    private JCheckBox autoCamera;
    private JSlider speed;
    private JLabel codeSpeed;
    private JLabel minerals;
    private JLabel gas;
    private JLabel supply;

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        Timer timer = new Timer(100, e -> {
            codeSpeed.setText("Code Speed: " + RBWListener.codeSpeed + "ms");
            minerals.setText("Minerals: " + RBWListener.currentMinerals);
            gas.setText("Gas: " + RBWListener.currentGas);
            supply.setText("Supply: " + RBWListener.currentSupplyLeft);
        });
        timer.start();

        restartGame = new JButton("Restart Game");
        restartGame.addActionListener(e -> {
            RBWListener.game.restartGame();
        });
        pauseResumeGame = new JButton("Pause Game");
        pauseResumeGame.addActionListener(e -> {
            if(RBWListener.game.isPaused()) {
                pauseResumeGame.setText("Pause Game");
                RBWListener.isPaused = false;
            } else {
                pauseResumeGame.setText("Resume Game");
                RBWListener.isPaused = true;
            }
        });
        fogOfWar = UIUtils.getPropertyCheckBox("Fog of War", Config.fogOfWar,
                e -> Config.fogOfWar = fogOfWar.isSelected());
        userInput = UIUtils.getPropertyCheckBox("User Input", Config.userInput,
                e -> Config.userInput = userInput.isSelected());
        autoCamera = UIUtils.getPropertyCheckBox("Auto Camera", Config.autoCamera,
                e -> Config.autoCamera = autoCamera.isSelected());
        speed = UIUtils.getSlider(0, 100, Config.speed, e -> Config.speed = speed.getValue());
        speed.setBorder(new TitledBorder("Game Speed"));
        codeSpeed = new JLabel("Code Speed: 0ms");
        minerals = new JLabel("Minerals: " + RBWListener.currentMinerals);
        minerals.setForeground(Color.blue);
        gas = new JLabel("Gas: " + RBWListener.currentGas);
        gas.setForeground(Color.green);
        supply = new JLabel("Supply: " + RBWListener.currentSupplyLeft);
        supply.setForeground(Color.GRAY);

        add(restartGame);
        add(pauseResumeGame);
        add(fogOfWar);
        add(userInput);
        add(autoCamera);
        add(speed);
        add(codeSpeed);
        add(minerals);
        add(gas);
        add(supply);
        setBorder(new TitledBorder("Main controls"));
    }
}
