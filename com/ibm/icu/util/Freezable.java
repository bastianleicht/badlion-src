package com.ibm.icu.util;

public interface Freezable extends Cloneable {
   boolean isFrozen();

   Object freeze();

   Object cloneAsThawed();
}
