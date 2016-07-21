package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

/**
 * Checks power state on the PCR1000.
 */
public class CheckPowerStateTask extends Task {
	
	private static final String CMD = "H1?\r\n";

	@Override
	public byte[] encode() {
		return null;
	}

	@Override
	public String getCommand() {
		return CMD;
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {
		try {
			commandOutput.execCommand(CMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}