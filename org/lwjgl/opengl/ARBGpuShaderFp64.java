package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GLContext;

public final class ARBGpuShaderFp64 {
   public static final int GL_DOUBLE_VEC2 = 36860;
   public static final int GL_DOUBLE_VEC3 = 36861;
   public static final int GL_DOUBLE_VEC4 = 36862;
   public static final int GL_DOUBLE_MAT2 = 36678;
   public static final int GL_DOUBLE_MAT3 = 36679;
   public static final int GL_DOUBLE_MAT4 = 36680;
   public static final int GL_DOUBLE_MAT2x3 = 36681;
   public static final int GL_DOUBLE_MAT2x4 = 36682;
   public static final int GL_DOUBLE_MAT3x2 = 36683;
   public static final int GL_DOUBLE_MAT3x4 = 36684;
   public static final int GL_DOUBLE_MAT4x2 = 36685;
   public static final int GL_DOUBLE_MAT4x3 = 36686;

   public static void glUniform1d(int location, double x) {
      GL40.glUniform1d(location, x);
   }

   public static void glUniform2d(int location, double x, double y) {
      GL40.glUniform2d(location, x, y);
   }

   public static void glUniform3d(int location, double x, double y, double z) {
      GL40.glUniform3d(location, x, y, z);
   }

   public static void glUniform4d(int location, double x, double y, double z, double w) {
      GL40.glUniform4d(location, x, y, z, w);
   }

   public static void glUniform1(int location, DoubleBuffer value) {
      GL40.glUniform1(location, value);
   }

   public static void glUniform2(int location, DoubleBuffer value) {
      GL40.glUniform2(location, value);
   }

   public static void glUniform3(int location, DoubleBuffer value) {
      GL40.glUniform3(location, value);
   }

   public static void glUniform4(int location, DoubleBuffer value) {
      GL40.glUniform4(location, value);
   }

   public static void glUniformMatrix2(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix2(location, transpose, value);
   }

   public static void glUniformMatrix3(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix3(location, transpose, value);
   }

   public static void glUniformMatrix4(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix4(location, transpose, value);
   }

   public static void glUniformMatrix2x3(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix2x3(location, transpose, value);
   }

   public static void glUniformMatrix2x4(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix2x4(location, transpose, value);
   }

   public static void glUniformMatrix3x2(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix3x2(location, transpose, value);
   }

   public static void glUniformMatrix3x4(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix3x4(location, transpose, value);
   }

   public static void glUniformMatrix4x2(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix4x2(location, transpose, value);
   }

   public static void glUniformMatrix4x3(int location, boolean transpose, DoubleBuffer value) {
      GL40.glUniformMatrix4x3(location, transpose, value);
   }

   public static void glGetUniform(int program, int location, DoubleBuffer params) {
      GL40.glGetUniform(program, location, params);
   }

   public static void glProgramUniform1dEXT(int program, int location, double x) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform1dEXT(program, location, x, function_pointer);
   }

   static native void nglProgramUniform1dEXT(int var0, int var1, double var2, long var4);

   public static void glProgramUniform2dEXT(int program, int location, double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform2dEXT(program, location, x, y, function_pointer);
   }

   static native void nglProgramUniform2dEXT(int var0, int var1, double var2, double var4, long var6);

   public static void glProgramUniform3dEXT(int program, int location, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform3dEXT(program, location, x, y, z, function_pointer);
   }

   static native void nglProgramUniform3dEXT(int var0, int var1, double var2, double var4, double var6, long var8);

   public static void glProgramUniform4dEXT(int program, int location, double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform4dEXT(program, location, x, y, z, w, function_pointer);
   }

   static native void nglProgramUniform4dEXT(int var0, int var1, double var2, double var4, double var6, double var8, long var10);

   public static void glProgramUniform1EXT(int program, int location, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform1dvEXT(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform1dvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform2EXT(int program, int location, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform2dvEXT(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform2dvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform3EXT(int program, int location, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform3dvEXT(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform3dvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform4EXT(int program, int location, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform4dvEXT(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform4dvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniformMatrix2EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2dvEXT(program, location, value.remaining() >> 2, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3dvEXT(program, location, value.remaining() / 9, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4dvEXT(program, location, value.remaining() >> 4, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix2x3EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2x3dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2x3dvEXT(program, location, value.remaining() / 6, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2x3dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix2x4EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2x4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2x4dvEXT(program, location, value.remaining() >> 3, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2x4dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3x2EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3x2dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3x2dvEXT(program, location, value.remaining() / 6, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3x2dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3x4EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3x4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3x4dvEXT(program, location, value.remaining() / 12, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3x4dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4x2EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4x2dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4x2dvEXT(program, location, value.remaining() >> 3, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4x2dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4x3EXT(int program, int location, boolean transpose, DoubleBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4x3dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4x3dvEXT(program, location, value.remaining() / 12, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4x3dvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);
}
