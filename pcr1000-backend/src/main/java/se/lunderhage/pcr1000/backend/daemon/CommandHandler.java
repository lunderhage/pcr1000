package se.lunderhage.pcr1000.backend.daemon;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.subscribers.CommandResultSubscriber;

/**
 * A wrapped OutputStream for convinient command sending.
 * TODO: Listen for CommandSuccessfulEvent/CommandFailedEvent.
 */
public class CommandHandler {

	private OutputStream serialOutput;
	private final EventBus eventBus;
	private final CommandResultSubscriber resultSubscriber = new CommandResultSubscriber();
	
	public CommandHandler(OutputStream serialOutput, EventBus eventBus) {
		this.serialOutput = serialOutput;
		this.eventBus = eventBus;
		
		if (this.eventBus != null) {
			eventBus.register(resultSubscriber);
		}
	}
	
	public void execCommand(String command) throws IOException {
		Preconditions.checkNotNull(serialOutput, "no OutputStream to write commands to.");
		
		serialOutput.write(command.getBytes());
		serialOutput.write(new String("\r\n").getBytes());
		serialOutput.flush();

	}
	
	public boolean lastCommandSuccessful() {
		return resultSubscriber.isSuccessful();
	}
	
	public void setSerialOutput(OutputStream serialOutput) {
		Preconditions.checkNotNull(serialOutput);
		this.serialOutput = serialOutput;
	}

	public void close() throws IOException {
		serialOutput.close();
	}
}
