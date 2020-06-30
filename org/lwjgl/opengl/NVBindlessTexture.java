package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVBindlessTexture {
   public static long glGetTextureHandleNV(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureHandleNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetTextureHandleNV(texture, function_pointer);
      return __result;
   }

   static native long nglGetTextureHandleNV(int var0, long var1);

   public static long glGetTextureSamplerHandleNV(int texture, int sampler) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureSamplerHandleNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetTextureSamplerHandleNV(texture, sampler, function_pointer);
      return __result;
   }

   static native long nglGetTextureSamplerHandleNV(int var0, int var1, long var2);

   public static void glMakeTextureHandleResidentNV(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeTextureHandleResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeTextureHandleResidentNV(handle, function_pointer);
   }

   static native void nglMakeTextureHandleResidentNV(long var0, long var2);

   public static void glMakeTextureHandleNonResidentNV(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeTextureHandleNonResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeTextureHandleNonResidentNV(handle, function_pointer);
   }

   static native void nglMakeTextureHandleNonResidentNV(long var0, long var2);

   public static long glGetImageHandleNV(int texture, int level, boolean layered, int layer, int format) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetImageHandleNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetImageHandleNV(texture, level, layered, layer, format, function_pointer);
      return __result;
   }

   static native long nglGetImageHandleNV(int var0, int var1, boolean var2, int var3, int var4, long var5);

   public static void glMakeImageHandleResidentNV(long handle, int access) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeImageHandleResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeImageHandleResidentNV(handle, access, function_pointer);
   }

   static native void nglMakeImageHandleResidentNV(long var0, int var2, long var3);

   public static void glMakeImageHandleNonResidentNV(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeImageHandleNonResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeImageHandleNonResidentNV(handle, function_pointer);
   }

   static native void nglMakeImageHandleNonResidentNV(long var0, long var2);

   public static void glUniformHandleui64NV(int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformHandleui64NV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniformHandleui64NV(location, value, function_pointer);
   }

   static native void nglUniformHandleui64NV(int var0, long var1, long var3);

   public static void glUniformHandleuNV(int location, LongBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformHandleui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglUniformHandleui64vNV(location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglUniformHandleui64vNV(int var0, int var1, long var2, long var4);

   public static void glProgramUniformHandleui64NV(int program, int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformHandleui64NV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniformHandleui64NV(program, location, value, function_pointer);
   }

   static native void nglProgramUniformHandleui64NV(int var0, int var1, long var2, long var4);

   public static void glProgramUniformHandleuNV(int program, int location, LongBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformHandleui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglProgramUniformHandleui64vNV(program, location, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglProgramUniformHandleui64vNV(int var0, int var1, int var2, long var3, long var5);

   public static boolean glIsTextureHandleResidentNV(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsTextureHandleResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsTextureHandleResidentNV(handle, function_pointer);
      return __result;
   }

   static native boolean nglIsTextureHandleResidentNV(long var0, long var2);

   public static boolean glIsImageHandleResidentNV(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsImageHandleResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsImageHandleResidentNV(handle, function_pointer);
      return __result;
   }

   static native boolean nglIsImageHandleResidentNV(long var0, long var2);
}
