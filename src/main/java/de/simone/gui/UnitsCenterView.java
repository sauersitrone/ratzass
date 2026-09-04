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
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.system.Form;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class UnitsCenterView extends Form implements UnitsCenterListener {

    private JTextArea textArea;
    private JCheckBox showResourcesCB;

    public UnitsCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();
        showResourcesCB = UIUtils.getCheckBox("Show resources", Env.showResources,
                e -> {
                    Env.showResources = showResourcesCB.isSelected();
                    updated(UnitsCenter.getInstance().unitEventsTable);
                });

        JPanel controlPanel = UIUtils.getControlPanel("Controls", showResourcesCB);

        JPanel north = UIUtils.getInVerticalPanel(MyDrawerBuilder.getEnvView(),
                UIUtils.getHeader("Units center", "Displays the current know units"), controlPanel);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        UnitsCenter.getInstance().addListener(this);
        updated(UnitsCenter.getInstance().unitEventsTable);
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
            table = table.dropRows(index.stream().mapToInt(Integer::intValue).toArray());
        }

        textArea.setText(table.printAll());
    }
}
