package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.mutable.Mutable;

public class MutableByte extends Number implements Comparable, Mutable {
   private static final long serialVersionUID = -1585823265L;
   private byte value;

   public MutableByte() {
   }

   public MutableByte(byte value) {
      this.value = value;
   }

   public MutableByte(Number value) {
      this.value = value.byteValue();
   }

   public MutableByte(String value) throws NumberFormatException {
      this.value = Byte.parseByte(value);
   }

   public Byte getValue() {
      return Byte.valueOf(this.value);
   }

   public void setValue(byte value) {
      this.value = value;
   }

   public void setValue(Number value) {
      this.value = value.byteValue();
   }

   public void increment() {
      ++this.value;
   }

   public void decrement() {
      --this.value;
   }

   public void add(byte operand) {
      this.value += operand;
   }

   public void add(Number operand) {
      this.value += operand.byteValue();
   }

   public void subtract(byte operand) {
      this.value -= operand;
   }

   public void subtract(Number operand) {
      this.value -= operand.byteValue();
   }

   public byte byteValue() {
      return this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Byte toByte() {
      return Byte.valueOf(this.byteValue());
   }

   public boolean equals(Object obj) {
      return obj instanceof MutableByte?this.value == ((MutableByte)obj).byteValue():false;
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableByte other) {
      byte anotherVal = other.value;
      return this.value < anotherVal?-1:(this.value == anotherVal?0:1);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
