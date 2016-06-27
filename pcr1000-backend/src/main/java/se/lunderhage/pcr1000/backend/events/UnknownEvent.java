package se.lunderhage.pcr1000.backend.events;

/**
 * An unknown (or not yet implemented event).
 */
public class UnknownEvent implements Event {
	
	private final String code;
	
	public UnknownEvent(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
}
