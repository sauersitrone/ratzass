package de.simone.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.simone.RBWListener;
import lombok.extern.java.Log;

@Log
public class SpeedPanel extends JPanel {

    public int initialSpeed = 0;
    public int slowest = 100;
    public int fastest = 0;

    public SpeedPanel() {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL, fastest, slowest, initialSpeed);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                log.info("Setting game speed: " + slider.getValue());
                RBWListener.game.setLocalSpeed(slider.getValue());
            }
        });

        JLabel label = new JLabel("Game Speed");
        add(label);
        add(slider);
    }
}
