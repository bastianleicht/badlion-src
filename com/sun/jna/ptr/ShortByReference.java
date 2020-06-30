package com.sun.jna.ptr;

import com.sun.jna.ptr.ByReference;

public class ShortByReference extends ByReference {
   public ShortByReference() {
      this((short)0);
   }

   public ShortByReference(short value) {
      super(2);
      this.setValue(value);
   }

   public void setValue(short value) {
      this.getPointer().setShort(0L, value);
   }

   public short getValue() {
      return this.getPointer().getShort(0L);
   }
}
