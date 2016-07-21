package se.lunderhage.pcr1000.backend.daemon;

/**
 * Thrown when a command fails.
 */
public class CommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandException(String message) {
		super(message);
	}
	
	

}
