package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVProgram;

public final class NVFragmentProgram extends NVProgram {
   public static final int GL_FRAGMENT_PROGRAM_NV = 34928;
   public static final int GL_MAX_TEXTURE_COORDS_NV = 34929;
   public static final int GL_MAX_TEXTURE_IMAGE_UNITS_NV = 34930;
   public static final int GL_FRAGMENT_PROGRAM_BINDING_NV = 34931;
   public static final int GL_MAX_FRAGMENT_PROGRAM_LOCAL_PARAMETERS_NV = 34920;

   public static void glProgramNamedParameter4fNV(int id, ByteBuffer name, float x, float y, float z, float w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramNamedParameter4fNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      nglProgramNamedParameter4fNV(id, name.remaining(), MemoryUtil.getAddress(name), x, y, z, w, function_pointer);
   }

   static native void nglProgramNamedParameter4fNV(int var0, int var1, long var2, float var4, float var5, float var6, float var7, long var8);

   public static void glProgramNamedParameter4dNV(int id, ByteBuffer name, double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramNamedParameter4dNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      nglProgramNamedParameter4dNV(id, name.remaining(), MemoryUtil.getAddress(name), x, y, z, w, function_pointer);
   }

   static native void nglProgramNamedParameter4dNV(int var0, int var1, long var2, double var4, double var6, double var8, double var10, long var12);

   public static void glGetProgramNamedParameterNV(int id, ByteBuffer name, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramNamedParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetProgramNamedParameterfvNV(id, name.remaining(), MemoryUtil.getAddress(name), MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramNamedParameterfvNV(int var0, int var1, long var2, long var4, long var6);

   public static void glGetProgramNamedParameterNV(int id, ByteBuffer name, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetProgramNamedParameterdvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetProgramNamedParameterdvNV(id, name.remaining(), MemoryUtil.getAddress(name), MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetProgramNamedParameterdvNV(int var0, int var1, long var2, long var4, long var6);
}
