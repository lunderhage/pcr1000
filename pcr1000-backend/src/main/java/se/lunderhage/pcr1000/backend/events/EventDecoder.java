package se.lunderhage.pcr1000.backend.events;

public class EventDecoder {

	private static final String COMMAND_OK = "H100";
	private static final String COMMAND_NOK = "H101";

	public static Event decode(String event) {

		switch (event) {
		case COMMAND_OK:
			return new CommandSuccessfulEvent();
		case COMMAND_NOK:
			return new CommandFailedEvent();
		}
		return new UnknownEvent(event);
	}
}
