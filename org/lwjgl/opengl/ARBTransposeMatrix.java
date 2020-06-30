package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBTransposeMatrix {
   public static final int GL_TRANSPOSE_MODELVIEW_MATRIX_ARB = 34019;
   public static final int GL_TRANSPOSE_PROJECTION_MATRIX_ARB = 34020;
   public static final int GL_TRANSPOSE_TEXTURE_MATRIX_ARB = 34021;
   public static final int GL_TRANSPOSE_COLOR_MATRIX_ARB = 34022;

   public static void glLoadTransposeMatrixARB(FloatBuffer pfMtx) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadTransposeMatrixfARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pfMtx, 16);
      nglLoadTransposeMatrixfARB(MemoryUtil.getAddress(pfMtx), function_pointer);
   }

   static native void nglLoadTransposeMatrixfARB(long var0, long var2);

   public static void glMultTransposeMatrixARB(FloatBuffer pfMtx) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultTransposeMatrixfARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pfMtx, 16);
      nglMultTransposeMatrixfARB(MemoryUtil.getAddress(pfMtx), function_pointer);
   }

   static native void nglMultTransposeMatrixfARB(long var0, long var2);
}
