package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.StringUtil;

public class HttpContentCompressor extends HttpContentEncoder {
   private final int compressionLevel;
   private final int windowBits;
   private final int memLevel;

   public HttpContentCompressor() {
      this(6);
   }

   public HttpContentCompressor(int compressionLevel) {
      this(compressionLevel, 15, 8);
   }

   public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
      if(compressionLevel >= 0 && compressionLevel <= 9) {
         if(windowBits >= 9 && windowBits <= 15) {
            if(memLevel >= 1 && memLevel <= 9) {
               this.compressionLevel = compressionLevel;
               this.windowBits = windowBits;
               this.memLevel = memLevel;
            } else {
               throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   protected HttpContentEncoder.Result beginEncode(HttpResponse headers, String acceptEncoding) throws Exception {
      String contentEncoding = headers.headers().get("Content-Encoding");
      if(contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding)) {
         return null;
      } else {
         ZlibWrapper wrapper = this.determineWrapper(acceptEncoding);
         if(wrapper == null) {
            return null;
         } else {
            String targetContentEncoding;
            switch(wrapper) {
            case GZIP:
               targetContentEncoding = "gzip";
               break;
            case ZLIB:
               targetContentEncoding = "deflate";
               break;
            default:
               throw new Error();
            }

            return new HttpContentEncoder.Result(targetContentEncoding, new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel)}));
         }
      }
   }

   protected ZlibWrapper determineWrapper(String acceptEncoding) {
      float starQ = -1.0F;
      float gzipQ = -1.0F;
      float deflateQ = -1.0F;

      for(String encoding : StringUtil.split(acceptEncoding, ',')) {
         float q = 1.0F;
         int equalsPos = encoding.indexOf(61);
         if(equalsPos != -1) {
            try {
               q = Float.valueOf(encoding.substring(equalsPos + 1)).floatValue();
            } catch (NumberFormatException var12) {
               q = 0.0F;
            }
         }

         if(encoding.contains("*")) {
            starQ = q;
         } else if(encoding.contains("gzip") && q > gzipQ) {
            gzipQ = q;
         } else if(encoding.contains("deflate") && q > deflateQ) {
            deflateQ = q;
         }
      }

      if(gzipQ <= 0.0F && deflateQ <= 0.0F) {
         if(starQ > 0.0F) {
            if(gzipQ == -1.0F) {
               return ZlibWrapper.GZIP;
            }

            if(deflateQ == -1.0F) {
               return ZlibWrapper.ZLIB;
            }
         }

         return null;
      } else if(gzipQ >= deflateQ) {
         return ZlibWrapper.GZIP;
      } else {
         return ZlibWrapper.ZLIB;
      }
   }
}
