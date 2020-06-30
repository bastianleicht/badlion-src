package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVGpuProgram4 {
   public static final int GL_PROGRAM_ATTRIB_COMPONENTS_NV = 35078;
   public static final int GL_PROGRAM_RESULT_COMPONENTS_NV = 35079;
   public static final int GL_MAX_PROGRAM_ATTRIB_COMPONENTS_NV = 35080;
   public static final int GL_MAX_PROGRAM_RESULT_COMPONENTS_NV = 35081;
   public static final int GL_MAX_PROGRAM_GENERIC_ATTRIBS_NV = 36261;
   public static final int GL_MAX_PROGRAM_GENERIC_RESULTS_NV = 36262;

   public static void glProgramLocalParameterI4iNV(int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParameterI4iNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramLocalParameterI4iNV(target, index, x, y, z, w, function_pointer);
   }

   static native void nglProgramLocalParameterI4iNV(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramLocalParameterI4NV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParameterI4ivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglProgramLocalParameterI4ivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramLocalParameterI4ivNV(int var0, int var1, long var2, long var4);

   public static void glProgramLocalParametersI4NV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParametersI4ivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglProgramLocalParametersI4ivNV(target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramLocalParametersI4ivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramLocalParameterI4uiNV(int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParameterI4uiNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramLocalParameterI4uiNV(target, index, x, y, z, w, function_pointer);
   }

   static native void nglProgramLocalParameterI4uiNV(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramLocalParameterI4uNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParameterI4uivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglProgramLocalParameterI4uivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramLocalParameterI4uivNV(int var0, int var1, long var2, long var4);

   public static void glProgramLocalParametersI4uNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParametersI4uivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglProgramLocalParametersI4uivNV(target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramLocalParametersI4uivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramEnvParameterI4iNV(int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParameterI4iNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramEnvParameterI4iNV(target, index, x, y, z, w, function_pointer);
   }

   static native void nglProgramEnvParameterI4iNV(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramEnvParameterI4NV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParameterI4ivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglProgramEnvParameterI4ivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramEnvParameterI4ivNV(int var0, int var1, long var2, long var4);

   public static void glProgramEnvParametersI4NV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParametersI4ivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglProgramEnvParametersI4ivNV(target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramEnvParametersI4ivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramEnvParameterI4uiNV(int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParameterI4uiNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramEnvParameterI4uiNV(target, index, x, y, z, w, function_pointer);
   }

   static native void nglProgramEnvParameterI4uiNV(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramEnvParameterI4uNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParameterI4uivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglProgramEnvParameterI4uivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramEnvParameterI4uivNV(int var0, int var1, long var2, long var4);

   public static void glProgramEnvParametersI4uNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParametersI4uivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglProgramEnvParametersI4uivNV(target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramEnvParametersI4uivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glGetProgramLocalParameterINV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramLocalParameterIivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetProgramLocalParameterIivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramLocalParameterIivNV(int var0, int var1, long var2, long var4);

   public static void glGetProgramLocalParameterIuNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramLocalParameterIuivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetProgramLocalParameterIuivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramLocalParameterIuivNV(int var0, int var1, long var2, long var4);

   public static void glGetProgramEnvParameterINV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramEnvParameterIivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetProgramEnvParameterIivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramEnvParameterIivNV(int var0, int var1, long var2, long var4);

   public static void glGetProgramEnvParameterIuNV(int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramEnvParameterIuivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetProgramEnvParameterIuivNV(target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramEnvParameterIuivNV(int var0, int var1, long var2, long var4);
}
