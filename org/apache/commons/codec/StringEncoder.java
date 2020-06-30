package org.apache.commons.codec;

import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.EncoderException;

public interface StringEncoder extends Encoder {
   String encode(String var1) throws EncoderException;
}
