package se.lunderhage.pcr1000.backend.events;

public class BandScopeEvent implements Event {

	private final String code;
	
	public BandScopeEvent(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}
	
	public String toString() {
		return "BandScopeEvent(" + code + ")";
	}

}
