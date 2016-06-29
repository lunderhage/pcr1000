package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lunderhage.pcr1000.backend.subscribers.PrintSubscriber;
import se.lunderhage.pcr1000.backend.tasks.Command;
import se.lunderhage.pcr1000.backend.tasks.Squelch;
import se.lunderhage.pcr1000.backend.tasks.Tune;
import se.lunderhage.pcr1000.backend.tasks.Volume;
import se.lunderhage.pcr1000.backend.types.Filter;
import se.lunderhage.pcr1000.backend.types.Mode;
import se.lunderhage.pcr1000.backend.types.RadioChannel;

/**
 * This class is the daemon that communicates with the PCR1000
 */
public class PCR1000 {

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000.class);
	
	private final String portName;
	private PCR1000CommandQueue commandQueue = null;
	
	public PCR1000(String portName) {
		this.portName = portName;
	}
	
	/**
	 * Turn on and init the PCR1000.
	 */
	public synchronized void start() {
		if (commandQueue == null || commandQueue.isShutdown()) {
			commandQueue = PCR1000CommandQueue.create(portName);
		}
	}
	
	/**
	 * Turn off PCR1000.
	 */
	public synchronized void stop() {
		if (commandQueue != null && !commandQueue.isShutdown()) {
			commandQueue.shutdown();
		}
	}
	
	/**
	 * Register subscriber on the event bus.
	 */
	public void register(Object subscriber) {
		commandQueue.register(subscriber);
	}
	
	public Future<?> submitCommand(Command command) {
		return commandQueue.submitCommand(command);
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, CommandException, TimeoutException {
		PCR1000 pcr1000 = new PCR1000("/dev/ttyUSB0");
		pcr1000.start();
		
		pcr1000.register(new PrintSubscriber());
		
		pcr1000.submitCommand(new Volume(80));
		pcr1000.submitCommand(new Squelch(147));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.WFM, Filter._230K, 107100000)));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.AM, Filter._15K,   122450000)));
		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.AM, Filter._15K,   118100000)));
//		pcr1000.submitCommand(new Tune(new RadioChannel(Mode.AM, Filter._15K,   126650000)));
		
		LOG.debug("Listening to radio... (press enter to quit)");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
		LOG.debug("Shutting down...");
		pcr1000.stop();
		
	}

}
