package se.lunderhage.pcr1000.backend.subscribers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import se.lunderhage.pcr1000.backend.events.CommandResponseEvent;

/**
 * Subscribe and wait for a command result.
 */
public class CommandResultSubscriber {

	private static final Logger LOG = LoggerFactory.getLogger(CommandResultSubscriber.class);

	private CommandResponseEvent event;
	private CountDownLatch latch = new CountDownLatch(1);

	@Subscribe
	public void handle(CommandResponseEvent event) {
		LOG.debug("Got event: {}", event);
		this.event = event;
		latch.countDown();
	}

	public boolean isSuccessful() {
		try {
			LOG.debug("Waiting for event...");
			latch.await(5000, TimeUnit.MILLISECONDS);
			latch = new CountDownLatch(1);
			if (event != null) {
				LOG.debug("Got event: {}", event);
				return event.isSuccessful();
			} else {
				LOG.debug("Timed out waiting for event.");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
