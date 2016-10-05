package se.lunderhage.pcr1000.backend.model.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Squelch implements Command {

    private final int level;

    @JsonCreator
    public Squelch(@JsonProperty("level") int level) {
        Preconditions.checkArgument(level >= 0 && level < 256, "Invalid squelch (must be 0-255)");
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
