package net.jpountz.lz4;

import java.nio.ByteBuffer;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4JNI;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.SafeUtils;

final class LZ4JNICompressor extends LZ4Compressor {
   public static final LZ4Compressor INSTANCE = new LZ4JNICompressor();
   private static LZ4Compressor SAFE_INSTANCE;

   public int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int maxDestLen) {
      SafeUtils.checkRange(src, srcOff, srcLen);
      SafeUtils.checkRange(dest, destOff, maxDestLen);
      int result = LZ4JNI.LZ4_compress_limitedOutput(src, (ByteBuffer)null, srcOff, srcLen, dest, (ByteBuffer)null, destOff, maxDestLen);
      if(result <= 0) {
         throw new LZ4Exception("maxDestLen is too small");
      } else {
         return result;
      }
   }

   public int compress(ByteBuffer src, int srcOff, int srcLen, ByteBuffer dest, int destOff, int maxDestLen) {
      ByteBufferUtils.checkNotReadOnly(dest);
      ByteBufferUtils.checkRange(src, srcOff, srcLen);
      ByteBufferUtils.checkRange(dest, destOff, maxDestLen);
      byte[] srcArr = null;
      byte[] destArr = null;
      ByteBuffer srcBuf = null;
      ByteBuffer destBuf = null;
      if(src.hasArray()) {
         srcArr = src.array();
      } else if(src.isDirect()) {
         srcBuf = src;
      }

      if(dest.hasArray()) {
         destArr = dest.array();
      } else if(dest.isDirect()) {
         destBuf = dest;
      }

      if((srcArr != null || srcBuf != null) && (destArr != null || destBuf != null)) {
         int result = LZ4JNI.LZ4_compress_limitedOutput(srcArr, srcBuf, srcOff, srcLen, destArr, destBuf, destOff, maxDestLen);
         if(result <= 0) {
            throw new LZ4Exception("maxDestLen is too small");
         } else {
            return result;
         }
      } else {
         LZ4Compressor safeInstance = SAFE_INSTANCE;
         if(safeInstance == null) {
            safeInstance = SAFE_INSTANCE = LZ4Factory.safeInstance().fastCompressor();
         }

         return safeInstance.compress(src, srcOff, srcLen, dest, destOff, maxDestLen);
      }
   }
}
