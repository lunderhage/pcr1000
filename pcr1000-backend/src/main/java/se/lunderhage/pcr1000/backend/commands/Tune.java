package se.lunderhage.pcr1000.backend.commands;

import se.lunderhage.pcr1000.backend.types.RadioChannel;

/**
 * Command to tune a RadioChannel on the PCR1000.
 */
public class Tune implements Command  {
	
	private static final String TUNE_CMD = "K0%010d%s%s00";
	
	// return "K0" + str(self.frequency).zfill(10) + self.mode + self.filter + "00"
	
	private final RadioChannel channel;

	public Tune(RadioChannel channel) {
		this.channel = channel;
	}

	public RadioChannel getChannel() {
		return channel;
	}
	
	@Override
	public String encode() {
		return String.format(
				TUNE_CMD,
				channel.getFrequency().getFrequency(),
				channel.getMode().getValue(),
				channel.getFilter().getValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
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
		Tune other = (Tune) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		return true;
	}
	
}
