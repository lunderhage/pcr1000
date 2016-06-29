package se.lunderhage.pcr1000.backend.daemon;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCR1000CommandQueueTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(PCR1000CommandQueueTest.class);

	private String serialPort = null;
	
	@Before
	public void setup() {
		List<String> ports = SerialPortUtils.getSerialPorts();
		if (ports.isEmpty()) {
			throw new IllegalStateException("No available serial ports!.");
		}
		LOG.debug("Available ports: {}", ports);
		serialPort = ports.get(1);
		serialPort = "/dev/ttyUSB0";
	}
	
	@Test
	public void test() throws InterruptedException {
		LOG.debug("Trying port: {}", serialPort);
		PCR1000CommandQueue pcr1000 = PCR1000CommandQueue.create(serialPort);
		
		Thread.sleep(10000);
		
		pcr1000.shutdown();

	}

}
