package se.lunderhage.pcr1000.backend.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = false, service = AudioStreamer.class)
public class AudioStreamer {

    private static final Logger LOG = LoggerFactory.getLogger(AudioStreamer.class);

    private Streamer streamer;

    @Activate
    public void start() {

        if (streamer != null && streamer.isRunning()) {
            return;
        }

        if (streamer == null) {
            streamer = new Streamer();
        }

        /*
         * TODO: Put streamer in some
         * executor that keeps it alive etc.
         */

        Thread t = new Thread(streamer);
        t.setDaemon(true);
        t.start();
    }

    @Deactivate
    public void shutdown() {
        streamer.stop();
    }

    public Pipe listen() throws IOException {
        return streamer.addSubscriber();
    }

    private static class Streamer implements Runnable {

        private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100f, 16, 1, true, true);

        private boolean running = false;
        private List<Pipe> subscribers = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void run() {
            running = true;
            LOG.debug("Starting streamer ({})", AUDIO_FORMAT);
            DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
            if (!AudioSystem.isLineSupported(micInfo)) {
                throw new IllegalStateException("Line is not supported.");
            }

            try (TargetDataLine audioInput = findLine(micInfo)) {
                audioInput.open(AUDIO_FORMAT);
                audioInput.start();
                byte[] audioBytes = new byte[audioInput.getBufferSize() / 10];
                int numBytes = 0;
                ByteBuffer streamBuffer = ByteBuffer.allocate(audioInput.getBufferSize() / 10);


                LOG.debug("Streamer is started. ({})", audioInput);
                while(running && !Thread.currentThread().isInterrupted()) {

                    numBytes = audioInput.read(audioBytes, 0, audioBytes.length);

                    /*
                     * TODO: Encode stream to MP3/WMA/OGG Vorbis etc.
                     */
                    streamBuffer.rewind().limit(streamBuffer.capacity());
                    streamBuffer.put(audioBytes, 0, numBytes).limit(streamBuffer.position());

                    for (Pipe subscriber : new ArrayList<>(subscribers)) {
                        streamBuffer.rewind();
                        try {
                            if (subscriber.sink().isOpen()) {
                                subscriber.sink().write(streamBuffer);
                            } else {
                                subscribers.remove(subscriber);
                            }
                        } catch (IOException e) {
                            LOG.debug("Unable to write to stream. Unsubscribing {}...", subscriber, e);
                            subscribers.remove(subscriber);
                        }
                    }

                }

            } catch (LineUnavailableException e) {
                throw new IllegalStateException("Unable to open audio input.", e);
            }

        }

        /**
         * Returns the first usable line.
         * @param lineInfo
         * @return
         * @throws IllegalArgumentException If no usable line was found.
         */
        private TargetDataLine findLine(DataLine.Info lineInfo) throws IllegalArgumentException {
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();

            TargetDataLine line = null;
            Exception exception = null;

            for (Mixer.Info mixerInfo : mixers) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                try {
                    line = (TargetDataLine) mixer.getLine(lineInfo);
                } catch (IllegalArgumentException | LineUnavailableException e) {
                    exception = e;
                }
            }

            if (line == null) {
                throw new IllegalArgumentException("Unable to find usable line.", exception);
            }

            return line;
        }

        public Pipe addSubscriber() throws IOException {
            Pipe subscriber = Pipe.open();
            subscriber.sink().configureBlocking(false);

            /*
             * TODO: We probably need to add some audio header here.
             */
            subscriber.sink().write(ByteBuffer.wrap(SND_HEADER).order(ByteOrder.BIG_ENDIAN));
            subscribers.add(subscriber);
            LOG.debug("Added subscriber.");
            return subscriber;
        }

        private static final byte[] SND_HEADER = new byte[] {
                0x2e, 0x73, 0x6e, 0x64, // Magic number
                0x00, 0x00, 0x00, 0x18, // Data offset
                Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE,  // Data size (unknown)
                0x00, 0x00, 0x00, 0x03, // 16-bit PCM
                0x00, 0x00, (byte) 0xac, 0x44, // 44100 hz
                0x00, 0x00, 0x00, 0x01 // 1 Channel
        };

        public boolean isRunning() {
            return running;
        }

        public void stop() {
            running = false;
        }

    }

}
