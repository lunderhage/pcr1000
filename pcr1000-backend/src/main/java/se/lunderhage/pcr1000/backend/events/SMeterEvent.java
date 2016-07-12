package se.lunderhage.pcr1000.backend.events;

public class SMeterEvent implements Event {

	private final int sMeter;
	private final String code;

	public SMeterEvent(String event) {
		sMeter = Integer.parseInt(event.substring(2, 3), 16);
		code = event;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
    public String toString() {
	    return getClass().getSimpleName() + "(" + sMeter + ")";
	}

}
