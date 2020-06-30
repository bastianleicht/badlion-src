package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.mutable.Mutable;

public class MutableFloat extends Number implements Comparable, Mutable {
   private static final long serialVersionUID = 5787169186L;
   private float value;

   public MutableFloat() {
   }

   public MutableFloat(float value) {
      this.value = value;
   }

   public MutableFloat(Number value) {
      this.value = value.floatValue();
   }

   public MutableFloat(String value) throws NumberFormatException {
      this.value = Float.parseFloat(value);
   }

   public Float getValue() {
      return Float.valueOf(this.value);
   }

   public void setValue(float value) {
      this.value = value;
   }

   public void setValue(Number value) {
      this.value = value.floatValue();
   }

   public boolean isNaN() {
      return Float.isNaN(this.value);
   }

   public boolean isInfinite() {
      return Float.isInfinite(this.value);
   }

   public void increment() {
      ++this.value;
   }

   public void decrement() {
      --this.value;
   }

   public void add(float operand) {
      this.value += operand;
   }

   public void add(Number operand) {
      this.value += operand.floatValue();
   }

   public void subtract(float operand) {
      this.value -= operand;
   }

   public void subtract(Number operand) {
      this.value -= operand.floatValue();
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Float toFloat() {
      return Float.valueOf(this.floatValue());
   }

   public boolean equals(Object obj) {
      return obj instanceof MutableFloat && Float.floatToIntBits(((MutableFloat)obj).value) == Float.floatToIntBits(this.value);
   }

   public int hashCode() {
      return Float.floatToIntBits(this.value);
   }

   public int compareTo(MutableFloat other) {
      float anotherVal = other.value;
      return Float.compare(this.value, anotherVal);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
