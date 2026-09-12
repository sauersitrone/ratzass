package de.simone.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.simone.Env;
import de.simone.command.UnitsCenter;
import de.simone.command.UnitsCenterListener;
import de.simone.ui.system.Form;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class UnitsCenterView extends Form implements UnitsCenterListener {

    private JTextArea textArea;
    private JCheckBox showResourcesCB;

    public UnitsCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();
        showResourcesCB = UIUtils.getPropertyCheckBox("Show resources", Env.showResources,
                e -> {
                    Env.showResources = showResourcesCB.isSelected();
                    updated(UnitsCenter.unitEventsTable);
                });

        JPanel controlPanel = UIUtils.getControlPanel("Controls", showResourcesCB);

        JPanel north = UIUtils.getInVerticalPanel(UIUtils.getHeader("Units center", "Displays the current know units"),
                controlPanel);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        UnitsCenter.addListener(this);
        updated(UnitsCenter.unitEventsTable);
    }

    @Override
    public void updated(Table units) {
        Table table = units.copy();
        if (!Env.showResources) {
            List<Integer> index = new ArrayList<>();
            for (int i = 0; i < table.rowCount(); i++) {
                Row row = table.row(i);
                if (row.getString("type").startsWith("Resource_"))
                    index.add(i);
            }
            int[] rows = index.stream().mapToInt(Integer::intValue).toArray();
            if (rows.length > 0)
                table = table.dropRows(rows);
        }

        textArea.setText(table.printAll());
    }
}
