package se.lunderhage.pcr1000.backend.tasks;


import java.io.IOException;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

public class FastTransferMode extends Command {

	private final static String CMD_FAST_TRANSFER_MODE_ENABLE = "G301";
	private final static String CMD_FAST_TRANSFER_MODE_DISABLE = "G300";
	
	private boolean enable = false;
	
	public FastTransferMode(boolean enable) {
		this.enable = enable;
	}

	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public String getCommand() {
		if (enable) {
			return CMD_FAST_TRANSFER_MODE_ENABLE;
		}
		return CMD_FAST_TRANSFER_MODE_DISABLE;
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {

		String cmd = enable == true ? CMD_FAST_TRANSFER_MODE_ENABLE : CMD_FAST_TRANSFER_MODE_DISABLE;
		
		try {
			commandOutput.execCommand(cmd);
			
			if (!commandOutput.lastCommandSuccessful()) {
				throw new RuntimeException("Failed to toggle fast transfer mode (" + enable + ").");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
