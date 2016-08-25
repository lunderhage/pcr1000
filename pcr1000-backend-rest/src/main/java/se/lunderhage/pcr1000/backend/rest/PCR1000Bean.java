package se.lunderhage.pcr1000.backend.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lunderhage.pcr1000.backend.model.PCR1000;
import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

@Path("/pcr1000")
@Produces(MediaType.APPLICATION_JSON)
public class PCR1000Bean {

    private static final Logger LOG = LoggerFactory.getLogger(PCR1000Bean.class);

    private PCR1000 pcr1000;

    public PCR1000Bean() {
        init();
    }

    private void init() {
        BundleContext context = FrameworkUtil.getBundle(PCR1000.class).getBundleContext();
        ServiceReference<?> ref = context.getServiceReference(PCR1000.class.getName());
        if (ref == null) {
            throw new IllegalStateException("Could not find PCR1000 Backend Service.");
        }
        pcr1000 = (PCR1000) context.getService(ref);
    }

    @POST
    @Path("/volume")
    public boolean setVolume(Volume volume) {
        LOG.debug("Set volume to {}", volume);
        return pcr1000.setVolume(volume);
    }

    @POST
    @Path("/squelch")
    public boolean setSquelch(Squelch squelch) {
        LOG.debug("Set squelch to {}", squelch);
        return pcr1000.setSquelch(squelch);
    }

    @POST
    @Path("/tune")
    public boolean tune(RadioChannel channel) {
        LOG.debug("Tune channel to {} ({})", channel);
        return pcr1000.tune(channel);
    }

    @GET
    @Path("/powerState")
    public PowerState getPowerState() {
        LOG.debug("Get power state.");
        return pcr1000.getPowerState();
    }


}
