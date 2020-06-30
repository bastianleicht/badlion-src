package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Locale;

public final class ByteBufUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
   private static final char[] HEXDUMP_TABLE = new char[1024];
   static final ByteBufAllocator DEFAULT_ALLOCATOR;
   private static final int THREAD_LOCAL_BUFFER_SIZE;

   public static String hexDump(ByteBuf buffer) {
      return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
   }

   public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
      if(length < 0) {
         throw new IllegalArgumentException("length: " + length);
      } else if(length == 0) {
         return "";
      } else {
         int endIndex = fromIndex + length;
         char[] buf = new char[length << 1];
         int srcIdx = fromIndex;

         for(int dstIdx = 0; srcIdx < endIndex; dstIdx += 2) {
            System.arraycopy(HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);
            ++srcIdx;
         }

         return new String(buf);
      }
   }

   public static int hashCode(ByteBuf buffer) {
      int aLen = buffer.readableBytes();
      int intCount = aLen >>> 2;
      int byteCount = aLen & 3;
      int hashCode = 1;
      int arrayIndex = buffer.readerIndex();
      if(buffer.order() == ByteOrder.BIG_ENDIAN) {
         for(int i = intCount; i > 0; --i) {
            hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
            arrayIndex += 4;
         }
      } else {
         for(int i = intCount; i > 0; --i) {
            hashCode = 31 * hashCode + swapInt(buffer.getInt(arrayIndex));
            arrayIndex += 4;
         }
      }

      for(int i = byteCount; i > 0; --i) {
         hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
      }

      if(hashCode == 0) {
         hashCode = 1;
      }

      return hashCode;
   }

   public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
      int aLen = bufferA.readableBytes();
      if(aLen != bufferB.readableBytes()) {
         return false;
      } else {
         int longCount = aLen >>> 3;
         int byteCount = aLen & 7;
         int aIndex = bufferA.readerIndex();
         int bIndex = bufferB.readerIndex();
         if(bufferA.order() == bufferB.order()) {
            for(int i = longCount; i > 0; --i) {
               if(bufferA.getLong(aIndex) != bufferB.getLong(bIndex)) {
                  return false;
               }

               aIndex += 8;
               bIndex += 8;
            }
         } else {
            for(int i = longCount; i > 0; --i) {
               if(bufferA.getLong(aIndex) != swapLong(bufferB.getLong(bIndex))) {
                  return false;
               }

               aIndex += 8;
               bIndex += 8;
            }
         }

         for(int i = byteCount; i > 0; --i) {
            if(bufferA.getByte(aIndex) != bufferB.getByte(bIndex)) {
               return false;
            }

            ++aIndex;
            ++bIndex;
         }

         return true;
      }
   }

   public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
      int aLen = bufferA.readableBytes();
      int bLen = bufferB.readableBytes();
      int minLength = Math.min(aLen, bLen);
      int uintCount = minLength >>> 2;
      int byteCount = minLength & 3;
      int aIndex = bufferA.readerIndex();
      int bIndex = bufferB.readerIndex();
      if(bufferA.order() == bufferB.order()) {
         for(int i = uintCount; i > 0; --i) {
            long va = bufferA.getUnsignedInt(aIndex);
            long vb = bufferB.getUnsignedInt(bIndex);
            if(va > vb) {
               return 1;
            }

            if(va < vb) {
               return -1;
            }

            aIndex += 4;
            bIndex += 4;
         }
      } else {
         for(int i = uintCount; i > 0; --i) {
            long va = bufferA.getUnsignedInt(aIndex);
            long vb = (long)swapInt(bufferB.getInt(bIndex)) & 4294967295L;
            if(va > vb) {
               return 1;
            }

            if(va < vb) {
               return -1;
            }

            aIndex += 4;
            bIndex += 4;
         }
      }

      for(int i = byteCount; i > 0; --i) {
         short va = bufferA.getUnsignedByte(aIndex);
         short vb = bufferB.getUnsignedByte(bIndex);
         if(va > vb) {
            return 1;
         }

         if(va < vb) {
            return -1;
         }

         ++aIndex;
         ++bIndex;
      }

      return aLen - bLen;
   }

   public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
      return fromIndex <= toIndex?firstIndexOf(buffer, fromIndex, toIndex, value):lastIndexOf(buffer, fromIndex, toIndex, value);
   }

   public static short swapShort(short value) {
      return Short.reverseBytes(value);
   }

   public static int swapMedium(int value) {
      int swapped = value << 16 & 16711680 | value & '\uff00' | value >>> 16 & 255;
      if((swapped & 8388608) != 0) {
         swapped |= -16777216;
      }

      return swapped;
   }

   public static int swapInt(int value) {
      return Integer.reverseBytes(value);
   }

   public static long swapLong(long value) {
      return Long.reverseBytes(value);
   }

   public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) {
      boolean release = true;
      ByteBuf dst = alloc.buffer(length);

      ByteBuf var5;
      try {
         buffer.readBytes(dst);
         release = false;
         var5 = dst;
      } finally {
         if(release) {
            dst.release();
         }

      }

      return var5;
   }

   private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
      fromIndex = Math.max(fromIndex, 0);
      if(fromIndex < toIndex && buffer.capacity() != 0) {
         for(int i = fromIndex; i < toIndex; ++i) {
            if(buffer.getByte(i) == value) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
      fromIndex = Math.min(fromIndex, buffer.capacity());
      if(fromIndex >= 0 && buffer.capacity() != 0) {
         for(int i = fromIndex - 1; i >= toIndex; --i) {
            if(buffer.getByte(i) == value) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
      return encodeString0(alloc, false, src, charset);
   }

   static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset) {
      CharsetEncoder encoder = CharsetUtil.getEncoder(charset);
      int length = (int)((double)src.remaining() * (double)encoder.maxBytesPerChar());
      boolean release = true;
      ByteBuf dst;
      if(enforceHeap) {
         dst = alloc.heapBuffer(length);
      } else {
         dst = alloc.buffer(length);
      }

      ByteBuf var11;
      try {
         ByteBuffer dstBuf = dst.internalNioBuffer(0, length);
         int pos = dstBuf.position();
         CoderResult cr = encoder.encode(src, dstBuf, true);
         if(!cr.isUnderflow()) {
            cr.throwException();
         }

         cr = encoder.flush(dstBuf);
         if(!cr.isUnderflow()) {
            cr.throwException();
         }

         dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
         release = false;
         var11 = dst;
      } catch (CharacterCodingException var15) {
         throw new IllegalStateException(var15);
      } finally {
         if(release) {
            dst.release();
         }

      }

      return var11;
   }

   static String decodeString(ByteBuffer src, Charset charset) {
      CharsetDecoder decoder = CharsetUtil.getDecoder(charset);
      CharBuffer dst = CharBuffer.allocate((int)((double)src.remaining() * (double)decoder.maxCharsPerByte()));

      try {
         CoderResult cr = decoder.decode(src, dst, true);
         if(!cr.isUnderflow()) {
            cr.throwException();
         }

         cr = decoder.flush(dst);
         if(!cr.isUnderflow()) {
            cr.throwException();
         }
      } catch (CharacterCodingException var5) {
         throw new IllegalStateException(var5);
      }

      return dst.flip().toString();
   }

   public static ByteBuf threadLocalDirectBuffer() {
      return (ByteBuf)(THREAD_LOCAL_BUFFER_SIZE <= 0?null:(PlatformDependent.hasUnsafe()?ByteBufUtil.ThreadLocalUnsafeDirectByteBuf.newInstance():ByteBufUtil.ThreadLocalDirectByteBuf.newInstance()));
   }

   static {
      char[] DIGITS = "0123456789abcdef".toCharArray();

      for(int i = 0; i < 256; ++i) {
         HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 15];
         HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 15];
      }

      String allocType = SystemPropertyUtil.get("io.netty.allocator.type", "unpooled").toLowerCase(Locale.US).trim();
      ByteBufAllocator alloc;
      if("unpooled".equals(allocType)) {
         alloc = UnpooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
      } else if("pooled".equals(allocType)) {
         alloc = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
      } else {
         alloc = UnpooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: unpooled (unknown: {})", (Object)allocType);
      }

      DEFAULT_ALLOCATOR = alloc;
      THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 65536);
      logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", (Object)Integer.valueOf(THREAD_LOCAL_BUFFER_SIZE));
   }

   static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf {
      private static final Recycler RECYCLER = new Recycler() {
         protected ByteBufUtil.ThreadLocalDirectByteBuf newObject(Recycler.Handle handle) {
            return new ByteBufUtil.ThreadLocalDirectByteBuf(handle);
         }
      };
      private final Recycler.Handle handle;

      static ByteBufUtil.ThreadLocalDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalDirectByteBuf buf = (ByteBufUtil.ThreadLocalDirectByteBuf)RECYCLER.get();
         buf.setRefCnt(1);
         return buf;
      }

      private ThreadLocalDirectByteBuf(Recycler.Handle handle) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = handle;
      }

      protected void deallocate() {
         if(this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            RECYCLER.recycle(this, this.handle);
         }

      }
   }

   static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      private static final Recycler RECYCLER = new Recycler() {
         protected ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newObject(Recycler.Handle handle) {
            return new ByteBufUtil.ThreadLocalUnsafeDirectByteBuf(handle);
         }
      };
      private final Recycler.Handle handle;

      static ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalUnsafeDirectByteBuf buf = (ByteBufUtil.ThreadLocalUnsafeDirectByteBuf)RECYCLER.get();
         buf.setRefCnt(1);
         return buf;
      }

      private ThreadLocalUnsafeDirectByteBuf(Recycler.Handle handle) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = handle;
      }

      protected void deallocate() {
         if(this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            RECYCLER.recycle(this, this.handle);
         }

      }
   }
}
