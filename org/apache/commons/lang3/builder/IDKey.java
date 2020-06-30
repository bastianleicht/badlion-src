package org.apache.commons.lang3.builder;

final class IDKey {
   private final Object value;
   private final int id;

   public IDKey(Object _value) {
      this.id = System.identityHashCode(_value);
      this.value = _value;
   }

   public int hashCode() {
      return this.id;
   }

   public boolean equals(Object other) {
      if(!(other instanceof IDKey)) {
         return false;
      } else {
         IDKey idKey = (IDKey)other;
         return this.id != idKey.id?false:this.value == idKey.value;
      }
   }
}
