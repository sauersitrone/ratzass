package de.simone.command;

import java.util.List;

public interface CommandQueueListener {

	public void update(List<Command> command);
}
