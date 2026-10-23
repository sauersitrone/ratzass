package de.simone.command;

import java.util.List;

public interface CommandQueueListener {

	public void commandIssued(List<Command> command);
}
