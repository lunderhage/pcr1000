package se.lunderhage.pcr1000.backend.audio.encoders;

import java.nio.ByteBuffer;

public class DaisyPipelineEncoder implements AudioEncoder {

    @Override
    public void encode(byte[] input, int len, ByteBuffer output) {
        // TODO Auto-generated method stub

        // Useless since the LameEncoder class is hardcoded to write files.

    }

}
