package se.lunderhage.pcr1000.backend.tasks;

import java.io.OutputStream;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * Command to set the volume on the PCR1000.
 */
public class Volume extends Command {
	
	private static final String VOLUME_CMD = "J40%s\n";
	
	private final int volume;

	public Volume(int volume) {
		Preconditions.checkArgument(volume >= 0 && volume <= 255, "Volume musst be between 0 and 255");
		this.volume = volume;
	}

	public int getVolume() {
		return volume;
	}
	
	@Override
	public byte[] encode() {
		return getCommand().getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + volume;
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
		Volume other = (Volume) obj;
		if (volume != other.volume)
			return false;
		return true;
	}

	@Override
	public void execute(OutputStream serialOutput, EventBus events) {
		// TODO Auto-generated method stub
		
	}
	
	public String getCommand() {
		return String.format(VOLUME_CMD, Integer.toHexString(volume).toUpperCase());
	}
	
}
