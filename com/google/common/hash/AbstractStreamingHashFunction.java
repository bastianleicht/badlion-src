package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractHasher;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction implements HashFunction {
   public HashCode hashObject(Object instance, Funnel funnel) {
      return this.newHasher().putObject(instance, funnel).hash();
   }

   public HashCode hashUnencodedChars(CharSequence input) {
      return this.newHasher().putUnencodedChars(input).hash();
   }

   public HashCode hashString(CharSequence input, Charset charset) {
      return this.newHasher().putString(input, charset).hash();
   }

   public HashCode hashInt(int input) {
      return this.newHasher().putInt(input).hash();
   }

   public HashCode hashLong(long input) {
      return this.newHasher().putLong(input).hash();
   }

   public HashCode hashBytes(byte[] input) {
      return this.newHasher().putBytes(input).hash();
   }

   public HashCode hashBytes(byte[] input, int off, int len) {
      return this.newHasher().putBytes(input, off, len).hash();
   }

   public Hasher newHasher(int expectedInputSize) {
      Preconditions.checkArgument(expectedInputSize >= 0);
      return this.newHasher();
   }

   protected abstract static class AbstractStreamingHasher extends AbstractHasher {
      private final ByteBuffer buffer;
      private final int bufferSize;
      private final int chunkSize;

      protected AbstractStreamingHasher(int chunkSize) {
         this(chunkSize, chunkSize);
      }

      protected AbstractStreamingHasher(int chunkSize, int bufferSize) {
         Preconditions.checkArgument(bufferSize % chunkSize == 0);
         this.buffer = ByteBuffer.allocate(bufferSize + 7).order(ByteOrder.LITTLE_ENDIAN);
         this.bufferSize = bufferSize;
         this.chunkSize = chunkSize;
      }

      protected abstract void process(ByteBuffer var1);

      protected void processRemaining(ByteBuffer bb) {
         bb.position(bb.limit());
         bb.limit(this.chunkSize + 7);

         while(bb.position() < this.chunkSize) {
            bb.putLong(0L);
         }

         bb.limit(this.chunkSize);
         bb.flip();
         this.process(bb);
      }

      public final Hasher putBytes(byte[] bytes) {
         return this.putBytes(bytes, 0, bytes.length);
      }

      public final Hasher putBytes(byte[] bytes, int off, int len) {
         return this.putBytes(ByteBuffer.wrap(bytes, off, len).order(ByteOrder.LITTLE_ENDIAN));
      }

      private Hasher putBytes(ByteBuffer readBuffer) {
         if(readBuffer.remaining() <= this.buffer.remaining()) {
            this.buffer.put(readBuffer);
            this.munchIfFull();
            return this;
         } else {
            int bytesToCopy = this.bufferSize - this.buffer.position();

            for(int i = 0; i < bytesToCopy; ++i) {
               this.buffer.put(readBuffer.get());
            }

            this.munch();

            while(readBuffer.remaining() >= this.chunkSize) {
               this.process(readBuffer);
            }

            this.buffer.put(readBuffer);
            return this;
         }
      }

      public final Hasher putUnencodedChars(CharSequence charSequence) {
         for(int i = 0; i < charSequence.length(); ++i) {
            this.putChar(charSequence.charAt(i));
         }

         return this;
      }

      public final Hasher putByte(byte b) {
         this.buffer.put(b);
         this.munchIfFull();
         return this;
      }

      public final Hasher putShort(short s) {
         this.buffer.putShort(s);
         this.munchIfFull();
         return this;
      }

      public final Hasher putChar(char c) {
         this.buffer.putChar(c);
         this.munchIfFull();
         return this;
      }

      public final Hasher putInt(int i) {
         this.buffer.putInt(i);
         this.munchIfFull();
         return this;
      }

      public final Hasher putLong(long l) {
         this.buffer.putLong(l);
         this.munchIfFull();
         return this;
      }

      public final Hasher putObject(Object instance, Funnel funnel) {
         funnel.funnel(instance, this);
         return this;
      }

      public final HashCode hash() {
         this.munch();
         this.buffer.flip();
         if(this.buffer.remaining() > 0) {
            this.processRemaining(this.buffer);
         }

         return this.makeHash();
      }

      abstract HashCode makeHash();

      private void munchIfFull() {
         if(this.buffer.remaining() < 8) {
            this.munch();
         }

      }

      private void munch() {
         this.buffer.flip();

         while(this.buffer.remaining() >= this.chunkSize) {
            this.process(this.buffer);
         }

         this.buffer.compact();
      }
   }
}
