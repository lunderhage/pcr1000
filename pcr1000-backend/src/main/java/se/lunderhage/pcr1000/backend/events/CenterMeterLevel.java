package se.lunderhage.pcr1000.backend.events;

public class CenterMeterLevel implements Event {
	
	private final String code;
	
	
	public CenterMeterLevel(String code) {
		this.code = code;
	}

	
	@Override
	public String getCode() {
		return code;
	}
	
	public String toString() {
		return "CenterMeterLevel(" + code + ")";
	}

}
