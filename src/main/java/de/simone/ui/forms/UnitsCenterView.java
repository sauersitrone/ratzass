package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import de.simone.Config;
import de.simone.UIUtils;
import de.simone.command.DogTag;
import de.simone.command.UnitsCenter;
import de.simone.command.UnitsCenterListener;
import de.simone.ui.system.Form;

public class UnitsCenterView extends Form implements UnitsCenterListener {

    private JTextArea textArea;
    private JCheckBox showResourcesCB;
    private JPanel header;
    private JPanel controlPanel;

    public UnitsCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();
        showResourcesCB = UIUtils.getPropertyCheckBox("Show resources", Config.showResources,
                e -> {
                    Config.showResources = showResourcesCB.isSelected();
                });

        controlPanel = UIUtils.getControlPanel("Controls", showResourcesCB);

        header = UIUtils.getHeader("Units center", "Displays the current know units");
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        UnitsCenter.addListener(this);

        textArea.setText("\n\tReady for control units.");
    }

    @Override
    public void updatePersonal(List<DogTag> units) {
        List<DogTag> list = new ArrayList<>(units);
        if (!Config.showResources)
            list = units.stream().filter(dogTag -> !dogTag.unitType.toString().startsWith("Resource_")).toList();

        textArea.setText(AsciiTable.getTable(AsciiTable.NO_BORDERS, list, Arrays.asList(
                new Column().header("Id").with(c -> "" + c.unit.getID()),
                new Column().header("UnitType").with(c -> c.unitType.toString()),
                new Column().header("Order").with(c -> c.unit.getOrder().toString()),
                new Column().header("SquadId").with(c -> "" + c.squadID),
                new Column().header("isEnemy").with(c -> "" + c.isEnemy),
                new Column().header("isAlive").with(c -> "" + c.isAlive))));
    }

    public JComponent getTitle() {
        return header;
    }

    public JComponent getControls() {
        return controlPanel;
    }
}
