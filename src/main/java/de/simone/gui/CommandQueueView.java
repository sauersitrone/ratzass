package de.simone.gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import de.simone.command.Command;
import de.simone.command.CommandQueue;
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.system.Form;

public class CommandQueueView extends Form {

    JTextArea textPane = null;
    int listSize = 30;
    Timer timer;

    public CommandQueueView() {
        setLayout(new BorderLayout());

        JPanel header = UIUtils.getHeader("Command Queue", "Displays the command queue in real-time.");

        textPane = UIUtils.getConsoleTextArea();
        JPanel north = UIUtils.getInVerticalPanel(MyDrawerBuilder.getEnvView(), header);
        add(north, BorderLayout.NORTH);
        add(textPane, BorderLayout.CENTER);

        timer = new Timer(1000, e -> {
            update();
        });
        timer.start();
        update();
    }

    public void update() {
        List<Command> commands = CommandQueue.getInstance().getCommands();
        int i0 = Math.max(0, commands.size() - listSize);
        List<Command> commands3 = commands.subList(i0, commands.size());
        StringBuffer buffer = new StringBuffer();

        for (Command command : commands3) {
            String msg = String.format("%d\t%s %s %d %s %d %s", command.cycle, command.order,
                    command.unitType, command.unitId, command.position, command.targetId, command.status);
            buffer.append(msg + "\n");
        }

        textPane.setText(buffer.toString());
    }
}
