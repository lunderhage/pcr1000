package se.lunderhage.pcr1000.backend.subscribers;

import com.google.common.eventbus.Subscribe;

import se.lunderhage.pcr1000.backend.events.Event;

public class PrintSubscriber {

	@Subscribe
	public void handleEvent(Event event) {
		System.out.println("Got event: " + event.getCode());
	}
}
