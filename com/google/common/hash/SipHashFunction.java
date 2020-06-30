package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import java.io.Serializable;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

final class SipHashFunction extends AbstractStreamingHashFunction implements Serializable {
   private final int c;
   private final int d;
   private final long k0;
   private final long k1;
   private static final long serialVersionUID = 0L;

   SipHashFunction(int c, int d, long k0, long k1) {
      Preconditions.checkArgument(c > 0, "The number of SipRound iterations (c=%s) during Compression must be positive.", new Object[]{Integer.valueOf(c)});
      Preconditions.checkArgument(d > 0, "The number of SipRound iterations (d=%s) during Finalization must be positive.", new Object[]{Integer.valueOf(d)});
      this.c = c;
      this.d = d;
      this.k0 = k0;
      this.k1 = k1;
   }

   public int bits() {
      return 64;
   }

   public Hasher newHasher() {
      return new SipHashFunction.SipHasher(this.c, this.d, this.k0, this.k1);
   }

   public String toString() {
      return "Hashing.sipHash" + this.c + "" + this.d + "(" + this.k0 + ", " + this.k1 + ")";
   }

   public boolean equals(@Nullable Object object) {
      if(!(object instanceof SipHashFunction)) {
         return false;
      } else {
         SipHashFunction other = (SipHashFunction)object;
         return this.c == other.c && this.d == other.d && this.k0 == other.k0 && this.k1 == other.k1;
      }
   }

   public int hashCode() {
      return (int)((long)(this.getClass().hashCode() ^ this.c ^ this.d) ^ this.k0 ^ this.k1);
   }

   private static final class SipHasher extends AbstractStreamingHashFunction.AbstractStreamingHasher {
      private static final int CHUNK_SIZE = 8;
      private final int c;
      private final int d;
      private long v0 = 8317987319222330741L;
      private long v1 = 7237128888997146477L;
      private long v2 = 7816392313619706465L;
      private long v3 = 8387220255154660723L;
      private long b = 0L;
      private long finalM = 0L;

      SipHasher(int c, int d, long k0, long k1) {
         super(8);
         this.c = c;
         this.d = d;
         this.v0 ^= k0;
         this.v1 ^= k1;
         this.v2 ^= k0;
         this.v3 ^= k1;
      }

      protected void process(ByteBuffer buffer) {
         this.b += 8L;
         this.processM(buffer.getLong());
      }

      protected void processRemaining(ByteBuffer buffer) {
         this.b += (long)buffer.remaining();

         for(int i = 0; buffer.hasRemaining(); i += 8) {
            this.finalM ^= ((long)buffer.get() & 255L) << i;
         }

      }

      public HashCode makeHash() {
         this.finalM ^= this.b << 56;
         this.processM(this.finalM);
         this.v2 ^= 255L;
         this.sipRound(this.d);
         return HashCode.fromLong(this.v0 ^ this.v1 ^ this.v2 ^ this.v3);
      }

      private void processM(long m) {
         this.v3 ^= m;
         this.sipRound(this.c);
         this.v0 ^= m;
      }

      private void sipRound(int iterations) {
         for(int i = 0; i < iterations; ++i) {
            this.v0 += this.v1;
            this.v2 += this.v3;
            this.v1 = Long.rotateLeft(this.v1, 13);
            this.v3 = Long.rotateLeft(this.v3, 16);
            this.v1 ^= this.v0;
            this.v3 ^= this.v2;
            this.v0 = Long.rotateLeft(this.v0, 32);
            this.v2 += this.v1;
            this.v0 += this.v3;
            this.v1 = Long.rotateLeft(this.v1, 17);
            this.v3 = Long.rotateLeft(this.v3, 21);
            this.v1 ^= this.v2;
            this.v3 ^= this.v0;
            this.v2 = Long.rotateLeft(this.v2, 32);
         }

      }
   }
}
