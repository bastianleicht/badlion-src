package org.lwjgl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.PointerBuffer;

public final class BufferUtils {
   public static ByteBuffer createByteBuffer(int size) {
      return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
   }

   public static ShortBuffer createShortBuffer(int size) {
      return createByteBuffer(size << 1).asShortBuffer();
   }

   public static CharBuffer createCharBuffer(int size) {
      return createByteBuffer(size << 1).asCharBuffer();
   }

   public static IntBuffer createIntBuffer(int size) {
      return createByteBuffer(size << 2).asIntBuffer();
   }

   public static LongBuffer createLongBuffer(int size) {
      return createByteBuffer(size << 3).asLongBuffer();
   }

   public static FloatBuffer createFloatBuffer(int size) {
      return createByteBuffer(size << 2).asFloatBuffer();
   }

   public static DoubleBuffer createDoubleBuffer(int size) {
      return createByteBuffer(size << 3).asDoubleBuffer();
   }

   public static PointerBuffer createPointerBuffer(int size) {
      return PointerBuffer.allocateDirect(size);
   }

   public static int getElementSizeExponent(Buffer buf) {
      if(buf instanceof ByteBuffer) {
         return 0;
      } else if(!(buf instanceof ShortBuffer) && !(buf instanceof CharBuffer)) {
         if(!(buf instanceof FloatBuffer) && !(buf instanceof IntBuffer)) {
            if(!(buf instanceof LongBuffer) && !(buf instanceof DoubleBuffer)) {
               throw new IllegalStateException("Unsupported buffer type: " + buf);
            } else {
               return 3;
            }
         } else {
            return 2;
         }
      } else {
         return 1;
      }
   }

   public static int getOffset(Buffer buffer) {
      return buffer.position() << getElementSizeExponent(buffer);
   }

   public static void zeroBuffer(ByteBuffer b) {
      zeroBuffer0(b, (long)b.position(), (long)b.remaining());
   }

   public static void zeroBuffer(ShortBuffer b) {
      zeroBuffer0(b, (long)b.position() * 2L, (long)b.remaining() * 2L);
   }

   public static void zeroBuffer(CharBuffer b) {
      zeroBuffer0(b, (long)b.position() * 2L, (long)b.remaining() * 2L);
   }

   public static void zeroBuffer(IntBuffer b) {
      zeroBuffer0(b, (long)b.position() * 4L, (long)b.remaining() * 4L);
   }

   public static void zeroBuffer(FloatBuffer b) {
      zeroBuffer0(b, (long)b.position() * 4L, (long)b.remaining() * 4L);
   }

   public static void zeroBuffer(LongBuffer b) {
      zeroBuffer0(b, (long)b.position() * 8L, (long)b.remaining() * 8L);
   }

   public static void zeroBuffer(DoubleBuffer b) {
      zeroBuffer0(b, (long)b.position() * 8L, (long)b.remaining() * 8L);
   }

   private static native void zeroBuffer0(Buffer var0, long var1, long var3);

   static native long getBufferAddress(Buffer var0);
}
