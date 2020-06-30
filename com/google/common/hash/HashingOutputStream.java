package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
public final class HashingOutputStream extends FilterOutputStream {
   private final Hasher hasher;

   public HashingOutputStream(HashFunction hashFunction, OutputStream out) {
      super((OutputStream)Preconditions.checkNotNull(out));
      this.hasher = (Hasher)Preconditions.checkNotNull(hashFunction.newHasher());
   }

   public void write(int b) throws IOException {
      this.hasher.putByte((byte)b);
      this.out.write(b);
   }

   public void write(byte[] bytes, int off, int len) throws IOException {
      this.hasher.putBytes(bytes, off, len);
      this.out.write(bytes, off, len);
   }

   public HashCode hash() {
      return this.hasher.hash();
   }

   public void close() throws IOException {
      this.out.close();
   }
}
