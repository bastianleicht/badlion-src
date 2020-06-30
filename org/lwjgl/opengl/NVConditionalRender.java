package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVConditionalRender {
   public static final int GL_QUERY_WAIT_NV = 36371;
   public static final int GL_QUERY_NO_WAIT_NV = 36372;
   public static final int GL_QUERY_BY_REGION_WAIT_NV = 36373;
   public static final int GL_QUERY_BY_REGION_NO_WAIT_NV = 36374;

   public static void glBeginConditionalRenderNV(int id, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginConditionalRenderNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginConditionalRenderNV(id, mode, function_pointer);
   }

   static native void nglBeginConditionalRenderNV(int var0, int var1, long var2);

   public static void glEndConditionalRenderNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndConditionalRenderNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndConditionalRenderNV(function_pointer);
   }

   static native void nglEndConditionalRenderNV(long var0);
}
