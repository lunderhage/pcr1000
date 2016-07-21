package se.lunderhage.pcr1000.backend.subscribers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import se.lunderhage.pcr1000.backend.events.Event;

public class PrintSubscriber {
	
	private static final Logger LOG = LoggerFactory.getLogger(PrintSubscriber.class);

	@Subscribe
	public void handleEvent(Event event) {
		LOG.info("Got event: {}", event);
	}
}
