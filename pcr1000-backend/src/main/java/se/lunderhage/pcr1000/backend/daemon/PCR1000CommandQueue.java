package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import gnu.io.NRSerialPort;
import se.lunderhage.pcr1000.backend.subscribers.PowerStateSubscriber;
import se.lunderhage.pcr1000.backend.subscribers.PrintSubscriber;
import se.lunderhage.pcr1000.backend.tasks.Command;
import se.lunderhage.pcr1000.backend.tasks.FastBaudRate;
import se.lunderhage.pcr1000.backend.tasks.FastTransferMode;
import se.lunderhage.pcr1000.backend.tasks.PowerState;
import se.lunderhage.pcr1000.backend.tasks.Start;
import se.lunderhage.pcr1000.backend.tasks.Stop;

public class PCR1000CommandQueue {

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000CommandQueue.class);

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private String portName = null;
	private NRSerialPort serialPort = null;

	private EventListener eventListener = null;
	private EventBus eventBus = new EventBus();
	private CommandHandler commandOutput = new CommandHandler(null, eventBus);

	/**
	 * Finds and starts the PCR1000 if found.
	 * @return
	 */
	public static PCR1000CommandQueue find() {
		return new PCR1000CommandQueue(SerialPortUtils.getSerialPorts());
	}

	/**
	 * Starts the PCR1000 on the specified port.
	 * @param portName
	 * @return
	 */
	public static PCR1000CommandQueue create(String portName) {
		return new PCR1000CommandQueue(ImmutableList.of(portName));
	}

	private PCR1000CommandQueue(List<String> portNames) {
		if (LOG.isDebugEnabled()) {
			register(new PrintSubscriber());
		}
		init(portNames);
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
	
	private boolean findPCR1000(List<String> serialPortNames) throws TooManyListenersException {
		PowerStateSubscriber powerStateSubscriber = new PowerStateSubscriber();
		
		for (String portName : serialPortNames) {
			for (BaudRate baudRate : BaudRate.values()) {
				serialPort = SerialPortUtils.openSerialPort(portName, baudRate.getBaudRate());
				eventListener = new EventListener(eventBus, serialPort.getInputStream());
				serialPort.addEventListener(eventListener);
				commandOutput.setSerialOutput(serialPort.getOutputStream());
				eventBus.register(powerStateSubscriber);
				submitCommand(new PowerState());
	            if (powerStateSubscriber.isTurnedOn() != null) {
	            	this.portName = portName;
	            	return true;
	            }
	            closeSerialPort();
			}
		}
		throw new IllegalStateException("No (free) PCR1000 was found.");
	}

	private void init(List<String> serialPortNames) {

		try {
			
			findPCR1000(serialPortNames);
			
			if (serialPort.getBaud() == BaudRate.INITIAL.getBaudRate()) {
				submitCommand(new Start()).get();
				LOG.debug("Switching to fast baudrate...");
				submitCommand(new FastBaudRate()).get();
				closeSerialPort();
				serialPort = SerialPortUtils.openSerialPort(portName, BaudRate.FAST.getBaudRate());
			}
		
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
