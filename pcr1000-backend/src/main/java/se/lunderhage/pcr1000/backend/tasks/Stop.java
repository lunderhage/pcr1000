package se.lunderhage.pcr1000.backend.tasks;

import java.io.OutputStream;

import com.google.common.eventbus.EventBus;

/**
 * Command to stop the PCR1000.
 */
public class Stop extends Command  {
	
	private static final String CMD = "H100\n";

	@Override
	public byte[] encode() {
		return CMD.getBytes();
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		// TODO Auto-generated method stub
		
	}
	
	public String getCommand() {
		return CMD;
	}
	
}
