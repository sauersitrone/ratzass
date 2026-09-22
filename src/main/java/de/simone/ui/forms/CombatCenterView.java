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

public class CombatCenterView extends Form implements CombatCenterListener {

    private JTextArea systemOutTextArea;
    private JPanel header;

    public CombatCenterView() {
        setLayout(new BorderLayout());
        header = UIUtils.getHeader("Combat Center",
                "Displays the communications between squads and the military center.");
        systemOutTextArea = UIUtils.getConsoleTextArea();
        add(new JScrollPane(systemOutTextArea), BorderLayout.CENTER);
    }

    @Override
    public void updated(String logs) {
        SwingUtilities.invokeLater(() -> systemOutTextArea.setText(logs));
    }

    public JComponent getTitle() {
        return header;
    }
}
