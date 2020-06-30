package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.JZlib.WrapperType;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.ZlibWrapper;

final class ZlibUtil {
   static void fail(Inflater z, String message, int resultCode) {
      throw inflaterException(z, message, resultCode);
   }

   static void fail(Deflater z, String message, int resultCode) {
      throw deflaterException(z, message, resultCode);
   }

   static DecompressionException inflaterException(Inflater z, String message, int resultCode) {
      return new DecompressionException(message + " (" + resultCode + ')' + (z.msg != null?": " + z.msg:""));
   }

   static CompressionException deflaterException(Deflater z, String message, int resultCode) {
      return new CompressionException(message + " (" + resultCode + ')' + (z.msg != null?": " + z.msg:""));
   }

   static WrapperType convertWrapperType(ZlibWrapper wrapper) {
      WrapperType convertedWrapperType;
      switch(wrapper) {
      case NONE:
         convertedWrapperType = JZlib.W_NONE;
         break;
      case ZLIB:
         convertedWrapperType = JZlib.W_ZLIB;
         break;
      case GZIP:
         convertedWrapperType = JZlib.W_GZIP;
         break;
      case ZLIB_OR_NONE:
         convertedWrapperType = JZlib.W_ANY;
         break;
      default:
         throw new Error();
      }

      return convertedWrapperType;
   }

   static int wrapperOverhead(ZlibWrapper wrapper) {
      int overhead;
      switch(wrapper) {
      case NONE:
         overhead = 0;
         break;
      case ZLIB:
      case ZLIB_OR_NONE:
         overhead = 2;
         break;
      case GZIP:
         overhead = 10;
         break;
      default:
         throw new Error();
      }

      return overhead;
   }
}
