package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.mutable.Mutable;

public class MutableBoolean implements Mutable, Serializable, Comparable {
   private static final long serialVersionUID = -4830728138360036487L;
   private boolean value;

   public MutableBoolean() {
   }

   public MutableBoolean(boolean value) {
      this.value = value;
   }

   public MutableBoolean(Boolean value) {
      this.value = value.booleanValue();
   }

   public Boolean getValue() {
      return Boolean.valueOf(this.value);
   }

   public void setValue(boolean value) {
      this.value = value;
   }

   public void setFalse() {
      this.value = false;
   }

   public void setTrue() {
      this.value = true;
   }

   public void setValue(Boolean value) {
      this.value = value.booleanValue();
   }

   public boolean isTrue() {
      return this.value;
   }

   public boolean isFalse() {
      return !this.value;
   }

   public boolean booleanValue() {
      return this.value;
   }

   public Boolean toBoolean() {
      return Boolean.valueOf(this.booleanValue());
   }

   public boolean equals(Object obj) {
      return obj instanceof MutableBoolean?this.value == ((MutableBoolean)obj).booleanValue():false;
   }

   public int hashCode() {
      return this.value?Boolean.TRUE.hashCode():Boolean.FALSE.hashCode();
   }

   public int compareTo(MutableBoolean other) {
      boolean anotherVal = other.value;
      return this.value == anotherVal?0:(this.value?1:-1);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
