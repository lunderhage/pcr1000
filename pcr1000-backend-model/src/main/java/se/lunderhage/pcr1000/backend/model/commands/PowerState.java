package se.lunderhage.pcr1000.backend.model.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerState implements Command {

    private final boolean turnedOn;

    @JsonCreator
    public PowerState(@JsonProperty("level") boolean isTurnedOn) {
        this.turnedOn = isTurnedOn;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

}
