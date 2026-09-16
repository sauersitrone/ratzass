package de.simone.gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import de.simone.Env;
import de.simone.RBWListener;

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

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        Timer timer = new Timer(100, e -> {
            codeSpeed.setText("Code Speed: " + RBWListener.codeSpeed + "ms");
            minerals.setText("Minerals: " + RBWListener.currentMinerals);
            gas.setText("Gas: " + RBWListener.currentGas);
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
                RBWListener.game.resumeGame();
            } else {
                System.out.println("ControlPanel.ControlPanel()");
                RBWListener.game.pauseGame();
                pauseResumeGame.setText("Resume Game");
            }
        });
        fogOfWar = UIUtils.getPropertyCheckBox("Fog of War", Env.fogOfWar,
                e -> Env.fogOfWar = fogOfWar.isSelected());
        userInput = UIUtils.getPropertyCheckBox("User Input", Env.userInput,
                e -> Env.userInput = userInput.isSelected());
        autoCamera = UIUtils.getPropertyCheckBox("Auto Camera", Env.autoCamera,
                e -> Env.autoCamera = autoCamera.isSelected());
        speed = UIUtils.getSlider(0, 100, Env.speed, e -> Env.speed = speed.getValue());
        speed.setBorder(new TitledBorder("Game Speed"));
        codeSpeed = new JLabel("Code Speed: 0ms");
        minerals = new JLabel("Minerals: " + RBWListener.currentMinerals);
        minerals.setForeground(Color.blue);
        gas = new JLabel("Gas: " + RBWListener.currentGas);
        gas.setForeground(Color.green);

        add(restartGame);
        add(pauseResumeGame);
        add(fogOfWar);
        add(userInput);
        add(autoCamera);
        add(speed);
        add(codeSpeed);
        add(minerals);
        add(gas);
        setBorder(new TitledBorder("Main controls"));

    }

}
