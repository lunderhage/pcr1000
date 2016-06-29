package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

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
	public void execute(CommandHandler commandOutput, EventBus events) {

		try {
			commandOutput.execCommand(CMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
