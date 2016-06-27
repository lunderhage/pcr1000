package se.lunderhage.pcr1000.backend.events;

/**
 * An event for a successful command.
 */
public class CommandSuccessfulEvent implements Event {
	
	@Override
	public String getCode() {
		return "H100";
	}

}
