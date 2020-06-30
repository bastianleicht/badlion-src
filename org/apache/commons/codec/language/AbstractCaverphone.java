package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public abstract class AbstractCaverphone implements StringEncoder {
   public Object encode(Object source) throws EncoderException {
      if(!(source instanceof String)) {
         throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
      } else {
         return this.encode((String)source);
      }
   }

   public boolean isEncodeEqual(String str1, String str2) throws EncoderException {
      return this.encode(str1).equals(this.encode(str2));
   }
}
