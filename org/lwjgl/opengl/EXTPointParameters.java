package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTPointParameters {
   public static final int GL_POINT_SIZE_MIN_EXT = 33062;
   public static final int GL_POINT_SIZE_MAX_EXT = 33063;
   public static final int GL_POINT_FADE_THRESHOLD_SIZE_EXT = 33064;
   public static final int GL_DISTANCE_ATTENUATION_EXT = 33065;

   public static void glPointParameterfEXT(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameterfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPointParameterfEXT(pname, param, function_pointer);
   }

   static native void nglPointParameterfEXT(int var0, float var1, long var2);

   public static void glPointParameterEXT(int pname, FloatBuffer pfParams) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pfParams, 4);
      nglPointParameterfvEXT(pname, MemoryUtil.getAddress(pfParams), function_pointer);
   }

   static native void nglPointParameterfvEXT(int var0, long var1, long var3);
}
