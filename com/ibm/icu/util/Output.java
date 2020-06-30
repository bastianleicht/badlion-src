package com.ibm.icu.util;

public class Output {
   public Object value;

   public String toString() {
      return this.value == null?"null":this.value.toString();
   }

   public Output() {
   }

   public Output(Object value) {
      this.value = value;
   }
}
