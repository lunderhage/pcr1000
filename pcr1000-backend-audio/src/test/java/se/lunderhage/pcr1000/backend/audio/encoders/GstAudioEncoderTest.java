package se.lunderhage.pcr1000.backend.audio.encoders;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class GstAudioEncoderTest {

    private static final Logger LOG = LoggerFactory.getLogger(GstAudioEncoderTest.class);

    private static final String OUTPUT_FILE = "/tmp/encoded.ogg";

    @Before
    public void setup() {
        File file = new File(OUTPUT_FILE);
        if (file.exists()) {
            file.delete();
        }

        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("org.gstreamer");
        rootLogger.setLevel(Level.DEBUG);
    }

    @Test
    public void test() throws IOException {

        FileChannel input = FileChannel.open(
                Paths.get("/home/qanslue/Documents/java-lame-tests/sound.au"),
                StandardOpenOption.READ);

        FileChannel output = FileChannel.open(Paths.get(OUTPUT_FILE), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        ByteBuffer inputBuffer = ByteBuffer.allocate(10240);
        ByteBuffer outputBuffer = ByteBuffer.allocate(10240);

        AudioEncoder encoder = new GstAudioEncoder();

        while (input.read(inputBuffer) > -1) {
            LOG.debug("Read " + inputBuffer.position() + " bytes.");
            encoder.encode(inputBuffer.array(), inputBuffer.position(), outputBuffer);
            output.write(outputBuffer);
            LOG.debug("Wrote " + outputBuffer.position() + " bytes.");
            inputBuffer.rewind();
            outputBuffer.rewind();
        }

        input.close();
        output.force(true);
        output.close();

        LOG.debug("Done.");

    }

    @Test
    public void connect() {
        Gst.init();
        Pipeline pipe = new Pipeline("SimplePipeline");
        //Element src = ElementFactory.make("fakesrc", "Source");
//        Element src = ElementFactory.make("pulsesrc", "Source");
//        src.set("device", "alsa_output.pci-0000_00_1b.0.analog-stereo.monitor");

        // gst-launch-0.10 filesrc location=sound.au ! queue ! audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321 ! audioconvert ! lame ! filesink location=/tmp/sound.mp3


        Element src = ElementFactory.make("filesrc", "Source");
        src.set("location", "/home/qanslue/Documents/java-lame-tests/sound.au");
//        src.setCaps(Caps.fromString("audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321"));
        Element queue = ElementFactory.make("queue", "queue");
        Caps caps = Caps.fromString("audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321");
        queue.setCaps(caps);

        Element convert = ElementFactory.make("audioconvert", "convert");
        Element lame = ElementFactory.make("lame", "lame");
        lame.set("name", "enc");
        lame.set("mode", "0");
        lame.set("vbr-quality", "6");

//        AppSink appsink = (AppSink) ElementFactory.make("appsink", "stream");
//        Buffer buf = appsink.pullBuffer();

        Element file = ElementFactory.make("filesink", "buffer");
        file.set("location", "/tmp/buffer.mp3");

//        Element shout = ElementFactory.make("shout2send", "icecast");
//        shout.set("mount", "/pandora.mp3");
//        shout.set("port", "8000");
//        shout.set("password", "something");
//        shout.set("ip", "192.168.1.201");

        pipe.addMany(src, queue, convert, lame, file);
        src.link(queue, convert, lame, file);

        pipe.setState(State.PLAYING);
        Gst.main();
        pipe.setState(State.NULL);
    }

    @Test
    public void connect2() {
        Gst.init();
        Element source = ElementFactory.make("filesrc", "filesrc0");
        source.set("location", "/home/qanslue/Documents/java-lame-tests/sound.au");
//        Bin bin = Bin.launch("filesrc location=/home/qanslue/Documents/java-lame-tests/sound.au ! queue ! audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321 ! audioconvert ! lame ! filesink location=/tmp/sound.mp3", true);
        Bin bin = Bin.launch("queue ! audio/x-raw-int,rate=44100,channels=1,width=16,depth=16,signed=true,endianness=4321 ! audioconvert ! lame ! filesink location=/tmp/sound.mp3", false);

        Pipeline pipe = new Pipeline();

        source.link(bin);
        pipe.addMany(source, bin);

        pipe.play();
        Gst.main();
        pipe.stop();
    }

}
