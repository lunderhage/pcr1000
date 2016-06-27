package se.lunderhage.pcr1000.backend.tasks;

import java.io.OutputStream;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * Command to set the squelch on the PCR1000.
 */
public class Squelch extends Command {
	
	private static final String SQUELCH_CMD = "J41%s\n";
	
	private final int squelch;

	public Squelch(int squelch) {
		Preconditions.checkArgument(squelch >= 0 && squelch <= 255, "Squelch must be between 0-255");
		this.squelch = squelch;
	}

	public int getSquelch() {
		return squelch;
	}
	
	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + squelch;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Squelch other = (Squelch) obj;
		if (squelch != other.squelch)
			return false;
		return true;
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		// TODO Auto-generated method stub
		
	}
	
	public String getCommand() {
		return String.format(SQUELCH_CMD, Integer.toHexString(squelch).toUpperCase());
	}
	
}
