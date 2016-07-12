package se.lunderhage.pcr1000.backend.events;

/**
 * This event occurs when radio is turned off.
 */
public class RadioTurnedOffEvent implements Event {

    public static final String RADIO_TURNED_OFF = "H100";

    @Override
    public String getCode() {
        return RADIO_TURNED_OFF;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
