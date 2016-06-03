package se.lunderhage.pcr1000.backend.types;

/**
 * The PCR1000 supports frequencies ranging from 50kHz to 1300MHz.
 * Frequencies are encoded in Hz.
 */
public class Frequency {
	private final int frequency;
	
	/**
	 * Create Frequency (in Hz).
	 * @param frequency
	 */
	Frequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Get frequency (in Hz).
	 * @return
	 */
	public int getFrequency() {
		return frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frequency;
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
		Frequency other = (Frequency) obj;
		if (frequency != other.frequency)
			return false;
		return true;
	}
	
}
