package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVPointSprite {
   public static final int GL_POINT_SPRITE_NV = 34913;
   public static final int GL_COORD_REPLACE_NV = 34914;
   public static final int GL_POINT_SPRITE_R_MODE_NV = 34915;

   public static void glPointParameteriNV(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameteriNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPointParameteriNV(pname, param, function_pointer);
   }

   static native void nglPointParameteriNV(int var0, int var1, long var2);

   public static void glPointParameterNV(int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglPointParameterivNV(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglPointParameterivNV(int var0, long var1, long var3);
}
