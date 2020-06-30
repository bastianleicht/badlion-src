package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class AMDInterleavedElements {
   public static final int GL_VERTEX_ELEMENT_SWIZZLE_AMD = 37284;
   public static final int GL_VERTEX_ID_SWIZZLE_AMD = 37285;

   public static void glVertexAttribParameteriAMD(int index, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribParameteriAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribParameteriAMD(index, pname, param, function_pointer);
   }

   static native void nglVertexAttribParameteriAMD(int var0, int var1, int var2, long var3);
}
