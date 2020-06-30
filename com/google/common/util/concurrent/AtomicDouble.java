package com.google.common.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class AtomicDouble extends Number implements Serializable {
   private static final long serialVersionUID = 0L;
   private transient volatile long value;
   private static final AtomicLongFieldUpdater updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");

   public AtomicDouble(double initialValue) {
      this.value = Double.doubleToRawLongBits(initialValue);
   }

   public AtomicDouble() {
   }

   public final double get() {
      return Double.longBitsToDouble(this.value);
   }

   public final void set(double newValue) {
      long next = Double.doubleToRawLongBits(newValue);
      this.value = next;
   }

   public final void lazySet(double newValue) {
      this.set(newValue);
   }

   public final double getAndSet(double newValue) {
      long next = Double.doubleToRawLongBits(newValue);
      return Double.longBitsToDouble(updater.getAndSet(this, next));
   }

   public final boolean compareAndSet(double expect, double update) {
      return updater.compareAndSet(this, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
   }

   public final boolean weakCompareAndSet(double expect, double update) {
      return updater.weakCompareAndSet(this, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
   }

   public final double getAndAdd(double delta) {
      double currentVal;
      while(true) {
         long current = this.value;
         currentVal = Double.longBitsToDouble(current);
         double nextVal = currentVal + delta;
         long next = Double.doubleToRawLongBits(nextVal);
         if(updater.compareAndSet(this, current, next)) {
            break;
         }
      }

      return currentVal;
   }

   public final double addAndGet(double delta) {
      double nextVal;
      while(true) {
         long current = this.value;
         double currentVal = Double.longBitsToDouble(current);
         nextVal = currentVal + delta;
         long next = Double.doubleToRawLongBits(nextVal);
         if(updater.compareAndSet(this, current, next)) {
            break;
         }
      }

      return nextVal;
   }

   public String toString() {
      return Double.toString(this.get());
   }

   public int intValue() {
      return (int)this.get();
   }

   public long longValue() {
      return (long)this.get();
   }

   public float floatValue() {
      return (float)this.get();
   }

   public double doubleValue() {
      return this.get();
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeDouble(this.get());
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.set(s.readDouble());
   }
}
