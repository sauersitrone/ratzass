package de.simone.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JTextArea;

import de.simone.command.BuildAction;
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
            String message = String.format("%s, %s, %d, %s, %s", buildOrder.remitent,
                    buildOrder.unitType.toString(), buildOrder.quantity, buildOrder.status.toString(),
                    buildOrder.message);
            textArea.append(message + "\n");
            for (BuildAction action : buildOrder.getBuildActions()) {
                String ut = action.unitType == null ? "None" : action.unitType.toString();
                message = String.format("\t %s, %s, %d, %s, %s", action.action.toString(),
                        ut, action.quantity, action.status.toString(), action.message);
                textArea.append(message + "\n");
            }
        }

        // textArea.setText(AsciiTable.getTable(buildOrders, Arrays.asList(
        // new Column().header("Remitent").with(c -> c.remitent),
        // new Column().header("Type").with(c -> c.unitType.toString()),
        // new Column().header("Quantity").with(c -> "" + c.quantity),
        // new Column().header("Status").with(c -> c.status.toString()),
        // new Column().header("Message").with(c -> c.message))));
    }
}
