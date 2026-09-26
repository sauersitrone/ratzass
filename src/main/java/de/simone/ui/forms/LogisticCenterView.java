package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import de.simone.UIUtils;
import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.LogisticCenterListener;
import de.simone.ui.system.Form;

public class LogisticCenterView extends Form implements LogisticCenterListener {

    private JTextArea textArea;
    private JPanel header;

    public LogisticCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();
        header = UIUtils.getHeader("Logistic Center", "Displays the current state of the logistic center.");
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        LogisticCenter.addListener(this);

        textArea.setText("\n\tReady for new build orders.");
    }

    @Override
    public void update(List<BuildOrder> buildOrders) {
        textArea.setText(AsciiTable.getTable(AsciiTable.NO_BORDERS, buildOrders, Arrays.asList(
                new Column().header("Id").with(c -> "" + c.id),
                new Column().header("UnitType").with(c -> c.unitType.toString()),
                new Column().header("Action").with(c -> c.action.toString()),
                new Column().header("Quantity").with(c -> "" + c.quantity),
                new Column().header("Status").with(c -> c.getStatus().toString()),
                new Column().header("Message").with(c -> c.message))));
    }

    public JComponent getTitle() {
        return header;
    }

}
