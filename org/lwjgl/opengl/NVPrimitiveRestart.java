package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVPrimitiveRestart {
   public static final int GL_PRIMITIVE_RESTART_NV = 34136;
   public static final int GL_PRIMITIVE_RESTART_INDEX_NV = 34137;

   public static void glPrimitiveRestartNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPrimitiveRestartNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPrimitiveRestartNV(function_pointer);
   }

   static native void nglPrimitiveRestartNV(long var0);

   public static void glPrimitiveRestartIndexNV(int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPrimitiveRestartIndexNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPrimitiveRestartIndexNV(index, function_pointer);
   }

   static native void nglPrimitiveRestartIndexNV(int var0, long var1);
}
