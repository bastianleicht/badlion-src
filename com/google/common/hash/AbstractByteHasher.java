package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractHasher;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractByteHasher extends AbstractHasher {
   private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);

   protected abstract void update(byte var1);

   protected void update(byte[] b) {
      this.update(b, 0, b.length);
   }

   protected void update(byte[] b, int off, int len) {
      for(int i = off; i < off + len; ++i) {
         this.update(b[i]);
      }

   }

   public Hasher putByte(byte b) {
      this.update(b);
      return this;
   }

   public Hasher putBytes(byte[] bytes) {
      Preconditions.checkNotNull(bytes);
      this.update(bytes);
      return this;
   }

   public Hasher putBytes(byte[] bytes, int off, int len) {
      Preconditions.checkPositionIndexes(off, off + len, bytes.length);
      this.update(bytes, off, len);
      return this;
   }

   private Hasher update(int bytes) {
      try {
         this.update(this.scratch.array(), 0, bytes);
      } finally {
         this.scratch.clear();
      }

      return this;
   }

   public Hasher putShort(short s) {
      this.scratch.putShort(s);
      return this.update((int)2);
   }

   public Hasher putInt(int i) {
      this.scratch.putInt(i);
      return this.update((int)4);
   }

   public Hasher putLong(long l) {
      this.scratch.putLong(l);
      return this.update((int)8);
   }

   public Hasher putChar(char c) {
      this.scratch.putChar(c);
      return this.update((int)2);
   }

   public Hasher putObject(Object instance, Funnel funnel) {
      funnel.funnel(instance, this);
      return this;
   }
}
