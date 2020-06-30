package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ATIPnTriangles {
   public static final int GL_PN_TRIANGLES_ATI = 34800;
   public static final int GL_MAX_PN_TRIANGLES_TESSELATION_LEVEL_ATI = 34801;
   public static final int GL_PN_TRIANGLES_POINT_MODE_ATI = 34802;
   public static final int GL_PN_TRIANGLES_NORMAL_MODE_ATI = 34803;
   public static final int GL_PN_TRIANGLES_TESSELATION_LEVEL_ATI = 34804;
   public static final int GL_PN_TRIANGLES_POINT_MODE_LINEAR_ATI = 34805;
   public static final int GL_PN_TRIANGLES_POINT_MODE_CUBIC_ATI = 34806;
   public static final int GL_PN_TRIANGLES_NORMAL_MODE_LINEAR_ATI = 34807;
   public static final int GL_PN_TRIANGLES_NORMAL_MODE_QUADRATIC_ATI = 34808;

   public static void glPNTrianglesfATI(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPNTrianglesfATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPNTrianglesfATI(pname, param, function_pointer);
   }

   static native void nglPNTrianglesfATI(int var0, float var1, long var2);

   public static void glPNTrianglesiATI(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPNTrianglesiATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPNTrianglesiATI(pname, param, function_pointer);
   }

   static native void nglPNTrianglesiATI(int var0, int var1, long var2);
}
