package se.lunderhage.pcr1000.backend.subscribers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import se.lunderhage.pcr1000.backend.events.RadioTurnedOffEvent;
import se.lunderhage.pcr1000.backend.events.RadioTurnedOnEvent;

/**
 * Subscribes for power state events.
 */
public class PowerStateSubscriber {
	
	private static final int EVENT_TIMEOUT = 4000;
	
	private static final Logger LOG = LoggerFactory.getLogger(PowerStateSubscriber.class);
	
	private Boolean turnedOn;
	private CountDownLatch latch = new CountDownLatch(1);

	@Subscribe
	public void powerOn(RadioTurnedOnEvent event) {
		LOG.debug("Got RadioTurnedOnEvent: {}", event);
		turnedOn = true;
		latch.countDown();
	}
	
	@Subscribe
	public void powerOff(RadioTurnedOffEvent event) {
		LOG.debug("Got RadioTurnedOffEvent: {}", event);
		turnedOn = false;
		latch.countDown();
	}
	
	/**
	 * Returns radio power state or null if no response.
	 * @return
	 */
	public Boolean isTurnedOn() {
		try {
			LOG.debug("Waiting for event...");
			// TODO: What is a good timeout on events that are
			// responses on commands?
			latch.await(EVENT_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return turnedOn;
	}
}
