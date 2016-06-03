package se.lunderhage.pcr1000.backend.commands;

import com.google.common.base.Preconditions;

/**
 * Command to set the volume on the PCR1000.
 */
public class Volume implements Command {
	
	private static final String VOLUME_CMD = "J40%s";
	
	private final int volume;

	public Volume(int volume) {
		Preconditions.checkArgument(volume >= 50000 && volume <= 1300000000, "Frequency must be between 50000 and 1300000000 Hz");
		this.volume = volume;
	}

	public int getVolume() {
		return volume;
	}
	
	@Override
	public String encode() {
		return String.format(VOLUME_CMD, Integer.toHexString(volume).toUpperCase());
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
	
	
}
