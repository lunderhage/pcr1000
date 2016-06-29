package se.lunderhage.pcr1000.backend.events;

public class DTMFEvent implements Event {

	private final String code;
	
	
	public DTMFEvent(String code) {
		this.code = code;
	}


	@Override
	public String getCode() {
		return code;
	}
	
	public String toString() {
		return "DTMFEvent(" + code + ")";
	}

}
