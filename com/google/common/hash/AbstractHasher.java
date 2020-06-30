package com.google.common.hash;

import com.google.common.hash.Hasher;
import java.nio.charset.Charset;

abstract class AbstractHasher implements Hasher {
   public final Hasher putBoolean(boolean b) {
      return this.putByte((byte)(b?1:0));
   }

   public final Hasher putDouble(double d) {
      return this.putLong(Double.doubleToRawLongBits(d));
   }

   public final Hasher putFloat(float f) {
      return this.putInt(Float.floatToRawIntBits(f));
   }

   public Hasher putUnencodedChars(CharSequence charSequence) {
      int i = 0;

      for(int len = charSequence.length(); i < len; ++i) {
         this.putChar(charSequence.charAt(i));
      }

      return this;
   }

   public Hasher putString(CharSequence charSequence, Charset charset) {
      return this.putBytes(charSequence.toString().getBytes(charset));
   }
}
