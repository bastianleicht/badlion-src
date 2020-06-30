package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.nio.charset.Charset;

abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction {
   final HashFunction[] functions;
   private static final long serialVersionUID = 0L;

   AbstractCompositeHashFunction(HashFunction... functions) {
      for(HashFunction function : functions) {
         Preconditions.checkNotNull(function);
      }

      this.functions = functions;
   }

   abstract HashCode makeHash(Hasher[] var1);

   public Hasher newHasher() {
      final Hasher[] hashers = new Hasher[this.functions.length];

      for(int i = 0; i < hashers.length; ++i) {
         hashers[i] = this.functions[i].newHasher();
      }

      return new Hasher() {
         public Hasher putByte(byte b) {
            for(Hasher hasher : hashers) {
               hasher.putByte(b);
            }

            return this;
         }

         public Hasher putBytes(byte[] bytes) {
            for(Hasher hasher : hashers) {
               hasher.putBytes(bytes);
            }

            return this;
         }

         public Hasher putBytes(byte[] bytes, int off, int len) {
            for(Hasher hasher : hashers) {
               hasher.putBytes(bytes, off, len);
            }

            return this;
         }

         public Hasher putShort(short s) {
            for(Hasher hasher : hashers) {
               hasher.putShort(s);
            }

            return this;
         }

         public Hasher putInt(int i) {
            for(Hasher hasher : hashers) {
               hasher.putInt(i);
            }

            return this;
         }

         public Hasher putLong(long l) {
            for(Hasher hasher : hashers) {
               hasher.putLong(l);
            }

            return this;
         }

         public Hasher putFloat(float f) {
            for(Hasher hasher : hashers) {
               hasher.putFloat(f);
            }

            return this;
         }

         public Hasher putDouble(double d) {
            for(Hasher hasher : hashers) {
               hasher.putDouble(d);
            }

            return this;
         }

         public Hasher putBoolean(boolean b) {
            for(Hasher hasher : hashers) {
               hasher.putBoolean(b);
            }

            return this;
         }

         public Hasher putChar(char c) {
            for(Hasher hasher : hashers) {
               hasher.putChar(c);
            }

            return this;
         }

         public Hasher putUnencodedChars(CharSequence chars) {
            for(Hasher hasher : hashers) {
               hasher.putUnencodedChars(chars);
            }

            return this;
         }

         public Hasher putString(CharSequence chars, Charset charset) {
            for(Hasher hasher : hashers) {
               hasher.putString(chars, charset);
            }

            return this;
         }

         public Hasher putObject(Object instance, Funnel funnel) {
            for(Hasher hasher : hashers) {
               hasher.putObject(instance, funnel);
            }

            return this;
         }

         public HashCode hash() {
            return AbstractCompositeHashFunction.this.makeHash(hashers);
         }
      };
   }
}
