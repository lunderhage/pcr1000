package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandException;

/**
 * Task to start the PCR1000.
 */
public class Start extends Command  {
	
	private static final String CMD = "H101\n";

	@Override
	public byte[] encode() {
		return CMD.getBytes();
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		
		// Register for H100 and H101 events.
		
		// If we get H100 - turn on radio.
		
		// Send H1? it will trigger H100 or H101 if it has not already happend.
		
		// If we get H101 - radio is on.
			
//		try {
//			
//			// Check radio power state.
//			String response = execCommand("H1?\n");			
//			if ("H100".equals(response) || "H101".equals(response)) {
//				// If it does respond here, radio is in 9600 bps.
//				LOG.debug("Turning on radio and setting baudrate to 38400");
//				execCommand(new Start());
//				execCommand(new BaudRate());
//			}
//
//			// 38400 baud			
//			LOG.debug("Reconnecting at 38400 baud...");
//			closeSerialPort();
//			openSerialPort(portName, INITIAL_BAUDRATE);
//			// Start listener (which serves events on EventBus).
//
//			// When radio is initiated...
//			LOG.debug("Turning on radio.");
//			execCommand(new Start());
//			execCommand(new FastTransferMode(true));
//		} catch (CommandException | IOException | TimeoutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	}
	
	public String getCommand() {
		return CMD;
	}
	
}
