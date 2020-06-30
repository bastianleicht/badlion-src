package org.apache.commons.codec;

import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.DecoderException;

public interface StringDecoder extends Decoder {
   String decode(String var1) throws DecoderException;
}
