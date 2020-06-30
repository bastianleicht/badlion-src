package org.lwjgl;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;

public final class MemoryUtil {
   private static final Charset ascii = Charset.forName("ISO-8859-1");
   private static final Charset utf8 = Charset.forName("UTF-8");
   private static final Charset utf16 = Charset.forName("UTF-16LE");
   private static final MemoryUtil.Accessor memUtil;

   public static long getAddress0(Buffer buffer) {
      return memUtil.getAddress(buffer);
   }

   public static long getAddress0Safe(Buffer buffer) {
      return buffer == null?0L:memUtil.getAddress(buffer);
   }

   public static long getAddress0(PointerBuffer buffer) {
      return memUtil.getAddress(buffer.getBuffer());
   }

   public static long getAddress0Safe(PointerBuffer buffer) {
      return buffer == null?0L:memUtil.getAddress(buffer.getBuffer());
   }

   public static long getAddress(ByteBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(ByteBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)position;
   }

   public static long getAddress(ShortBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(ShortBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 1);
   }

   public static long getAddress(CharBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(CharBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 1);
   }

   public static long getAddress(IntBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(IntBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 2);
   }

   public static long getAddress(FloatBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(FloatBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 2);
   }

   public static long getAddress(LongBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(LongBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 3);
   }

   public static long getAddress(DoubleBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(DoubleBuffer buffer, int position) {
      return getAddress0((Buffer)buffer) + (long)(position << 3);
   }

   public static long getAddress(PointerBuffer buffer) {
      return getAddress(buffer, buffer.position());
   }

   public static long getAddress(PointerBuffer buffer, int position) {
      return getAddress0(buffer) + (long)(position * PointerBuffer.getPointerSize());
   }

   public static long getAddressSafe(ByteBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(ByteBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(ShortBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(ShortBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(CharBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(CharBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(IntBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(IntBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(FloatBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(FloatBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(LongBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(LongBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(DoubleBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(DoubleBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static long getAddressSafe(PointerBuffer buffer) {
      return buffer == null?0L:getAddress(buffer);
   }

   public static long getAddressSafe(PointerBuffer buffer, int position) {
      return buffer == null?0L:getAddress(buffer, position);
   }

   public static ByteBuffer encodeASCII(CharSequence text) {
      return encode(text, ascii);
   }

   public static ByteBuffer encodeUTF8(CharSequence text) {
      return encode(text, utf8);
   }

   public static ByteBuffer encodeUTF16(CharSequence text) {
      return encode(text, utf16);
   }

   private static ByteBuffer encode(CharSequence text, Charset charset) {
      return text == null?null:encode(CharBuffer.wrap(new MemoryUtil.CharSequenceNT(text)), charset);
   }

   private static ByteBuffer encode(CharBuffer in, Charset charset) {
      CharsetEncoder encoder = charset.newEncoder();
      int n = (int)((float)in.remaining() * encoder.averageBytesPerChar());
      ByteBuffer out = BufferUtils.createByteBuffer(n);
      if(n == 0 && in.remaining() == 0) {
         return out;
      } else {
         encoder.reset();

         while(true) {
            CoderResult cr = in.hasRemaining()?encoder.encode(in, out, true):CoderResult.UNDERFLOW;
            if(cr.isUnderflow()) {
               cr = encoder.flush(out);
            }

            if(cr.isUnderflow()) {
               out.flip();
               return out;
            }

            if(cr.isOverflow()) {
               n = 2 * n + 1;
               ByteBuffer o = BufferUtils.createByteBuffer(n);
               out.flip();
               o.put(out);
               out = o;
            } else {
               try {
                  cr.throwException();
               } catch (CharacterCodingException var7) {
                  throw new RuntimeException(var7);
               }
            }
         }
      }
   }

   public static String decodeASCII(ByteBuffer buffer) {
      return decode(buffer, ascii);
   }

   public static String decodeUTF8(ByteBuffer buffer) {
      return decode(buffer, utf8);
   }

   public static String decodeUTF16(ByteBuffer buffer) {
      return decode(buffer, utf16);
   }

   private static String decode(ByteBuffer buffer, Charset charset) {
      return buffer == null?null:decodeImpl(buffer, charset);
   }

   private static String decodeImpl(ByteBuffer in, Charset charset) {
      CharsetDecoder decoder = charset.newDecoder();
      int n = (int)((float)in.remaining() * decoder.averageCharsPerByte());
      CharBuffer out = BufferUtils.createCharBuffer(n);
      if(n == 0 && in.remaining() == 0) {
         return "";
      } else {
         decoder.reset();

         while(true) {
            CoderResult cr = in.hasRemaining()?decoder.decode(in, out, true):CoderResult.UNDERFLOW;
            if(cr.isUnderflow()) {
               cr = decoder.flush(out);
            }

            if(cr.isUnderflow()) {
               out.flip();
               return out.toString();
            }

            if(cr.isOverflow()) {
               n = 2 * n + 1;
               CharBuffer o = BufferUtils.createCharBuffer(n);
               out.flip();
               o.put(out);
               out = o;
            } else {
               try {
                  cr.throwException();
               } catch (CharacterCodingException var7) {
                  throw new RuntimeException(var7);
               }
            }
         }
      }
   }

   private static MemoryUtil.Accessor loadAccessor(String className) throws Exception {
      return (MemoryUtil.Accessor)Class.forName(className).newInstance();
   }

   static Field getAddressField() throws NoSuchFieldException {
      return getDeclaredFieldRecursive(ByteBuffer.class, "address");
   }

   private static Field getDeclaredFieldRecursive(Class root, String fieldName) throws NoSuchFieldException {
      Class<?> type = root;

      while(true) {
         try {
            return type.getDeclaredField(fieldName);
         } catch (NoSuchFieldException var4) {
            type = type.getSuperclass();
            if(type == null) {
               throw new NoSuchFieldException(fieldName + " does not exist in " + root.getSimpleName() + " or any of its superclasses.");
            }
         }
      }
   }

   static {
      MemoryUtil.Accessor util;
      try {
         util = loadAccessor("org.lwjgl.MemoryUtilSun$AccessorUnsafe");
      } catch (Exception var6) {
         try {
            util = loadAccessor("org.lwjgl.MemoryUtilSun$AccessorReflectFast");
         } catch (Exception var5) {
            try {
               util = new MemoryUtil.AccessorReflect();
            } catch (Exception var4) {
               LWJGLUtil.log("Unsupported JVM detected, this will likely result in low performance. Please inform LWJGL developers.");
               util = new MemoryUtil.AccessorJNI();
            }
         }
      }

      LWJGLUtil.log("MemoryUtil Accessor: " + util.getClass().getSimpleName());
      memUtil = util;
   }

   interface Accessor {
      long getAddress(Buffer var1);
   }

   private static class AccessorJNI implements MemoryUtil.Accessor {
      private AccessorJNI() {
      }

      public long getAddress(Buffer buffer) {
         return BufferUtils.getBufferAddress(buffer);
      }
   }

   private static class AccessorReflect implements MemoryUtil.Accessor {
      private final Field address;

      AccessorReflect() {
         try {
            this.address = MemoryUtil.getAddressField();
         } catch (NoSuchFieldException var2) {
            throw new UnsupportedOperationException(var2);
         }

         this.address.setAccessible(true);
      }

      public long getAddress(Buffer buffer) {
         try {
            return this.address.getLong(buffer);
         } catch (IllegalAccessException var3) {
            return 0L;
         }
      }
   }

   private static class CharSequenceNT implements CharSequence {
      final CharSequence source;

      CharSequenceNT(CharSequence source) {
         this.source = source;
      }

      public int length() {
         return this.source.length() + 1;
      }

      public char charAt(int index) {
         return index == this.source.length()?'\u0000':this.source.charAt(index);
      }

      public CharSequence subSequence(int start, int end) {
         return new MemoryUtil.CharSequenceNT(this.source.subSequence(start, Math.min(end, this.source.length())));
      }
   }
}
