package se.lunderhage.pcr1000.backend.events;

public interface CommandResponseEvent extends Event {

	/**
	 * Check wether this CommandResponseEvent was succesful or not.
	 * @return
	 */
	public boolean isSuccessful();
}
