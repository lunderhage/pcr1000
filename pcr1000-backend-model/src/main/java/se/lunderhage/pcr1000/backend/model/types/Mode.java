package se.lunderhage.pcr1000.backend.model.types;

/**
 * The different modes supported by the PCR1000.
 */
public enum Mode {
	LSB("00"), USB("01"), AM("02"), CW("03"), FM("05"), WFM("06");
	
	private final String value;
	
	private Mode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
