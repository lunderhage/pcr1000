package se.lunderhage.pcr1000.backend.audio.encoders;

import java.nio.ByteBuffer;

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSrc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses Gstreamer (native) to encode input into output ByteBuffer
 */
public class GstAudioEncoder implements AudioEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(GstAudioEncoder.class);

    private Pipeline pipe;
    private AppSrc source;
    private AppSink sink;

    public GstAudioEncoder() {
        Gst.init();

        pipe = new Pipeline();
        pipe.setAutoFlushBus(true);

        /* gst-launch-1.0
         * (alsasrc device="hw:1,0" !
         * "audio/x-raw, format=S16LE,rate=44100,channels=1" !)
         * audioconvert !
         * vorbisenc quality=0.9 !
         * oggmux !
         * (tcpserversink host=0.0.0.0 port=8000 blocksize=1300)
         */

        /*
         * Raw samples to MP3.
         * gst-launch-0.10 filesrc location=sound.au !
         * queue !
         * audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321 !
         * audioconvert !
         * lame !
         * filesink location=/tmp/sound.mp3
         */

        source = (AppSrc) ElementFactory.make("appsrc", "app-source");
        Caps caps = new Caps("audio/x-raw, format=S16BE,rate=44100,channels=1");
        source.setCaps(caps);



        Element audioConvert = ElementFactory.make("audioconvert", "AudoConvert");
        Element lame = ElementFactory.make("lamemp3enc", "lame");

//        Element vorbisEnc = ElementFactory.make("vorbisenc", "VorbisEnc");
//        vorbisEnc.set("quality", "0.9");
//        Element oggMux = ElementFactory.make("oggmux", "OggMux");

        sink = (AppSink) ElementFactory.make("appsink", "app-sink");

        sink.connect(new AppSink.NEW_BUFFER() {

            @Override
            public void newBuffer(AppSink elem) {
                LOG.debug("Sink: New buffer!");
            }
        });

        source.link(audioConvert, lame, sink);
        pipe.addMany(source, audioConvert, lame, sink);

        LOG.debug("Pipeline: " + pipe);

        pipe.play();

        new Thread(new Runnable() {

            @Override
            public void run() {
                Gst.main();
            }

        }).start();
    }

    @Override
    public void encode(byte[] input, int len, ByteBuffer output) {
        output.rewind();
        output.limit(output.capacity());

        /*
         * Push the buffer into the source end
         * of the pipeline for conversion.
         */
        Buffer buf = new Buffer(len);
        buf.getByteBuffer().get(input);
        source.pushBuffer(buf);

        /*
         * Pull the encoded buffer from the
         * sink end of the pipeline.
         */
        LOG.debug("Pulling encoded buffer...");
        sink.ready();
        Buffer encoded = sink.pullBuffer();
        LOG.debug("Pulled.");
        if (encoded != null) {
            output.put(encoded.getByteBuffer());
            output.limit(output.position());
            output.rewind();
        }
    }
}
