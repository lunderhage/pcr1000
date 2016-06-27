package se.lunderhage.pcr1000.backend.tasks;

import java.io.OutputStream;

import com.google.common.eventbus.EventBus;

/**
 * Sets the baud rate to maximum (38400 baud)
 */
public class BaudRate extends Command {
	
	private static final String CMD = "G105\n";

	@Override
	public byte[] encode() {
		return CMD.getBytes();
	}
	
	public String getCommand() {
		return CMD;
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		// TODO Auto-generated method stub

	}

}
