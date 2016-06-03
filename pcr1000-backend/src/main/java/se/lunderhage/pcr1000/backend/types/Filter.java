package se.lunderhage.pcr1000.backend.types;

/**
 * The different filters supported by the PCR1000.
 */
public enum Filter {
	_2K8("00"), _6K("01"), _15K("02"), _50K("03"), _230K("04");
	
	private final String value;
	
	private Filter(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
