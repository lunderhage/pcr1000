package se.lunderhage.pcr1000.backend.events;

/**
 * Event for command failure.
 */
public class CommandFailedEvent implements Event {

	@Override
	public String getCode() {
		return "H101";
	}

}
