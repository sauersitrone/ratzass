package de.simone.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.simone.command.UnitsCenter;
import de.simone.command.UnitsCenterListener;
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.system.Form;
import tech.tablesaw.api.Table;

public class UnitsCenterView extends Form implements UnitsCenterListener {

    private JTextArea textArea;

    public UnitsCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();

        JPanel north = UIUtils.getInVerticalPanel(MyDrawerBuilder.getEnvView(),
                UIUtils.getHeader("Units center", "Displays the current know units"));
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        UnitsCenter.getInstance().addListener(this);
        updated(UnitsCenter.getInstance().unitEventsTable);
    }

    @Override
    public void updated(Table units) {
        textArea.setText(units.printAll());
    }
}
