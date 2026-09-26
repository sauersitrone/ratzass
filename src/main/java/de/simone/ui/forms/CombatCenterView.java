package de.simone.ui.forms;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.simone.UIUtils;
import de.simone.command.CombatCenterListener;
import de.simone.ui.system.Form;
import de.simone.ui.utils.SystemForm;

@SystemForm(name = "Combat Center", 
description = "Displays the communications between squads and the military center",
tags = {"military", "combat", "squads", "battle"})
public class CombatCenterView extends Form implements CombatCenterListener {

    private JTextArea textArea;
    private JPanel header;

    public CombatCenterView() {
        setLayout(new BorderLayout());
        header = UIUtils.getHeader("Combat Center",
                "Displays the communications between squads and the military center.");
        textArea = UIUtils.getConsoleTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        textArea.setText("\n\tReady for combat.");
    }

    @Override
    public void update(String logs) {
        SwingUtilities.invokeLater(() -> textArea.setText(logs));
    }

    public JComponent getTitle() {
        return header;
    }
}
