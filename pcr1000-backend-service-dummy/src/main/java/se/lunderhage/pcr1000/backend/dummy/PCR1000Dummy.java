package se.lunderhage.pcr1000.backend.dummy;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lunderhage.pcr1000.backend.model.PCR1000;
import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;


/**
 * Mocked PCR1000 for testing purposes.
 */
@Component(immediate = false, service = PCR1000.class, property = { "service.ranking:Integer=1" })
public class PCR1000Dummy implements PCR1000 {

    private static final Logger LOG = LoggerFactory.getLogger(PCR1000Dummy.class);

    private boolean turnedOn = false;

    @Activate
    @Override
    public void start() {
        LOG.debug("Starting dummy service.");
        turnedOn = true;
    }

    @Deactivate
    @Override
    public void stop() {
        LOG.debug("Stopping dummy service.");
        turnedOn = false;
    }

    @Override
    public void register(Object subscriber) {
        LOG.debug("Registering subscriber.");
    }

    @Override
    public boolean setVolume(Volume volume) {
        LOG.debug("Set volume.");
        return true;
    }

    @Override
    public boolean setSquelch(Squelch squelch) {
        LOG.debug("Set squelch.");
        return true;
    }

    @Override
    public boolean tune(RadioChannel channel) {
        LOG.debug("Tune channel: {}", channel);
        return true;
    }

    @Override
    public PowerState getPowerState() {
        LOG.debug("Get power state.");
        return new PowerState(turnedOn);
    }

}
