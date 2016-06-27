package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import gnu.io.NRSerialPort;
import gnu.io.SerialPort;
import se.lunderhage.pcr1000.backend.tasks.BaudRate;
import se.lunderhage.pcr1000.backend.tasks.Command;
import se.lunderhage.pcr1000.backend.tasks.FastTransferMode;
import se.lunderhage.pcr1000.backend.tasks.Start;
import se.lunderhage.pcr1000.backend.tasks.Stop;

public class PCR1000CommandQueue {

	private static final int INITIAL_BAUDRATE = 9600;
	private static final int FAST_BAUDRATE = 38400;

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000CommandQueue.class);

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private String portName = null;
	private NRSerialPort serialPort = null;
	private InputStream serialInput = null;
	private OutputStream serialOutput = null;
	private EventListener eventListener = null;
	private EventBus eventBus = new EventBus();

	public static PCR1000CommandQueue create(String portName) {
		return new PCR1000CommandQueue(portName);
	}

	private PCR1000CommandQueue(String portName) {
		this.portName = portName;
		init();
	}

	public Future<?> submitCommand(Command command) {
		return executor.submit(new Runnable() {

			@Override
			public void run() {
				command.execute(serialOutput, eventBus);
			}

		});
	}
	
	public boolean isShutdown() {
		return executor.isShutdown();
	}

	private void init() {
		try {
			openSerialPort(portName, INITIAL_BAUDRATE);
			eventListener = new EventListener(eventBus, serialInput);

			// Submit initial powerup task
			submitCommand(new Start()).get();
			submitCommand(new BaudRate()).get();
			
			eventListener.stop();

			closeSerialPort();
			openSerialPort(portName, FAST_BAUDRATE);
			
			eventListener = new EventListener(eventBus, serialInput);

			// Submit powerup task and initiate fast transfer mode.
			submitCommand(new Start()).get();
			submitCommand(new FastTransferMode(true)).get();

			LOG.debug("PCR1000 is ready to receive commands.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
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

	private void openSerialPort(String portName, int baudrate) {
		serialPort = new NRSerialPort(portName, baudrate);

		LOG.debug("Opening serial port.");
		serialPort.connect();
		serialPort.getSerialPortInstance().setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

		// To keep radio turned on...
		serialPort.getSerialPortInstance().setRTS(true);
		serialPort.getSerialPortInstance().setDTR(true);

		serialInput = serialPort.getInputStream();
		serialOutput = serialPort.getOutputStream();		
	}

	private void closeSerialPort() {
		LOG.debug("Closing serial port.");
		serialPort.disconnect();
		try {
			serialOutput.close();
			serialInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void shutdown() {

		try {
			submitCommand(new Stop()).get();
			eventListener.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSerialPort();
		}

		executor.shutdown();
	}
}
