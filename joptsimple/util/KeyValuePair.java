package joptsimple.util;

public final class KeyValuePair {
   public final String key;
   public final String value;

   private KeyValuePair(String key, String value) {
      this.key = key;
      this.value = value;
   }

   public static KeyValuePair valueOf(String asString) {
      int equalsIndex = asString.indexOf(61);
      if(equalsIndex == -1) {
         return new KeyValuePair(asString, "");
      } else {
         String aKey = asString.substring(0, equalsIndex);
         String aValue = equalsIndex == asString.length() - 1?"":asString.substring(equalsIndex + 1);
         return new KeyValuePair(aKey, aValue);
      }
   }

   public boolean equals(Object that) {
      if(!(that instanceof KeyValuePair)) {
         return false;
      } else {
         KeyValuePair other = (KeyValuePair)that;
         return this.key.equals(other.key) && this.value.equals(other.value);
      }
   }

   public int hashCode() {
      return this.key.hashCode() ^ this.value.hashCode();
   }

   public String toString() {
      return this.key + '=' + this.value;
   }
}
