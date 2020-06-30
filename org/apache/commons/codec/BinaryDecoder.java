package org.apache.commons.codec;

import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.DecoderException;

public interface BinaryDecoder extends Decoder {
   byte[] decode(byte[] var1) throws DecoderException;
}
