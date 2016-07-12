package se.lunderhage.pcr1000.backend.events;

/**
 * Event for command failure.
 */
public class CommandFailedEvent implements CommandResponseEvent {

    public static final String COMMAND_FAILED = "G001";

    @Override
    public String getCode() {
        return COMMAND_FAILED;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
