package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.spdy.SpdyHeaderBlockJZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeaderBlockZlibEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.internal.PlatformDependent;

abstract class SpdyHeaderBlockEncoder {
   static SpdyHeaderBlockEncoder newInstance(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
      return (SpdyHeaderBlockEncoder)(PlatformDependent.javaVersion() >= 7?new SpdyHeaderBlockZlibEncoder(version, compressionLevel):new SpdyHeaderBlockJZlibEncoder(version, compressionLevel, windowBits, memLevel));
   }

   abstract ByteBuf encode(SpdyHeadersFrame var1) throws Exception;

   abstract void end();
}
