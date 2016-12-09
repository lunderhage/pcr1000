package se.lunderhage.pcr1000.backend.audio.encoders;

import java.nio.ByteBuffer;


public interface AudioEncoder {

    /**
     * Encode input into output ByteBuffer
     * @param input
     * @param len
     * @param output
     */
    public void encode(byte[] input, int len, ByteBuffer output);

}
