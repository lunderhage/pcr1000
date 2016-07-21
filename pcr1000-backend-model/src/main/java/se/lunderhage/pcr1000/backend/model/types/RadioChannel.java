package se.lunderhage.pcr1000.backend.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A RadioChannel consists of all information needed to tune in a channel
 * on the PCR1000.
 */
public class RadioChannel {

	private final Mode mode;
	private final Filter filter;
	private final Frequency frequency;

	@JsonCreator
	public RadioChannel(
	        @JsonProperty("mode") Mode mode,
	        @JsonProperty("filter") Filter filter,
	        @JsonProperty("frequency") Frequency frequency) {
		// TODO: There are some combinations of mode and filter that do not work.
		this.mode = mode;
		this.filter = filter;
		this.frequency = frequency;
	}

	public RadioChannel(Mode mode, Filter filter, int frequency) {
		this(mode, filter, new Frequency(frequency));
	}

	public Mode getMode() {
		return mode;
	}
	public Filter getFilter() {
		return filter;
	}
	public Frequency getFrequency() {
		return frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
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
		RadioChannel other = (RadioChannel) obj;
		if (filter != other.filter)
			return false;
		if (frequency == null) {
			if (other.frequency != null)
				return false;
		} else if (!frequency.equals(other.frequency))
			return false;
		if (mode != other.mode)
			return false;
		return true;
	}

}
