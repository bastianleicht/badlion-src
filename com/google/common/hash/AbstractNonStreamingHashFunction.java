package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractHasher;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction implements HashFunction {
   public Hasher newHasher() {
      return new AbstractNonStreamingHashFunction.BufferingHasher(32);
   }

   public Hasher newHasher(int expectedInputSize) {
      Preconditions.checkArgument(expectedInputSize >= 0);
      return new AbstractNonStreamingHashFunction.BufferingHasher(expectedInputSize);
   }

   public HashCode hashObject(Object instance, Funnel funnel) {
      return this.newHasher().putObject(instance, funnel).hash();
   }

   public HashCode hashUnencodedChars(CharSequence input) {
      int len = input.length();
      Hasher hasher = this.newHasher(len * 2);

      for(int i = 0; i < len; ++i) {
         hasher.putChar(input.charAt(i));
      }

      return hasher.hash();
   }

   public HashCode hashString(CharSequence input, Charset charset) {
      return this.hashBytes(input.toString().getBytes(charset));
   }

   public HashCode hashInt(int input) {
      return this.newHasher(4).putInt(input).hash();
   }

   public HashCode hashLong(long input) {
      return this.newHasher(8).putLong(input).hash();
   }

   public HashCode hashBytes(byte[] input) {
      return this.hashBytes(input, 0, input.length);
   }

   private final class BufferingHasher extends AbstractHasher {
      final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
      static final int BOTTOM_BYTE = 255;

      BufferingHasher(int expectedInputSize) {
         this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(expectedInputSize);
      }

      public Hasher putByte(byte b) {
         this.stream.write(b);
         return this;
      }

      public Hasher putBytes(byte[] bytes) {
         try {
            this.stream.write(bytes);
            return this;
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      public Hasher putBytes(byte[] bytes, int off, int len) {
         this.stream.write(bytes, off, len);
         return this;
      }

      public Hasher putShort(short s) {
         this.stream.write(s & 255);
         this.stream.write(s >>> 8 & 255);
         return this;
      }

      public Hasher putInt(int i) {
         this.stream.write(i & 255);
         this.stream.write(i >>> 8 & 255);
         this.stream.write(i >>> 16 & 255);
         this.stream.write(i >>> 24 & 255);
         return this;
      }

      public Hasher putLong(long l) {
         for(int i = 0; i < 64; i += 8) {
            this.stream.write((byte)((int)(l >>> i & 255L)));
         }

         return this;
      }

      public Hasher putChar(char c) {
         this.stream.write(c & 255);
         this.stream.write(c >>> 8 & 255);
         return this;
      }

      public Hasher putObject(Object instance, Funnel funnel) {
         funnel.funnel(instance, this);
         return this;
      }

      public HashCode hash() {
         return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
      }
   }

   private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
      ExposedByteArrayOutputStream(int expectedInputSize) {
         super(expectedInputSize);
      }

      byte[] byteArray() {
         return this.buf;
      }

      int length() {
         return this.count;
      }
   }
}
