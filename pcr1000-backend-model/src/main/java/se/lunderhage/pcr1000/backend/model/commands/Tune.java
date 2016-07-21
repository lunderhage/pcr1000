package se.lunderhage.pcr1000.backend.model.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

public class Tune implements Command {

    private final RadioChannel channel;

    @JsonCreator
    public Tune(@JsonProperty("channel") RadioChannel channel) {
        super();
        this.channel = channel;
    }

    public RadioChannel getChannel() {
        return channel;
    }

}
