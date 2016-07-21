package se.lunderhage.pcr1000.backend.model;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

@Path("/pcr1000")
@Produces(MediaType.APPLICATION_JSON)
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void setVolume(Volume volume);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void setSquelch(Squelch squelch);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void tune(RadioChannel channel);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PowerState getPowerState();

}
