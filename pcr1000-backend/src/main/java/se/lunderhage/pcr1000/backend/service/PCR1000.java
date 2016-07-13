package se.lunderhage.pcr1000.backend.service;

import java.util.concurrent.Future;

import se.lunderhage.pcr1000.backend.tasks.Command;

public interface PCR1000 {

    /**
     * Turn on and init the PCR1000.
     */
    public void start();
    /**
     * Turn off PCR1000.
     */
    public void stop();

    /**
     * Register subscriber on the event bus.
     */
    public void register(Object subscriber);

    /**
     * Submit a command to the PCR1000.
     * @param command
     * @return
     */
    public Future<?> submitCommand(Command command);

}
