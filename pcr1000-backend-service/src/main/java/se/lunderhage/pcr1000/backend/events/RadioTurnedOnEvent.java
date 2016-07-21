package se.lunderhage.pcr1000.backend.events;

/**
 * This event occurs when radio is turned on.
 */
public class RadioTurnedOnEvent implements Event {

	public static final String RADIO_TURNED_ON = "H101";

	@Override
	public String getCode() {
		return RADIO_TURNED_ON;
	}

	@Override
    public String toString() {
	    return getClass().getSimpleName();
	}
}
