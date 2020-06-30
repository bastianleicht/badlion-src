package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractByteHasher;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class MessageDigestHashFunction extends AbstractStreamingHashFunction implements Serializable {
   private final MessageDigest prototype;
   private final int bytes;
   private final boolean supportsClone;
   private final String toString;

   MessageDigestHashFunction(String algorithmName, String toString) {
      this.prototype = getMessageDigest(algorithmName);
      this.bytes = this.prototype.getDigestLength();
      this.toString = (String)Preconditions.checkNotNull(toString);
      this.supportsClone = this.supportsClone();
   }

   MessageDigestHashFunction(String algorithmName, int bytes, String toString) {
      this.toString = (String)Preconditions.checkNotNull(toString);
      this.prototype = getMessageDigest(algorithmName);
      int maxLength = this.prototype.getDigestLength();
      Preconditions.checkArgument(bytes >= 4 && bytes <= maxLength, "bytes (%s) must be >= 4 and < %s", new Object[]{Integer.valueOf(bytes), Integer.valueOf(maxLength)});
      this.bytes = bytes;
      this.supportsClone = this.supportsClone();
   }

   private boolean supportsClone() {
      try {
         this.prototype.clone();
         return true;
      } catch (CloneNotSupportedException var2) {
         return false;
      }
   }

   public int bits() {
      return this.bytes * 8;
   }

   public String toString() {
      return this.toString;
   }

   private static MessageDigest getMessageDigest(String algorithmName) {
      try {
         return MessageDigest.getInstance(algorithmName);
      } catch (NoSuchAlgorithmException var2) {
         throw new AssertionError(var2);
      }
   }

   public Hasher newHasher() {
      if(this.supportsClone) {
         try {
            return new MessageDigestHashFunction.MessageDigestHasher((MessageDigest)this.prototype.clone(), this.bytes);
         } catch (CloneNotSupportedException var2) {
            ;
         }
      }

      return new MessageDigestHashFunction.MessageDigestHasher(getMessageDigest(this.prototype.getAlgorithm()), this.bytes);
   }

   Object writeReplace() {
      return new MessageDigestHashFunction.SerializedForm(this.prototype.getAlgorithm(), this.bytes, this.toString);
   }

   private static final class MessageDigestHasher extends AbstractByteHasher {
      private final MessageDigest digest;
      private final int bytes;
      private boolean done;

      private MessageDigestHasher(MessageDigest digest, int bytes) {
         this.digest = digest;
         this.bytes = bytes;
      }

      protected void update(byte b) {
         this.checkNotDone();
         this.digest.update(b);
      }

      protected void update(byte[] b) {
         this.checkNotDone();
         this.digest.update(b);
      }

      protected void update(byte[] b, int off, int len) {
         this.checkNotDone();
         this.digest.update(b, off, len);
      }

      private void checkNotDone() {
         Preconditions.checkState(!this.done, "Cannot re-use a Hasher after calling hash() on it");
      }

      public HashCode hash() {
         this.checkNotDone();
         this.done = true;
         return this.bytes == this.digest.getDigestLength()?HashCode.fromBytesNoCopy(this.digest.digest()):HashCode.fromBytesNoCopy(Arrays.copyOf(this.digest.digest(), this.bytes));
      }
   }

   private static final class SerializedForm implements Serializable {
      private final String algorithmName;
      private final int bytes;
      private final String toString;
      private static final long serialVersionUID = 0L;

      private SerializedForm(String algorithmName, int bytes, String toString) {
         this.algorithmName = algorithmName;
         this.bytes = bytes;
         this.toString = toString;
      }

      private Object readResolve() {
         return new MessageDigestHashFunction(this.algorithmName, this.bytes, this.toString);
      }
   }
}
