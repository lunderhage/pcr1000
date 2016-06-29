package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;

/**
 * Command to stop the PCR1000.
 */
public class Stop extends Command  {
	
	private static final Logger LOG = LoggerFactory.getLogger(Stop.class);
			
	private static final String CMD = "H100\n";

	@Override
	public byte[] encode() {
		return CMD.getBytes();
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {
		try {
			LOG.debug("Turning off radio...");
			commandOutput.execCommand(CMD);
			
			if (!commandOutput.lastCommandSuccessful()) {
				throw new RuntimeException("Failed to turn off radio.");
			}
			LOG.debug("Radio is turned off.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getCommand() {
		return CMD;
	}
	
}
