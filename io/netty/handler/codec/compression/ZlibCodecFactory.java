package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
   private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", true);

   public static ZlibEncoder newZlibEncoder(int compressionLevel) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(compressionLevel):new JdkZlibEncoder(compressionLevel));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(wrapper):new JdkZlibEncoder(wrapper));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(wrapper, compressionLevel):new JdkZlibEncoder(wrapper, compressionLevel));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel):new JdkZlibEncoder(wrapper, compressionLevel));
   }

   public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(dictionary):new JdkZlibEncoder(dictionary));
   }

   public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(compressionLevel, dictionary):new JdkZlibEncoder(compressionLevel, dictionary));
   }

   public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() < 7?new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary):new JdkZlibEncoder(compressionLevel, dictionary));
   }

   public static ZlibDecoder newZlibDecoder() {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder?new JdkZlibDecoder():new JZlibDecoder());
   }

   public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder?new JdkZlibDecoder(wrapper):new JZlibDecoder(wrapper));
   }

   public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder?new JdkZlibDecoder(dictionary):new JZlibDecoder(dictionary));
   }

   static {
      logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object)Boolean.valueOf(noJdkZlibDecoder));
   }
}
