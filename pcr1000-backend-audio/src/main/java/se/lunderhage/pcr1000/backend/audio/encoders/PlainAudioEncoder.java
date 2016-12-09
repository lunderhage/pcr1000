package se.lunderhage.pcr1000.backend.audio.encoders;

import java.nio.ByteBuffer;

/**
 * Just copies the input to the output ByteBuffer.
 */
public class PlainAudioEncoder implements AudioEncoder {

    @Override
    public void encode(byte[] input, int len, ByteBuffer output) {
        output.rewind().limit(output.capacity());
        output.put(input, 0, len).limit(output.position());
        output.rewind();
    }

}
