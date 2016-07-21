package se.lunderhage.pcr1000.backend.events;

/**
 * Decodes string responses from the radio to Events
 * that then can be posted on an event bus in order to
 * be listened for.
 */
public class EventDecoder {

	public static Event decode(String event) {

		switch (event) {
		case RadioTurnedOffEvent.RADIO_TURNED_OFF:
			return new RadioTurnedOffEvent();
		case RadioTurnedOnEvent.RADIO_TURNED_ON:
			return new RadioTurnedOnEvent();
		case CommandSuccessfulEvent.COMMAND_SUCCESSFUL:
			return new CommandSuccessfulEvent();
		case CommandFailedEvent.COMMAND_FAILED:
			return new CommandFailedEvent();
		}
		
		if (event.startsWith("G00")) {
			// Sometimes when switching baudrate we might get
			// an incomplete response.
			return new CommandSuccessfulEvent();
		}
		if (event.startsWith("I0")) {
			return new BusyStatus(event);
		}
	
		if (event.startsWith("I1")) {
			return new SMeterEvent(event);
		}
		
		if (event.startsWith("I2")) {
			return new CenterMeterLevel(event);
		}
		
		if (event.startsWith("I3")) {
			return new DTMFEvent(event);
		}
		
		if (event.startsWith("NE")) {
			return new BandScopeEvent(event);
		}
		return new UnknownEvent(event.trim());
	}
}
