package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class AMDSamplePositions {
   public static final int GL_SUBSAMPLE_DISTANCE_AMD = 34879;

   public static void glSetMultisampleAMD(int pname, int index, FloatBuffer val) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetMultisamplefvAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)val, 2);
      nglSetMultisamplefvAMD(pname, index, MemoryUtil.getAddress(val), function_pointer);
   }

   static native void nglSetMultisamplefvAMD(int var0, int var1, long var2, long var4);
}
