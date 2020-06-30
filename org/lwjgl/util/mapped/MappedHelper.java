package org.lwjgl.util.mapped;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.util.mapped.MappedObject;
import org.lwjgl.util.mapped.MappedObjectUnsafe;
import org.lwjgl.util.mapped.MappedSet2;
import org.lwjgl.util.mapped.MappedSet3;
import org.lwjgl.util.mapped.MappedSet4;

public class MappedHelper {
   public static void setup(MappedObject mo, ByteBuffer buffer, int align, int sizeof) {
      if(LWJGLUtil.CHECKS && mo.baseAddress != 0L) {
         throw new IllegalStateException("this method should not be called by user-code");
      } else if(LWJGLUtil.CHECKS && !buffer.isDirect()) {
         throw new IllegalArgumentException("bytebuffer must be direct");
      } else {
         mo.preventGC = buffer;
         if(LWJGLUtil.CHECKS && align <= 0) {
            throw new IllegalArgumentException("invalid alignment");
         } else if(!LWJGLUtil.CHECKS || sizeof > 0 && sizeof % align == 0) {
            long addr = MemoryUtil.getAddress(buffer);
            if(LWJGLUtil.CHECKS && addr % (long)align != 0L) {
               throw new IllegalStateException("buffer address not aligned on " + align + " bytes");
            } else {
               mo.baseAddress = mo.viewAddress = addr;
            }
         } else {
            throw new IllegalStateException("sizeof not a multiple of alignment");
         }
      }
   }

   public static void checkAddress(long viewAddress, MappedObject mapped) {
      mapped.checkAddress(viewAddress);
   }

   public static void put_views(MappedSet2 set, int view) {
      set.view(view);
   }

   public static void put_views(MappedSet3 set, int view) {
      set.view(view);
   }

   public static void put_views(MappedSet4 set, int view) {
      set.view(view);
   }

   public static void put_view(MappedObject mapped, int view, int sizeof) {
      mapped.setViewAddress(mapped.baseAddress + (long)(view * sizeof));
   }

   public static int get_view(MappedObject mapped, int sizeof) {
      return (int)(mapped.viewAddress - mapped.baseAddress) / sizeof;
   }

   public static void put_view_shift(MappedObject mapped, int view, int sizeof_shift) {
      mapped.setViewAddress(mapped.baseAddress + (long)(view << sizeof_shift));
   }

   public static int get_view_shift(MappedObject mapped, int sizeof_shift) {
      return (int)(mapped.viewAddress - mapped.baseAddress) >> sizeof_shift;
   }

   public static void put_view_next(MappedObject mapped, int sizeof) {
      mapped.setViewAddress(mapped.viewAddress + (long)sizeof);
   }

   public static MappedObject dup(MappedObject src, MappedObject dst) {
      dst.baseAddress = src.baseAddress;
      dst.viewAddress = src.viewAddress;
      dst.preventGC = src.preventGC;
      return dst;
   }

   public static MappedObject slice(MappedObject src, MappedObject dst) {
      dst.baseAddress = src.viewAddress;
      dst.viewAddress = src.viewAddress;
      dst.preventGC = src.preventGC;
      return dst;
   }

   public static void copy(MappedObject src, MappedObject dst, int bytes) {
      if(MappedObject.CHECKS) {
         src.checkRange(bytes);
         dst.checkRange(bytes);
      }

      MappedObjectUnsafe.INSTANCE.copyMemory(src.viewAddress, dst.viewAddress, (long)bytes);
   }

   public static ByteBuffer newBuffer(long address, int capacity) {
      return MappedObjectUnsafe.newBuffer(address, capacity);
   }

   public static void bput(byte value, long addr) {
      MappedObjectUnsafe.INSTANCE.putByte(addr, value);
   }

   public static void bput(MappedObject mapped, byte value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putByte(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static byte bget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getByte(addr);
   }

   public static byte bget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getByte(mapped.viewAddress + (long)fieldOffset);
   }

   public static void bvput(byte value, long addr) {
      MappedObjectUnsafe.INSTANCE.putByteVolatile((Object)null, addr, value);
   }

   public static void bvput(MappedObject mapped, byte value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putByteVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static byte bvget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getByteVolatile((Object)null, addr);
   }

   public static byte bvget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getByteVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void sput(short value, long addr) {
      MappedObjectUnsafe.INSTANCE.putShort(addr, value);
   }

   public static void sput(MappedObject mapped, short value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putShort(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static short sget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getShort(addr);
   }

   public static short sget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getShort(mapped.viewAddress + (long)fieldOffset);
   }

   public static void svput(short value, long addr) {
      MappedObjectUnsafe.INSTANCE.putShortVolatile((Object)null, addr, value);
   }

   public static void svput(MappedObject mapped, short value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putShortVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static short svget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getShortVolatile((Object)null, addr);
   }

   public static short svget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getShortVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void cput(char value, long addr) {
      MappedObjectUnsafe.INSTANCE.putChar(addr, value);
   }

   public static void cput(MappedObject mapped, char value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putChar(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static char cget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getChar(addr);
   }

   public static char cget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getChar(mapped.viewAddress + (long)fieldOffset);
   }

   public static void cvput(char value, long addr) {
      MappedObjectUnsafe.INSTANCE.putCharVolatile((Object)null, addr, value);
   }

   public static void cvput(MappedObject mapped, char value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putCharVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static char cvget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getCharVolatile((Object)null, addr);
   }

   public static char cvget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getCharVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void iput(int value, long addr) {
      MappedObjectUnsafe.INSTANCE.putInt(addr, value);
   }

   public static void iput(MappedObject mapped, int value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putInt(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static int iget(long address) {
      return MappedObjectUnsafe.INSTANCE.getInt(address);
   }

   public static int iget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getInt(mapped.viewAddress + (long)fieldOffset);
   }

   public static void ivput(int value, long addr) {
      MappedObjectUnsafe.INSTANCE.putIntVolatile((Object)null, addr, value);
   }

   public static void ivput(MappedObject mapped, int value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putIntVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static int ivget(long address) {
      return MappedObjectUnsafe.INSTANCE.getIntVolatile((Object)null, address);
   }

   public static int ivget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getIntVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void fput(float value, long addr) {
      MappedObjectUnsafe.INSTANCE.putFloat(addr, value);
   }

   public static void fput(MappedObject mapped, float value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putFloat(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static float fget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getFloat(addr);
   }

   public static float fget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getFloat(mapped.viewAddress + (long)fieldOffset);
   }

   public static void fvput(float value, long addr) {
      MappedObjectUnsafe.INSTANCE.putFloatVolatile((Object)null, addr, value);
   }

   public static void fvput(MappedObject mapped, float value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putFloatVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static float fvget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getFloatVolatile((Object)null, addr);
   }

   public static float fvget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getFloatVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void jput(long value, long addr) {
      MappedObjectUnsafe.INSTANCE.putLong(addr, value);
   }

   public static void jput(MappedObject mapped, long value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putLong(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static long jget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getLong(addr);
   }

   public static long jget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getLong(mapped.viewAddress + (long)fieldOffset);
   }

   public static void jvput(long value, long addr) {
      MappedObjectUnsafe.INSTANCE.putLongVolatile((Object)null, addr, value);
   }

   public static void jvput(MappedObject mapped, long value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putLongVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static long jvget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getLongVolatile((Object)null, addr);
   }

   public static long jvget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getLongVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }

   public static void aput(long value, long addr) {
      MappedObjectUnsafe.INSTANCE.putAddress(addr, value);
   }

   public static void aput(MappedObject mapped, long value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putAddress(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static long aget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getAddress(addr);
   }

   public static long aget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getAddress(mapped.viewAddress + (long)fieldOffset);
   }

   public static void dput(double value, long addr) {
      MappedObjectUnsafe.INSTANCE.putDouble(addr, value);
   }

   public static void dput(MappedObject mapped, double value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putDouble(mapped.viewAddress + (long)fieldOffset, value);
   }

   public static double dget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getDouble(addr);
   }

   public static double dget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getDouble(mapped.viewAddress + (long)fieldOffset);
   }

   public static void dvput(double value, long addr) {
      MappedObjectUnsafe.INSTANCE.putDoubleVolatile((Object)null, addr, value);
   }

   public static void dvput(MappedObject mapped, double value, int fieldOffset) {
      MappedObjectUnsafe.INSTANCE.putDoubleVolatile((Object)null, mapped.viewAddress + (long)fieldOffset, value);
   }

   public static double dvget(long addr) {
      return MappedObjectUnsafe.INSTANCE.getDoubleVolatile((Object)null, addr);
   }

   public static double dvget(MappedObject mapped, int fieldOffset) {
      return MappedObjectUnsafe.INSTANCE.getDoubleVolatile((Object)null, mapped.viewAddress + (long)fieldOffset);
   }
}
