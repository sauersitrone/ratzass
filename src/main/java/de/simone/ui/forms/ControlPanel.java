package de.simone.ui.forms;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;

import de.simone.Config;
import de.simone.UIUtils;
import de.simone.command.RBWListener;
import de.simone.ui.layout.ResponsiveLayout;
import de.simone.ui.layout.ResponsiveLayout.JustifyContent;

public class ControlPanel extends JPanel {

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
        ResponsiveLayout layout = new ResponsiveLayout(JustifyContent.START);
        layout.setJustifyContent(JustifyContent.START);
        layout.setHorizontalGap(5);
        setLayout(layout);
        Dimension preSize = new Dimension(150, 32);

        Timer timer = new Timer(100, e -> {
            String speed = String.format("%02d", RBWListener.codeSpeed);
            codeSpeed.setText("Code Speed: " + speed + "ms");
            minerals.setText("Minerals: " + RBWListener.currentMinerals);
            gas.setText("Gas: " + RBWListener.currentGas);
            supply.setText("Supply: " + RBWListener.currentSupplyUsed + "/" + RBWListener.currentSupplyTotal + "("
                    + RBWListener.currentSupplyLeft + ")");
        });
        timer.start();

        pauseResumeGame = new JButton("Pause Game");
        pauseResumeGame.addActionListener(e -> {
            if (RBWListener.game.isPaused()) {
                pauseResumeGame.setText("Pause Game");
                RBWListener.isPaused = false;
            } else {
                pauseResumeGame.setText("Resume Game");
                RBWListener.isPaused = true;
            }
        });
        fogOfWar = UIUtils.getPropertyCheckBox("Fog of War", Config.fogOfWar,
                e -> Config.fogOfWar = fogOfWar.isSelected());
        fogOfWar.setPreferredSize(preSize);
        userInput = UIUtils.getPropertyCheckBox("User Input", Config.userInput,
                e -> Config.userInput = userInput.isSelected());
        userInput.setPreferredSize(preSize);
        autoCamera = UIUtils.getPropertyCheckBox("Auto Camera", Config.autoCamera,
                e -> Config.autoCamera = autoCamera.isSelected());
        autoCamera.setPreferredSize(preSize);

        speed = UIUtils.getSlider(0, 100, Config.speed, e -> {
            Config.speed = speed.getValue();
            Config.save();
        });
        speed.setToolTipText("Game Speed");
        speed.setPreferredSize(preSize);
        JPanel speedPanel = new JPanel();
        speedPanel.add(speed);
        speedPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background: $Button.background;" +
                "arc:10;");

        codeSpeed = new JLabel("Code Speed: 0ms");
        codeSpeed.setPreferredSize(preSize);
        codeSpeed.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +1");
        minerals = new JLabel("Minerals: " + RBWListener.currentMinerals);
        minerals.setForeground(Color.blue);
        minerals.setPreferredSize(preSize);
        minerals.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +1");
        gas = new JLabel("Gas: " + RBWListener.currentGas);
        gas.setForeground(Color.green);
        gas.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +1");
        gas.setPreferredSize(preSize);
        supply = new JLabel("Supply: -");
        supply.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +1");
        supply.setForeground(Color.GRAY);
        supply.setPreferredSize(preSize);

        Border border = new CompoundBorder(new TitledBorder("Main controls"), new EmptyBorder(5, 5, 5, 5));

        // add(restartGame);
        add(pauseResumeGame);
        add(fogOfWar);
        add(userInput);
        add(autoCamera);
        add(speedPanel);
        add(codeSpeed);
        add(minerals);
        add(gas);
        add(supply);
        setBorder(border);
    }
}
