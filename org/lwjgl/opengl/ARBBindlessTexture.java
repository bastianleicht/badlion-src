package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBBindlessTexture {
   public static final int GL_UNSIGNED_INT64_ARB = 5135;

   public static long glGetTextureHandleARB(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureHandleARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetTextureHandleARB(texture, function_pointer);
      return __result;
   }

   static native long nglGetTextureHandleARB(int var0, long var1);

   public static long glGetTextureSamplerHandleARB(int texture, int sampler) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureSamplerHandleARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetTextureSamplerHandleARB(texture, sampler, function_pointer);
      return __result;
   }

   static native long nglGetTextureSamplerHandleARB(int var0, int var1, long var2);

   public static void glMakeTextureHandleResidentARB(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeTextureHandleResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeTextureHandleResidentARB(handle, function_pointer);
   }

   static native void nglMakeTextureHandleResidentARB(long var0, long var2);

   public static void glMakeTextureHandleNonResidentARB(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeTextureHandleNonResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeTextureHandleNonResidentARB(handle, function_pointer);
   }

   static native void nglMakeTextureHandleNonResidentARB(long var0, long var2);

   public static long glGetImageHandleARB(int texture, int level, boolean layered, int layer, int format) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetImageHandleARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetImageHandleARB(texture, level, layered, layer, format, function_pointer);
      return __result;
   }

   static native long nglGetImageHandleARB(int var0, int var1, boolean var2, int var3, int var4, long var5);

   public static void glMakeImageHandleResidentARB(long handle, int access) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeImageHandleResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeImageHandleResidentARB(handle, access, function_pointer);
   }

   static native void nglMakeImageHandleResidentARB(long var0, int var2, long var3);

   public static void glMakeImageHandleNonResidentARB(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeImageHandleNonResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeImageHandleNonResidentARB(handle, function_pointer);
   }

   static native void nglMakeImageHandleNonResidentARB(long var0, long var2);

   public static void glUniformHandleui64ARB(int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformHandleui64ARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniformHandleui64ARB(location, value, function_pointer);
   }

   static native void nglUniformHandleui64ARB(int var0, long var1, long var3);

   public static void glUniformHandleuARB(int location, LongBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformHandleui64vARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglUniformHandleui64vARB(location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglUniformHandleui64vARB(int var0, int var1, long var2, long var4);

   public static void glProgramUniformHandleui64ARB(int program, int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformHandleui64ARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniformHandleui64ARB(program, location, value, function_pointer);
   }

   static native void nglProgramUniformHandleui64ARB(int var0, int var1, long var2, long var4);

   public static void glProgramUniformHandleuARB(int program, int location, LongBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformHandleui64vARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglProgramUniformHandleui64vARB(program, location, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglProgramUniformHandleui64vARB(int var0, int var1, int var2, long var3, long var5);

   public static boolean glIsTextureHandleResidentARB(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsTextureHandleResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsTextureHandleResidentARB(handle, function_pointer);
      return __result;
   }

   static native boolean nglIsTextureHandleResidentARB(long var0, long var2);

   public static boolean glIsImageHandleResidentARB(long handle) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsImageHandleResidentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsImageHandleResidentARB(handle, function_pointer);
      return __result;
   }

   static native boolean nglIsImageHandleResidentARB(long var0, long var2);

   public static void glVertexAttribL1ui64ARB(int index, long x) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL1ui64ARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribL1ui64ARB(index, x, function_pointer);
   }

   static native void nglVertexAttribL1ui64ARB(int var0, long var1, long var3);

   public static void glVertexAttribL1uARB(int index, LongBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL1ui64vARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)v, 1);
      nglVertexAttribL1ui64vARB(index, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglVertexAttribL1ui64vARB(int var0, long var1, long var3);

   public static void glGetVertexAttribLuARB(int index, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribLui64vARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 4);
      nglGetVertexAttribLui64vARB(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribLui64vARB(int var0, int var1, long var2, long var4);
}
