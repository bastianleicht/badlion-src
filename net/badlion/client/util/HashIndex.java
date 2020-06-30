package net.badlion.client.util;

public class HashIndex {
   private final String hash;
   private final int index;

   public HashIndex(String hash, int index) {
      this.hash = hash;
      this.index = index;
   }

   public String getHash() {
      if(this.hash == null) {
         throw new NullPointerException("Hash cannot be null");
      } else {
         return this.hash;
      }
   }

   public int getIndex() {
      return this.index;
   }
}
