package net.jpountz.xxhash;

import net.jpountz.xxhash.StreamingXXHash32;
import net.jpountz.xxhash.XXHashJNI;

final class StreamingXXHash32JNI extends StreamingXXHash32 {
   private long state;

   StreamingXXHash32JNI(int seed) {
      super(seed);
      this.state = XXHashJNI.XXH32_init(seed);
   }

   private void checkState() {
      if(this.state == 0L) {
         throw new AssertionError("Already finalized");
      }
   }

   public void reset() {
      this.checkState();
      XXHashJNI.XXH32_free(this.state);
      this.state = XXHashJNI.XXH32_init(this.seed);
   }

   public int getValue() {
      this.checkState();
      return XXHashJNI.XXH32_digest(this.state);
   }

   public void update(byte[] bytes, int off, int len) {
      this.checkState();
      XXHashJNI.XXH32_update(this.state, bytes, off, len);
   }

   protected void finalize() throws Throwable {
      super.finalize();
      XXHashJNI.XXH32_free(this.state);
      this.state = 0L;
   }

   static class Factory implements StreamingXXHash32.Factory {
      public static final StreamingXXHash32.Factory INSTANCE = new StreamingXXHash32JNI.Factory();

      public StreamingXXHash32 newStreamingHash(int seed) {
         return new StreamingXXHash32JNI(seed);
      }
   }
}
