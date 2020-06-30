package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVGpuShader5;

public final class NVShaderBufferLoad {
   public static final int GL_BUFFER_GPU_ADDRESS_NV = 36637;
   public static final int GL_GPU_ADDRESS_NV = 36660;
   public static final int GL_MAX_SHADER_BUFFER_ADDRESS_NV = 36661;

   public static void glMakeBufferResidentNV(int target, int access) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeBufferResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeBufferResidentNV(target, access, function_pointer);
   }

   static native void nglMakeBufferResidentNV(int var0, int var1, long var2);

   public static void glMakeBufferNonResidentNV(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeBufferNonResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeBufferNonResidentNV(target, function_pointer);
   }

   static native void nglMakeBufferNonResidentNV(int var0, long var1);

   public static boolean glIsBufferResidentNV(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsBufferResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsBufferResidentNV(target, function_pointer);
      return __result;
   }

   static native boolean nglIsBufferResidentNV(int var0, long var1);

   public static void glMakeNamedBufferResidentNV(int buffer, int access) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeNamedBufferResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeNamedBufferResidentNV(buffer, access, function_pointer);
   }

   static native void nglMakeNamedBufferResidentNV(int var0, int var1, long var2);

   public static void glMakeNamedBufferNonResidentNV(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMakeNamedBufferNonResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMakeNamedBufferNonResidentNV(buffer, function_pointer);
   }

   static native void nglMakeNamedBufferNonResidentNV(int var0, long var1);

   public static boolean glIsNamedBufferResidentNV(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsNamedBufferResidentNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsNamedBufferResidentNV(buffer, function_pointer);
      return __result;
   }

   static native boolean nglIsNamedBufferResidentNV(int var0, long var1);

   public static void glGetBufferParameteruNV(int target, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameterui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 1);
      nglGetBufferParameterui64vNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetBufferParameterui64vNV(int var0, int var1, long var2, long var4);

   public static long glGetBufferParameterui64NV(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameterui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetBufferParameterui64vNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetNamedBufferParameteruNV(int buffer, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferParameterui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 1);
      nglGetNamedBufferParameterui64vNV(buffer, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedBufferParameterui64vNV(int var0, int var1, long var2, long var4);

   public static long glGetNamedBufferParameterui64NV(int buffer, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferParameterui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetNamedBufferParameterui64vNV(buffer, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetIntegeruNV(int value, LongBuffer result) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)result, 1);
      nglGetIntegerui64vNV(value, MemoryUtil.getAddress(result), function_pointer);
   }

   static native void nglGetIntegerui64vNV(int var0, long var1, long var3);

   public static long glGetIntegerui64NV(int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer result = APIUtil.getBufferLong(caps);
      nglGetIntegerui64vNV(value, MemoryUtil.getAddress(result), function_pointer);
      return result.get(0);
   }

   public static void glUniformui64NV(int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformui64NV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniformui64NV(location, value, function_pointer);
   }

   static native void nglUniformui64NV(int var0, long var1, long var3);

   public static void glUniformuNV(int location, LongBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglUniformui64vNV(location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglUniformui64vNV(int var0, int var1, long var2, long var4);

   public static void glGetUniformuNV(int program, int location, LongBuffer params) {
      NVGpuShader5.glGetUniformuNV(program, location, params);
   }

   public static void glProgramUniformui64NV(int program, int location, long value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformui64NV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniformui64NV(program, location, value, function_pointer);
   }

   static native void nglProgramUniformui64NV(int var0, int var1, long var2, long var4);

   public static void glProgramUniformuNV(int program, int location, LongBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformui64vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformui64vNV(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformui64vNV(int var0, int var1, int var2, long var3, long var5);
}
