package se.lunderhage.pcr1000.backend.model.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Squelch implements Command {

    private final int level;

    @JsonCreator
    public Squelch(@JsonProperty("level") int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
