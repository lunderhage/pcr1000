package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import se.lunderhage.pcr1000.backend.tasks.CheckPowerStateTask;
import se.lunderhage.pcr1000.backend.tasks.FastTransferModeTask;
import se.lunderhage.pcr1000.backend.tasks.SetFastBaudRate;
import se.lunderhage.pcr1000.backend.tasks.StartTask;
import se.lunderhage.pcr1000.backend.tasks.StopTask;
import se.lunderhage.pcr1000.backend.tasks.Task;

public class PCR1000CommandQueue {

	private static final Logger LOG = LoggerFactory.getLogger(PCR1000CommandQueue.class);

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private String portName = null;
	private NRSerialPort serialPort = null;

	private EventListener eventListener = null;
	private EventBus eventBus = new EventBus();
	private CommandHandler commandOutput = new CommandHandler(null, eventBus);

	public static PCR1000CommandQueue find() {

	    for (String serialPort : SerialPortUtils.getSerialPorts()) {
	        for (BaudRate baudRate : BaudRate.values()) {
    	        if (testPort(serialPort, baudRate.getBaudRate())) {
    	            // TODO: Make use of all information we got here in order to
    	            // speed up the startup of the radio.
    	            LOG.debug("Found radio on {}@{}bps", serialPort, baudRate.getBaudRate());
    	            return new PCR1000CommandQueue(serialPort);
    	        }
    	        LOG.debug("No response on {}@{}bps.", serialPort, baudRate.getBaudRate());
	        }
	    }
	    throw new IllegalStateException("No (free) PCR1000 was found.");
	}

	public static PCR1000CommandQueue create(String portName) {
		return new PCR1000CommandQueue(portName);
	}

	/*
	 * This isn't beautiful, but it seems to the most stable
	 * way to start the radio for now.
	 */
    public static boolean testPort(String portName, int baudRate) {
        // TODO: Find PCR1000 on specified port.
        NRSerialPort serialPort = null;
        EventBus eventBus = new EventBus();
        PowerStateSubscriber powerState = new PowerStateSubscriber();
        try {
            serialPort = SerialPortUtils.openSerialPort(portName, baudRate);
            InputStream input = serialPort.getInputStream();
            EventListener eventListener = new EventListener(eventBus, input);
            serialPort.addEventListener(eventListener);
            eventBus.register(powerState);
            OutputStream output = serialPort.getOutputStream();

            output.write(new CheckPowerStateTask().getCommand().getBytes());
            output.write("\r\n".getBytes());
            output.flush();
            if (powerState.gotResponse()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } finally {
            eventBus.unregister(powerState);
            serialPort.removeEventListener();
            if (serialPort != null) {
                serialPort.disconnect();
            }
        }

        return false;
    }


	private PCR1000CommandQueue(String portName) {
		this.portName = portName;

		if (LOG.isDebugEnabled()) {
			register(new PrintSubscriber());
		}
		init();
	}

	public Future<?> submitCommand(Task command) {
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

		try {
			serialPort = SerialPortUtils.openSerialPort(portName, BaudRate.INITIAL.getBaudRate());
			eventListener = new EventListener(eventBus, serialPort.getInputStream());
			serialPort.addEventListener(eventListener);
			commandOutput.setSerialOutput(serialPort.getOutputStream());

			// Check power state in 9600.
			PowerStateSubscriber powerStateSubscriber = new PowerStateSubscriber();
			eventBus.register(powerStateSubscriber);
			new CheckPowerStateTask().execute(commandOutput, eventBus);

			if (powerStateSubscriber.gotResponse()) {
				LOG.debug("Got power state response: {} Starting radio...");
				// Submit initial powerup task
				new StartTask().execute(commandOutput, eventBus);
				LOG.debug("Switching to fast baudrate...");
				new SetFastBaudRate().execute(commandOutput, eventBus);
			} else {
				LOG.debug("No power state response.");
			}

			eventBus.unregister(powerStateSubscriber);

			closeSerialPort();
			serialPort = SerialPortUtils.openSerialPort(portName, BaudRate.FAST.getBaudRate());
			eventListener = new EventListener(eventBus, serialPort.getInputStream());
			serialPort.addEventListener(eventListener);
			commandOutput.setSerialOutput(serialPort.getOutputStream());

			// Submit powerup task and initiate fast transfer mode.
			LOG.debug("Starting radio in fast baudrate...");
			new StartTask().execute(commandOutput, eventBus);
			LOG.debug("Enabling fast transfer mode...");
			new FastTransferModeTask(true).execute(commandOutput, eventBus);

			LOG.debug("PCR1000 is ready to receive commands.");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Register subscriber on the event bus.
	 */
	public void register(Object subscriber) {
	    LOG.debug("Registering {} on eventBus.", subscriber.getClass().getName());
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
			submitCommand(new StopTask()).get();
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
