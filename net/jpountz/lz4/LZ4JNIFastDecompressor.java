package net.jpountz.lz4;

import java.nio.ByteBuffer;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4JNI;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.SafeUtils;

final class LZ4JNIFastDecompressor extends LZ4FastDecompressor {
   public static final LZ4JNIFastDecompressor INSTANCE = new LZ4JNIFastDecompressor();
   private static LZ4FastDecompressor SAFE_INSTANCE;

   public final int decompress(byte[] src, int srcOff, byte[] dest, int destOff, int destLen) {
      SafeUtils.checkRange(src, srcOff);
      SafeUtils.checkRange(dest, destOff, destLen);
      int result = LZ4JNI.LZ4_decompress_fast(src, (ByteBuffer)null, srcOff, dest, (ByteBuffer)null, destOff, destLen);
      if(result < 0) {
         throw new LZ4Exception("Error decoding offset " + (srcOff - result) + " of input buffer");
      } else {
         return result;
      }
   }

   public int decompress(ByteBuffer src, int srcOff, ByteBuffer dest, int destOff, int destLen) {
      ByteBufferUtils.checkNotReadOnly(dest);
      ByteBufferUtils.checkRange(src, srcOff);
      ByteBufferUtils.checkRange(dest, destOff, destLen);
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
         int result = LZ4JNI.LZ4_decompress_fast(srcArr, srcBuf, srcOff, destArr, destBuf, destOff, destLen);
         if(result < 0) {
            throw new LZ4Exception("Error decoding offset " + (srcOff - result) + " of input buffer");
         } else {
            return result;
         }
      } else {
         LZ4FastDecompressor safeInstance = SAFE_INSTANCE;
         if(safeInstance == null) {
            safeInstance = SAFE_INSTANCE = LZ4Factory.safeInstance().fastDecompressor();
         }

         return safeInstance.decompress(src, srcOff, dest, destOff, destLen);
      }
   }
}
