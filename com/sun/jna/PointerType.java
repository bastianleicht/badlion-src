package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;

public abstract class PointerType implements NativeMapped {
   private Pointer pointer;

   protected PointerType() {
      this.pointer = Pointer.NULL;
   }

   protected PointerType(Pointer p) {
      this.pointer = p;
   }

   public Class nativeType() {
      return Pointer.class;
   }

   public Object toNative() {
      return this.getPointer();
   }

   public Pointer getPointer() {
      return this.pointer;
   }

   public void setPointer(Pointer p) {
      this.pointer = p;
   }

   public Object fromNative(Object nativeValue, FromNativeContext context) {
      if(nativeValue == null) {
         return null;
      } else {
         try {
            PointerType pt = (PointerType)this.getClass().newInstance();
            pt.pointer = (Pointer)nativeValue;
            return pt;
         } catch (InstantiationException var4) {
            throw new IllegalArgumentException("Can\'t instantiate " + this.getClass());
         } catch (IllegalAccessException var5) {
            throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
         }
      }
   }

   public int hashCode() {
      return this.pointer != null?this.pointer.hashCode():0;
   }

   public boolean equals(Object o) {
      if(o == this) {
         return true;
      } else if(o instanceof PointerType) {
         Pointer p = ((PointerType)o).getPointer();
         return this.pointer == null?p == null:this.pointer.equals(p);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.pointer == null?"NULL":this.pointer.toString();
   }
}
