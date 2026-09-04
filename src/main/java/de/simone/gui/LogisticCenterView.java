package de.simone.gui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import de.simone.command.BuildOrder;
import de.simone.command.LogisticCenter;
import de.simone.command.LogisticCenterListener;
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.system.Form;

public class LogisticCenterView extends Form implements LogisticCenterListener {

    private JTextArea textArea;

    public LogisticCenterView() {
        setLayout(new BorderLayout());
        textArea = UIUtils.getConsoleTextArea();

        JPanel north = UIUtils.getInVerticalPanel(MyDrawerBuilder.getEnvView(),
                UIUtils.getHeader("Logistic Center", "Displays the current state of the logistic center."));
        add(north, BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);
        LogisticCenter.getInstance().addListener(this);
    }

    @Override
    public void updated(List<BuildOrder> buildOrders) {
        
        textArea.setText(AsciiTable.getTable(AsciiTable.NO_BORDERS, buildOrders, Arrays.asList(
        new Column().header("Cicle").with(c -> ""+c.cicle),
        new Column().header("UnitType").with(c -> c.unitType.toString()),
        new Column().header("Action").with(c -> c.action.toString()),
        new Column().header("Quantity").with(c -> "" + c.quantity),
        new Column().header("Status").with(c -> c.status.toString()),
        new Column().header("Message").with(c -> c.message))));
    }
}
