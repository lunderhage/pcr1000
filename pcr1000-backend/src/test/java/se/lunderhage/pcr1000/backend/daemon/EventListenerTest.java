package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.TooManyListenersException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import gnu.io.NRSerialPort;
import se.lunderhage.pcr1000.backend.subscribers.PrintSubscriber;

public class EventListenerTest {

	private static final Logger LOG = LoggerFactory.getLogger(EventListenerTest.class);

	private EventBus eventBus;
	private InputStream serialInput;

	/* TODO: Mock this for the tests. */
	private NRSerialPort serialPort;

	@Before
	public void setup() {
		LOG.debug("Setting up event bus...");
		eventBus = new EventBus();
		LOG.debug("Setting up serial port...");
		serialPort = SerialPortUtils.openSerialPort("/dev/ttyUSB0", BaudRate.INITIAL.getBaudRate());
		LOG.debug("Getting input stream from serial port...");
		serialInput = serialPort.getInputStream();
	}

	@Test
	public void test() throws InterruptedException, TooManyListenersException {
		LOG.debug("Creating & Starting event listener...");
		EventListener eventListener = new EventListener(eventBus, serialInput);
		serialPort.addEventListener(eventListener);

		PrintSubscriber subscriber = new PrintSubscriber();

		LOG.debug("Subscribing to event bus...");
		eventBus.register(subscriber);

		LOG.debug("Hit enter to stop.");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();

		eventBus.unregister(subscriber);
	}

	@After
	public void tearDown() throws IOException {
		LOG.debug("Closing serial port.");
		serialPort.disconnect();
	}

}
