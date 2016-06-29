package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.eventbus.EventBus;
import gnu.io.NRSerialPort;
import se.lunderhage.pcr1000.backend.subscribers.PowerStateSubscriber;
import se.lunderhage.pcr1000.backend.subscribers.PrintSubscriber;
import se.lunderhage.pcr1000.backend.tasks.BaudRate;
import se.lunderhage.pcr1000.backend.tasks.Command;
import se.lunderhage.pcr1000.backend.tasks.FastTransferMode;
import se.lunderhage.pcr1000.backend.tasks.PowerState;
import se.lunderhage.pcr1000.backend.tasks.Start;
import se.lunderhage.pcr1000.backend.tasks.Stop;

public class PCR1000CommandQueue {


	public static final int INITIAL_BAUDRATE = 9600;
	public static final int FAST_BAUDRATE = 38400;

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000CommandQueue.class);

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private String portName = null;
	private NRSerialPort serialPort = null;

	private EventListener eventListener = null;
	private EventBus eventBus = new EventBus();
	private CommandHandler commandOutput = new CommandHandler(null, eventBus);

	public static PCR1000CommandQueue create(String portName) {
		return new PCR1000CommandQueue(portName);
	}

	private PCR1000CommandQueue(String portName) {
		this.portName = portName;
		
		if (LOG.isDebugEnabled()) {
			register(new PrintSubscriber());
		}
		init();
	}

	public Future<?> submitCommand(Command command) {
		return executor.submit(new Runnable() {

			@Override
			public void run() {
				command.execute(commandOutput, eventBus);
			}

		});
	}
	
	public boolean isShutdown() {
		return executor.isShutdown();
	}

	private void init() {
		
		/*
		 * TODO:
		 * Radio might be on or off
		 * Radio might be in 9600 bps or 38400 bps.
		 * 
		 * Test if we get response on 9600.
		 * If yes: switch to 38400.
		 * If no: Radio might already be in 38400.
		 * 
		 * Test if we get response on 38400.
		 * Turn on fast transfer mode.
		 * 
		 * Done!
		 */

		try {
			serialPort = SerialPortUtils.openSerialPort(portName, INITIAL_BAUDRATE);
			eventListener = new EventListener(eventBus, serialPort.getInputStream());
			serialPort.addEventListener(eventListener);
			commandOutput.setSerialOutput(serialPort.getOutputStream());
			
			// Check power state in 9600.
			PowerStateSubscriber powerStateSubscriber = new PowerStateSubscriber();
			eventBus.register(powerStateSubscriber);
			submitCommand(new PowerState()).get();
			
			if (powerStateSubscriber.isTurnedOn() != null) {
				LOG.debug("Got power state response: {} Starting radio...");
				// Submit initial powerup task
				submitCommand(new Start());
				LOG.debug("Switching to fast baudrate...");
				submitCommand(new BaudRate()).get();				
			} else {
				LOG.debug("No power state response.");
			}
			

			eventBus.unregister(powerStateSubscriber);

			closeSerialPort();
			serialPort = SerialPortUtils.openSerialPort(portName, FAST_BAUDRATE);
			eventListener = new EventListener(eventBus, serialPort.getInputStream());
			serialPort.addEventListener(eventListener);
			commandOutput.setSerialOutput(serialPort.getOutputStream());
			
			// Submit powerup task and initiate fast transfer mode.
			LOG.debug("Starting radio in fast baudrate & enabling fast transfer mode...");
			submitCommand(new Start()).get();
			submitCommand(new FastTransferMode(true)).get();

			LOG.debug("PCR1000 is ready to receive commands.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Register subscriber on the event bus.
	 */
	public void register(Object subscriber) {
		eventBus.register(subscriber);
	}

	private void closeSerialPort() {
		LOG.debug("Closing serial port.");
		serialPort.removeEventListener();
		serialPort.disconnect();
		try {
			commandOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {

		try {
			submitCommand(new Stop()).get();
			closeSerialPort();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		executor.shutdown();
	}
}
