package se.lunderhage.pcr1000.backend.tasks;


import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

/**
 * A command to be sent to the PCR1000.
 */
public abstract class Command {
	
	/**
	 * Encode the command into a byte[] that can be sent to the PCR1000.
	 * @return
	 */
	public abstract byte[] encode();
	
	public abstract String getCommand();
	
	/**
	 * Execute command/task on the PCR1000.
	 * @param serialOutput - The serial output for sending command.
	 * @param events - EventBus for incoming events from the PCR1000.
	 */
	public abstract void execute(CommandHandler commandOutput, EventBus events);
	
}
