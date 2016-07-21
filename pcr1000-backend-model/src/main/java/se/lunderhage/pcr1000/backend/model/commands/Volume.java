package se.lunderhage.pcr1000.backend.model.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Volume implements Command {

    private final int level;

    @JsonCreator
    public Volume(@JsonProperty("level") int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
