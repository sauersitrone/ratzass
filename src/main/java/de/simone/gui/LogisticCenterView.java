package de.simone.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JTextArea;

import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.LogisticCenterListener;
import de.simone.ui.system.Form;

public class LogisticCenterView extends Form implements LogisticCenterListener {

    private JTextArea textArea;

    public LogisticCenterView() {
        setLayout(new BorderLayout());
        textArea = GuiUtils.getConsoleTextArea();

        add(GuiUtils.getHeader("Logistic Center", "Displays the current state of the logistic center."),
                BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);
        LogisticCenter.getInstance().addListener(this);
    }

    @Override
    public void updated(List<BuildOrder> buildOrders) {
        textArea.setText("");
        for (BuildOrder buildOrder : buildOrders) {
            // String message = buildOrder.toString().replace(",", "\n\t");
            String message = buildOrder.toString();
            textArea.append(message + "\n");
        }

        // textArea.setText(AsciiTable.getTable(buildOrders, Arrays.asList(
        // new Column().header("Remitent").with(c -> c.remitent),
        // new Column().header("Type").with(c -> c.unitType.toString()),
        // new Column().header("Quantity").with(c -> "" + c.quantity),
        // new Column().header("Status").with(c -> c.status.toString()),
        // new Column().header("Message").with(c -> c.message))));
    }
}
