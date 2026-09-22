package de.simone.ui.forms;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import de.simone.UIUtils;
import de.simone.command.Command;
import de.simone.command.CommandQueue;
import de.simone.command.CommandQueueListener;
import de.simone.ui.system.Form;

public class CommandQueueView extends Form implements CommandQueueListener {

    private JTextArea textArea = null;
    private JPanel header;

    public CommandQueueView() {
        setLayout(new BorderLayout());
        header = UIUtils.getHeader("Command Queue", "Displays the command queue in real-time.");
        textArea = UIUtils.getConsoleTextArea();
        add(textArea, BorderLayout.CENTER);

        update(CommandQueue.getCommands());
    }

    @Override
    public void update(List<Command> commands) {
        textArea.setText(AsciiTable.getTable(AsciiTable.NO_BORDERS, commands, Arrays.asList(
                new Column().header("Cicle").with(c -> "" + c.cycle),
                new Column().header("UnitId").with(c -> "" + c.unitId),
                new Column().header("targetId").with(c -> "" + c.targetId),
                new Column().header("order").with(c -> c.order.toString()),
                new Column().header("Position").with(c -> "" + c.position),
                new Column().header("Tile Position").with(c -> "" + c.tilePosition),
                new Column().header("Status").with(c -> c.status.toString()),
                new Column().header("Message").with(c -> c.message))));
    }

    public JComponent getTitle() {
        return header;
    }
}
