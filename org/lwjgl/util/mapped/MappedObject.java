package org.lwjgl.util.mapped;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.util.mapped.MappedForeach;

public abstract class MappedObject {
   static final boolean CHECKS = LWJGLUtil.getPrivilegedBoolean("org.lwjgl.util.mapped.Checks");
   public long baseAddress;
   public long viewAddress;
   ByteBuffer preventGC;
   public static int SIZEOF = -1;
   public int view;

   protected final long getViewAddress(int view) {
      throw new InternalError("type not registered");
   }

   public final void setViewAddress(long address) {
      if(CHECKS) {
         this.checkAddress(address);
      }

      this.viewAddress = address;
   }

   final void checkAddress(long address) {
      long base = MemoryUtil.getAddress0((Buffer)this.preventGC);
      int offset = (int)(address - base);
      if(address < base || this.preventGC.capacity() < offset + this.getSizeof()) {
         throw new IndexOutOfBoundsException(Integer.toString(offset / this.getSizeof()));
      }
   }

   final void checkRange(int bytes) {
      if(bytes < 0) {
         throw new IllegalArgumentException();
      } else if((long)this.preventGC.capacity() < this.viewAddress - MemoryUtil.getAddress0((Buffer)this.preventGC) + (long)bytes) {
         throw new BufferOverflowException();
      }
   }

   public final int getAlign() {
      throw new InternalError("type not registered");
   }

   public final int getSizeof() {
      throw new InternalError("type not registered");
   }

   public final int capacity() {
      throw new InternalError("type not registered");
   }

   public static MappedObject map(ByteBuffer bb) {
      throw new InternalError("type not registered");
   }

   public static MappedObject map(long address, int capacity) {
      throw new InternalError("type not registered");
   }

   public static MappedObject malloc(int elementCount) {
      throw new InternalError("type not registered");
   }

   public final MappedObject dup() {
      throw new InternalError("type not registered");
   }

   public final MappedObject slice() {
      throw new InternalError("type not registered");
   }

   public final void runViewConstructor() {
      throw new InternalError("type not registered");
   }

   public final void next() {
      throw new InternalError("type not registered");
   }

   public final void copyTo(MappedObject target) {
      throw new InternalError("type not registered");
   }

   public final void copyRange(MappedObject target, int instances) {
      throw new InternalError("type not registered");
   }

   public static Iterable foreach(MappedObject mapped) {
      return foreach(mapped, mapped.capacity());
   }

   public static Iterable foreach(MappedObject mapped, int elementCount) {
      return new MappedForeach(mapped, elementCount);
   }

   public final MappedObject[] asArray() {
      throw new InternalError("type not registered");
   }

   public final ByteBuffer backingByteBuffer() {
      return this.preventGC;
   }
}
