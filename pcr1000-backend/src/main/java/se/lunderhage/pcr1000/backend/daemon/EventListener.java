package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import se.lunderhage.pcr1000.backend.events.Event;
import se.lunderhage.pcr1000.backend.events.EventDecoder;

/**
 * Listens for events from the PCR1000 on the serial port and posts
 * them to the assigned EventBus.
 */
public class EventListener implements SerialPortEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);
	private static final int SHORT_RESPONSE_LENGTH = 4;
	private static final int LONG_RESPONSE_LENGTH = 32;

	private final EventBus eventBus;
	private InputStream serialInput;
	ByteBuffer buffer = ByteBuffer.allocate(LONG_RESPONSE_LENGTH);

	public EventListener(EventBus eventBus, InputStream serialInput) {
		Preconditions.checkNotNull(eventBus, "eventBus cannot be null.");
		Preconditions.checkNotNull(serialInput, "serialInput cannot be null.");
		this.serialInput = serialInput;
		this.eventBus = eventBus;
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent ev) {
		if (ev.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				while (serialInput.available() > 0) {
					byte b = (byte) serialInput.read();
					if (b == '\r' || b == '\n' || b == -1) {
						continue;
					}

					switch (b) {
					case 'G':
					case 'H':
					case 'I':
						buffer.rewind();
						buffer.limit(SHORT_RESPONSE_LENGTH);
						buffer.put(b);
						break;
					case 'N':
						buffer.rewind();
						buffer.limit(LONG_RESPONSE_LENGTH);
						buffer.put(b);
						break;
					default:
						buffer.put(b);
					}
					
					if (!buffer.hasRemaining()) {
						postEvent(buffer);
						clearBuffer();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void clearBuffer() {
		buffer.clear();
		while (buffer.hasRemaining()) {
			buffer.put((byte) 0);
		}
		buffer.clear();
	}
	
	private void postEvent(ByteBuffer buffer) {
		String response = new String(buffer.array()).trim();
		if (response.isEmpty()) {
			return;
		}
		Event event = EventDecoder.decode(response);

		if (response != null) {
			eventBus.post(event);
		}
	}
}
