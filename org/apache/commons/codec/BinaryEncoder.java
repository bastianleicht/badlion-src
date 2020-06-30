package org.apache.commons.codec;

import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.EncoderException;

public interface BinaryEncoder extends Encoder {
   byte[] encode(byte[] var1) throws EncoderException;
}
