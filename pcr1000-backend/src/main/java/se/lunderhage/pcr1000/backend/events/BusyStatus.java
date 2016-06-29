package se.lunderhage.pcr1000.backend.events;

public class BusyStatus implements Event {

	private final String code;
	
	public BusyStatus(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}
	
	public String toString() {
		return "BusyEvent(" + code + ")";
	}

}
