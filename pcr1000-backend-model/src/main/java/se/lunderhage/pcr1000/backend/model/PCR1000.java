package se.lunderhage.pcr1000.backend.model;

import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

public interface PCR1000 {

    /**
     * Turn on and init the PCR1000.
     */
    public boolean start();
    /**
     * Turn off PCR1000.
     */
    public boolean stop();

    /**
     * Register subscriber on the event bus.
     */
    public void register(Object subscriber);

    /**
     * Set volume level.
     * @param volume
     * @return
     */
    public boolean setVolume(Volume volume);

    /**
     * Set squelch level.
     * @param squelch
     * @return
     */
    public boolean setSquelch(Squelch squelch);

    /**
     * Tune radio channel.
     * @param channel
     * @return
     */
    public boolean tune(RadioChannel channel);

    /**
     * Get current power state.
     * @return
     */
    public PowerState getPowerState();

}
