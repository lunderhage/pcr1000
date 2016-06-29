package se.lunderhage.pcr1000.backend.events;

/**
 * An event for a successful command.
 */
public class CommandSuccessfulEvent implements CommandResponseEvent {
	
	public static final String COMMAND_SUCCESSFUL = "G000";
	
	@Override
	public String getCode() {
		return COMMAND_SUCCESSFUL;
	}

	@Override
	public boolean isSuccessful() {
		return true;
	}

}
