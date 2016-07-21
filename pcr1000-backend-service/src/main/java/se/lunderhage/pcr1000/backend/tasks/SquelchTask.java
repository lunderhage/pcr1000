package se.lunderhage.pcr1000.backend.tasks;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import se.lunderhage.pcr1000.backend.daemon.CommandHandler;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;

/**
 * Command to set the squelch on the PCR1000.
 */
public class SquelchTask extends Task {

	private static final String SQUELCH_CMD = "J41%s\n";

	private final Squelch squelch;

	public SquelchTask(Squelch squelch) {
		Preconditions.checkArgument(squelch.getLevel() >= 0 && squelch.getLevel() <= 255, "Squelch must be between 0-255");
		this.squelch = squelch;
	}

	public Squelch getSquelch() {
		return squelch;
	}

	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public void execute(CommandHandler commandOutput, EventBus events) {
		try {
			commandOutput.execCommand(getCommand());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
    public String getCommand() {
		return String.format(SQUELCH_CMD, Integer.toHexString(squelch.getLevel()).toUpperCase());
	}

}
