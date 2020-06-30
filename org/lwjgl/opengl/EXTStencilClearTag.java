package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTStencilClearTag {
   public static final int GL_STENCIL_TAG_BITS_EXT = 35058;
   public static final int GL_STENCIL_CLEAR_TAG_VALUE_EXT = 35059;

   public static void glStencilClearTagEXT(int stencilTagBits, int stencilClearTag) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilClearTagEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilClearTagEXT(stencilTagBits, stencilClearTag, function_pointer);
   }

   static native void nglStencilClearTagEXT(int var0, int var1, long var2);
}
