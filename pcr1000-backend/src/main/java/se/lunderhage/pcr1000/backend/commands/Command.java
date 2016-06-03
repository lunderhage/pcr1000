package se.lunderhage.pcr1000.backend.commands;

/**
 * A command to be sent to the PCR1000.
 */
public interface Command {
	
	/**
	 * Encode the command into a String that can be sent to the PCR1000.
	 * @return
	 */
	public String encode();

}
