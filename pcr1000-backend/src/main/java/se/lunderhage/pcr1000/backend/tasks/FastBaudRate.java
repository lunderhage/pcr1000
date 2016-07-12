package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

/**
 * Sets the baud rate to maximum (38400 baud)
 */
public class FastBaudRate extends Command {

	private static final String CMD = /* "G105" */ "G104"; // 19200 is a workaround for a bug in NRSerialPort.

	@Override
	public byte[] encode() {
		return CMD.getBytes();
	}

	@Override
    public String getCommand() {
		return CMD;
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {

		try {
			commandOutput.execCommand(CMD);
			// No point waiting here since we changed the baud rate.
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
