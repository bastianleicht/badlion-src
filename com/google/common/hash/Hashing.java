package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.hash.AbstractCompositeHashFunction;
import com.google.common.hash.ChecksumHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.MessageDigestHashFunction;
import com.google.common.hash.Murmur3_128HashFunction;
import com.google.common.hash.Murmur3_32HashFunction;
import com.google.common.hash.SipHashFunction;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.annotation.Nullable;

@Beta
public final class Hashing {
   private static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();

   public static HashFunction goodFastHash(int minimumBits) {
      int bits = checkPositiveAndMakeMultipleOf32(minimumBits);
      if(bits == 32) {
         return Hashing.Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32;
      } else if(bits <= 128) {
         return Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
      } else {
         int hashFunctionsNeeded = (bits + 127) / 128;
         HashFunction[] hashFunctions = new HashFunction[hashFunctionsNeeded];
         hashFunctions[0] = Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
         int seed = GOOD_FAST_HASH_SEED;

         for(int i = 1; i < hashFunctionsNeeded; ++i) {
            seed += 1500450271;
            hashFunctions[i] = murmur3_128(seed);
         }

         return new Hashing.ConcatenatedHashFunction(hashFunctions);
      }
   }

   public static HashFunction murmur3_32(int seed) {
      return new Murmur3_32HashFunction(seed);
   }

   public static HashFunction murmur3_32() {
      return Hashing.Murmur3_32Holder.MURMUR3_32;
   }

   public static HashFunction murmur3_128(int seed) {
      return new Murmur3_128HashFunction(seed);
   }

   public static HashFunction murmur3_128() {
      return Hashing.Murmur3_128Holder.MURMUR3_128;
   }

   public static HashFunction sipHash24() {
      return Hashing.SipHash24Holder.SIP_HASH_24;
   }

   public static HashFunction sipHash24(long k0, long k1) {
      return new SipHashFunction(2, 4, k0, k1);
   }

   public static HashFunction md5() {
      return Hashing.Md5Holder.MD5;
   }

   public static HashFunction sha1() {
      return Hashing.Sha1Holder.SHA_1;
   }

   public static HashFunction sha256() {
      return Hashing.Sha256Holder.SHA_256;
   }

   public static HashFunction sha512() {
      return Hashing.Sha512Holder.SHA_512;
   }

   public static HashFunction crc32() {
      return Hashing.Crc32Holder.CRC_32;
   }

   public static HashFunction adler32() {
      return Hashing.Adler32Holder.ADLER_32;
   }

   private static HashFunction checksumHashFunction(Hashing.ChecksumType type, String toString) {
      return new ChecksumHashFunction(type, type.bits, toString);
   }

   public static int consistentHash(HashCode hashCode, int buckets) {
      return consistentHash(hashCode.padToLong(), buckets);
   }

   public static int consistentHash(long input, int buckets) {
      Preconditions.checkArgument(buckets > 0, "buckets must be positive: %s", new Object[]{Integer.valueOf(buckets)});
      Hashing.LinearCongruentialGenerator generator = new Hashing.LinearCongruentialGenerator(input);
      int candidate = 0;

      while(true) {
         int next = (int)((double)(candidate + 1) / generator.nextDouble());
         if(next < 0 || next >= buckets) {
            return candidate;
         }

         candidate = next;
      }
   }

   public static HashCode combineOrdered(Iterable hashCodes) {
      Iterator<HashCode> iterator = hashCodes.iterator();
      Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
      int bits = ((HashCode)iterator.next()).bits();
      byte[] resultBytes = new byte[bits / 8];

      for(HashCode hashCode : hashCodes) {
         byte[] nextBytes = hashCode.asBytes();
         Preconditions.checkArgument(nextBytes.length == resultBytes.length, "All hashcodes must have the same bit length.");

         for(int i = 0; i < nextBytes.length; ++i) {
            resultBytes[i] = (byte)(resultBytes[i] * 37 ^ nextBytes[i]);
         }
      }

      return HashCode.fromBytesNoCopy(resultBytes);
   }

   public static HashCode combineUnordered(Iterable hashCodes) {
      Iterator<HashCode> iterator = hashCodes.iterator();
      Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
      byte[] resultBytes = new byte[((HashCode)iterator.next()).bits() / 8];

      for(HashCode hashCode : hashCodes) {
         byte[] nextBytes = hashCode.asBytes();
         Preconditions.checkArgument(nextBytes.length == resultBytes.length, "All hashcodes must have the same bit length.");

         for(int i = 0; i < nextBytes.length; ++i) {
            resultBytes[i] += nextBytes[i];
         }
      }

      return HashCode.fromBytesNoCopy(resultBytes);
   }

   static int checkPositiveAndMakeMultipleOf32(int bits) {
      Preconditions.checkArgument(bits > 0, "Number of bits must be positive");
      return bits + 31 & -32;
   }

   private static class Adler32Holder {
      static final HashFunction ADLER_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.ADLER_32, "Hashing.adler32()");
   }

   static enum ChecksumType implements Supplier {
      CRC_32(32) {
         public Checksum get() {
            return new CRC32();
         }
      },
      ADLER_32(32) {
         public Checksum get() {
            return new Adler32();
         }
      };

      private final int bits;

      private ChecksumType(int bits) {
         this.bits = bits;
      }

      public abstract Checksum get();
   }

   @VisibleForTesting
   static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {
      private final int bits;

      ConcatenatedHashFunction(HashFunction... functions) {
         super(functions);
         int bitSum = 0;

         for(HashFunction function : functions) {
            bitSum += function.bits();
         }

         this.bits = bitSum;
      }

      HashCode makeHash(Hasher[] hashers) {
         byte[] bytes = new byte[this.bits / 8];
         int i = 0;

         for(Hasher hasher : hashers) {
            HashCode newHash = hasher.hash();
            i += newHash.writeBytesTo(bytes, i, newHash.bits() / 8);
         }

         return HashCode.fromBytesNoCopy(bytes);
      }

      public int bits() {
         return this.bits;
      }

      public boolean equals(@Nullable Object object) {
         if(object instanceof Hashing.ConcatenatedHashFunction) {
            Hashing.ConcatenatedHashFunction other = (Hashing.ConcatenatedHashFunction)object;
            if(this.bits == other.bits && this.functions.length == other.functions.length) {
               for(int i = 0; i < this.functions.length; ++i) {
                  if(!this.functions[i].equals(other.functions[i])) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int hash = this.bits;

         for(HashFunction function : this.functions) {
            hash ^= function.hashCode();
         }

         return hash;
      }
   }

   private static class Crc32Holder {
      static final HashFunction CRC_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.CRC_32, "Hashing.crc32()");
   }

   private static final class LinearCongruentialGenerator {
      private long state;

      public LinearCongruentialGenerator(long seed) {
         this.state = seed;
      }

      public double nextDouble() {
         this.state = 2862933555777941757L * this.state + 1L;
         return (double)((int)(this.state >>> 33) + 1) / 2.147483648E9D;
      }
   }

   private static class Md5Holder {
      static final HashFunction MD5 = new MessageDigestHashFunction("MD5", "Hashing.md5()");
   }

   private static class Murmur3_128Holder {
      static final HashFunction MURMUR3_128 = new Murmur3_128HashFunction(0);
      static final HashFunction GOOD_FAST_HASH_FUNCTION_128 = Hashing.murmur3_128(Hashing.GOOD_FAST_HASH_SEED);
   }

   private static class Murmur3_32Holder {
      static final HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
      static final HashFunction GOOD_FAST_HASH_FUNCTION_32 = Hashing.murmur3_32(Hashing.GOOD_FAST_HASH_SEED);
   }

   private static class Sha1Holder {
      static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");
   }

   private static class Sha256Holder {
      static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256", "Hashing.sha256()");
   }

   private static class Sha512Holder {
      static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512", "Hashing.sha512()");
   }

   private static class SipHash24Holder {
      static final HashFunction SIP_HASH_24 = new SipHashFunction(2, 4, 506097522914230528L, 1084818905618843912L);
   }
}
