package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.events.Event;
import se.lunderhage.pcr1000.backend.events.EventDecoder;

public class EventListener implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);
	
	private final EventBus eventBus;
	private final InputStream serialInput;
	
	private boolean running = true;

	public EventListener(EventBus eventBus, InputStream serialInput) {
		Preconditions.checkNotNull(eventBus, "eventBus cannot be null.");
		Preconditions.checkNotNull(serialInput, "serialInput cannot be null.");
		this.eventBus = eventBus;
		this.serialInput = serialInput;
	}

	@Override
	public void run() {
		LOG.debug("EventListener is starting...");
		while (running && !Thread.currentThread().isInterrupted()) {
			String response = getResponse(serialInput);
			Event event = EventDecoder.decode(response);
			
			if (response != null) {
				eventBus.post(event);
			}
		}
	}
	
	public String getResponse(InputStream input) {
		
		StringBuilder builder = new StringBuilder();
		while (running && !Thread.currentThread().isInterrupted())
			try {
				if (input.available() > 0) {
					byte b = (byte) input.read();
					LOG.debug("Got byte: {} (0x{})", (char) b, Integer.toHexString(b));
					if (b == '\n' || b == -1) {
						break;
					}
					builder.append((char) b);
				}
			} catch (IOException e) {
				LOG.error("Error reading input: {}", e);
			}
		
		LOG.debug("Returning: {}", builder.toString());
		return builder.toString();
	}

	public void stop() {
		running = false;
	}
}
