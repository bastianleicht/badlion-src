package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTStencilTwoSide {
   public static final int GL_STENCIL_TEST_TWO_SIDE_EXT = 35088;
   public static final int GL_ACTIVE_STENCIL_FACE_EXT = 35089;

   public static void glActiveStencilFaceEXT(int face) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glActiveStencilFaceEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglActiveStencilFaceEXT(face, function_pointer);
   }

   static native void nglActiveStencilFaceEXT(int var0, long var1);
}
