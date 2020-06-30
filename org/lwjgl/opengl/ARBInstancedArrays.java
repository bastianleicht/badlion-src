package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBInstancedArrays {
   public static final int GL_VERTEX_ATTRIB_ARRAY_DIVISOR_ARB = 35070;

   public static void glVertexAttribDivisorARB(int index, int divisor) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribDivisorARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribDivisorARB(index, divisor, function_pointer);
   }

   static native void nglVertexAttribDivisorARB(int var0, int var1, long var2);
}
