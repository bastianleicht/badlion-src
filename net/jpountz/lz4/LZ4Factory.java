package net.jpountz.lz4;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Decompressor;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.lz4.LZ4UnknownSizeDecompressor;
import net.jpountz.util.Native;
import net.jpountz.util.Utils;

public final class LZ4Factory {
   private static LZ4Factory NATIVE_INSTANCE;
   private static LZ4Factory JAVA_UNSAFE_INSTANCE;
   private static LZ4Factory JAVA_SAFE_INSTANCE;
   private final String impl;
   private final LZ4Compressor fastCompressor;
   private final LZ4Compressor highCompressor;
   private final LZ4FastDecompressor fastDecompressor;
   private final LZ4SafeDecompressor safeDecompressor;
   private final LZ4Compressor[] highCompressors = new LZ4Compressor[18];

   private static LZ4Factory instance(String impl) {
      try {
         return new LZ4Factory(impl);
      } catch (Exception var2) {
         throw new AssertionError(var2);
      }
   }

   public static synchronized LZ4Factory nativeInstance() {
      if(NATIVE_INSTANCE == null) {
         NATIVE_INSTANCE = instance("JNI");
      }

      return NATIVE_INSTANCE;
   }

   public static synchronized LZ4Factory safeInstance() {
      if(JAVA_SAFE_INSTANCE == null) {
         JAVA_SAFE_INSTANCE = instance("JavaSafe");
      }

      return JAVA_SAFE_INSTANCE;
   }

   public static synchronized LZ4Factory unsafeInstance() {
      if(JAVA_UNSAFE_INSTANCE == null) {
         JAVA_UNSAFE_INSTANCE = instance("JavaUnsafe");
      }

      return JAVA_UNSAFE_INSTANCE;
   }

   public static LZ4Factory fastestJavaInstance() {
      if(Utils.isUnalignedAccessAllowed()) {
         try {
            return unsafeInstance();
         } catch (Throwable var1) {
            return safeInstance();
         }
      } else {
         return safeInstance();
      }
   }

   public static LZ4Factory fastestInstance() {
      if(!Native.isLoaded() && Native.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
         return fastestJavaInstance();
      } else {
         try {
            return nativeInstance();
         } catch (Throwable var1) {
            return fastestJavaInstance();
         }
      }
   }

   private static Object classInstance(String cls) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
      Class<?> c = LZ4Factory.class.getClassLoader().loadClass(cls);
      Field f = c.getField("INSTANCE");
      return f.get((Object)null);
   }

   private LZ4Factory(String impl) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
      this.impl = impl;
      this.fastCompressor = (LZ4Compressor)classInstance("net.jpountz.lz4.LZ4" + impl + "Compressor");
      this.highCompressor = (LZ4Compressor)classInstance("net.jpountz.lz4.LZ4HC" + impl + "Compressor");
      this.fastDecompressor = (LZ4FastDecompressor)classInstance("net.jpountz.lz4.LZ4" + impl + "FastDecompressor");
      this.safeDecompressor = (LZ4SafeDecompressor)classInstance("net.jpountz.lz4.LZ4" + impl + "SafeDecompressor");
      Constructor<? extends LZ4Compressor> highConstructor = this.highCompressor.getClass().getDeclaredConstructor(new Class[]{Integer.TYPE});
      this.highCompressors[9] = this.highCompressor;

      for(int level = 1; level <= 17; ++level) {
         if(level != 9) {
            this.highCompressors[level] = (LZ4Compressor)highConstructor.newInstance(new Object[]{Integer.valueOf(level)});
         }
      }

      byte[] original = new byte[]{(byte)97, (byte)98, (byte)99, (byte)100, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106};

      for(LZ4Compressor compressor : Arrays.asList(new LZ4Compressor[]{this.fastCompressor, this.highCompressor})) {
         int maxCompressedLength = compressor.maxCompressedLength(original.length);
         byte[] compressed = new byte[maxCompressedLength];
         int compressedLength = compressor.compress((byte[])original, 0, original.length, (byte[])compressed, 0, maxCompressedLength);
         byte[] restored = new byte[original.length];
         this.fastDecompressor.decompress((byte[])compressed, 0, (byte[])restored, 0, original.length);
         if(!Arrays.equals(original, restored)) {
            throw new AssertionError();
         }

         Arrays.fill(restored, (byte)0);
         int decompressedLength = this.safeDecompressor.decompress(compressed, 0, compressedLength, restored, 0);
         if(decompressedLength != original.length || !Arrays.equals(original, restored)) {
            throw new AssertionError();
         }
      }

   }

   public LZ4Compressor fastCompressor() {
      return this.fastCompressor;
   }

   public LZ4Compressor highCompressor() {
      return this.highCompressor;
   }

   public LZ4Compressor highCompressor(int compressionLevel) {
      if(compressionLevel > 17) {
         compressionLevel = 17;
      } else if(compressionLevel < 1) {
         compressionLevel = 9;
      }

      return this.highCompressors[compressionLevel];
   }

   public LZ4FastDecompressor fastDecompressor() {
      return this.fastDecompressor;
   }

   public LZ4SafeDecompressor safeDecompressor() {
      return this.safeDecompressor;
   }

   /** @deprecated */
   public LZ4UnknownSizeDecompressor unknownSizeDecompressor() {
      return this.safeDecompressor();
   }

   /** @deprecated */
   public LZ4Decompressor decompressor() {
      return this.fastDecompressor();
   }

   public static void main(String[] args) {
      System.out.println("Fastest instance is " + fastestInstance());
      System.out.println("Fastest Java instance is " + fastestJavaInstance());
   }

   public String toString() {
      return this.getClass().getSimpleName() + ":" + this.impl;
   }
}
