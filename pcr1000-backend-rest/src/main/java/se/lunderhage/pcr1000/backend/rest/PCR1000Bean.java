package se.lunderhage.pcr1000.backend.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import se.lunderhage.pcr1000.backend.model.PCR1000;
import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

@Component(service=PCR1000Bean.class, immediate = true, property={
        "service.exported.interfaces=*",
        "service.exported.configs=org.apache.cxf.rs",
        "org.apache.cxf.rs.address=http://0.0.0.0:8181/pcr1000",
        "org.apache.cxf.rs.provider=com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider"})

@Produces(MediaType.APPLICATION_JSON)
@Path("/pcr1000")
public class PCR1000Bean {

    private static final Logger LOG = LoggerFactory.getLogger(PCR1000Bean.class);

    private static String BASE_URI = "http://0.0.0.0:8181/";

    @Reference
    private PCR1000 pcr1000;

    private HttpServer server;

    public PCR1000Bean() {
        new JacksonJsonProvider(); // Workaround to have it included in the bundle.
    }

    @Activate
    public void startEmbeddedServer() throws IOException {

        /*
         * TODO: Only activate if there is
         * no web frontend registered.
         */

        final ResourceConfig rc = new ResourceConfig();
        rc.register(this);
        rc.registerInstances(new JacksonJsonProvider());
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        server.start();
    }

    @Deactivate
    public void stopEmbeddedServer() {
        if (server != null) {
            server.shutdownNow();
        }
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
