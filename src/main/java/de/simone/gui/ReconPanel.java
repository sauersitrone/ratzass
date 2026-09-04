package de.simone.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import de.simone.RBWListener;

public class ReconPanel extends JPanel {

    private final Timer repaintTimer;
    private JTextArea textArea;

    public ReconPanel() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();

        add(UIUtils.getHeader("Recon", "Displays the current state of the game as seen by Ratzass."),
                BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);

        repaintTimer = new Timer(200, e -> refresh());
        repaintTimer.start();
    }

    private void refresh() {
        textArea.setText("");
        textArea.setText(RBWListener.unitEventsTable.print());
    }
}
