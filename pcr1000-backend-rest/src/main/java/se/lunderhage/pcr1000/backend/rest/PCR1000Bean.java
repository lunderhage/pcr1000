package se.lunderhage.pcr1000.backend.rest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SourceChannel;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import se.lunderhage.pcr1000.backend.audio.AudioStreamer;
import se.lunderhage.pcr1000.backend.model.PCR1000;
import se.lunderhage.pcr1000.backend.model.commands.PowerState;
import se.lunderhage.pcr1000.backend.model.commands.Squelch;
import se.lunderhage.pcr1000.backend.model.commands.Volume;
import se.lunderhage.pcr1000.backend.model.types.Filter;
import se.lunderhage.pcr1000.backend.model.types.Mode;
import se.lunderhage.pcr1000.backend.model.types.RadioChannel;

@Component(service=PCR1000Bean.class, immediate = true, property={
        "service.exported.interfaces=*",
        "service.exported.configs=org.apache.cxf.rs",
        "org.apache.cxf.rs.address=http://0.0.0.0:8181/pcr1000",
        "org.apache.cxf.rs.provider=com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider"})

@Produces(MediaType.APPLICATION_JSON)
public class PCR1000Bean {

    private static final Logger LOG = LoggerFactory.getLogger(PCR1000Bean.class);

    @Reference
    private PCR1000 pcr1000;

    @Reference
    private AudioStreamer streamer;

    private Server server;

    public PCR1000Bean() {
        new JacksonJsonProvider(); // Workaround to have it included in the bundle.
    }

    @Activate
    public void activate() {

        /*
         * TODO: Only activate if there is
         * no web frontend registered.
         */

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(PCR1000Bean.class);
        sf.setResourceProvider(PCR1000Bean.class,
            new SingletonResourceProvider(this));
        sf.setAddress("http://0.0.0.0:8181/pcr1000");
        sf.setProvider(new JacksonJsonProvider());
        server = sf.create();
    }

    @Deactivate
    public void deactivate() {
        if (server != null) {
            server.stop();
            server.destroy();
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

    /**
     * Tune with the specified parameters and
     * listen to radio.
     * @param channel
     * @param squelch
     * @param volume
     * @return
     * @throws IOException
     */
    @Produces("audio/basic")
    @GET
    @Path("/listen")
    public Response listen(
            @QueryParam("frequency") String frequency,
            @QueryParam("mode") String mode,
            @QueryParam("filter") String filter,
            @QueryParam("volume") String volume,
            @QueryParam("squelch") String squelch
            ) throws IOException {

        try {
            if (frequency != null && mode != null && filter != null) {
                RadioChannel channel = new RadioChannel(Mode.valueOf(mode.toUpperCase()),
                        Filter.valueOf(filter.toUpperCase()),
                        Integer.parseInt(frequency));
                pcr1000.tune(channel);
            }

            if (volume != null) {
                Volume vol = new Volume(Integer.parseInt(volume));
                pcr1000.setVolume(vol);
            }

            if (squelch != null) {
                Squelch sqlch = new Squelch(Integer.parseInt(squelch));
                pcr1000.setSquelch(sqlch);
            }
        } catch (Exception e) {
            // TODO: Find out why the message is overwritten.
            throw new BadRequestException(e.getMessage(), e);
        }

        return startStream();
    }

    private Response startStream() throws IOException {
        final Pipe pipe = streamer.listen();

        StreamingOutput p = new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException {

                try {

                    SourceChannel srcChannel = pipe.source();
                    ByteBuffer buffer = ByteBuffer.allocate(4410);

                    while (!Thread.currentThread().isInterrupted()) {

                        int bytes = srcChannel.read(buffer);

                        if (bytes == -1) {
                            output.close();
                        }

                        if (bytes > 0) {
                            srcChannel.read(buffer);
                            output.write(buffer.array(), 0, buffer.position());
                            buffer.clear();
                        }
                    }

                } catch (IOException e) {
                    pipe.sink().close();
                    pipe.source().close();
                }

            }
        };
        LOG.debug("Listening to radio...");
        return Response.ok(p, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Type", "audio/x-raw")
                .build();
    }

}
