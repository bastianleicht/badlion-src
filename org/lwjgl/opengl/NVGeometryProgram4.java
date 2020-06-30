package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTGeometryShader4;
import org.lwjgl.opengl.GLContext;

public final class NVGeometryProgram4 {
   public static final int GL_GEOMETRY_PROGRAM_NV = 35878;
   public static final int GL_MAX_PROGRAM_OUTPUT_VERTICES_NV = 35879;
   public static final int GL_MAX_PROGRAM_TOTAL_OUTPUT_COMPONENTS_NV = 35880;

   public static void glProgramVertexLimitNV(int target, int limit) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramVertexLimitNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramVertexLimitNV(target, limit, function_pointer);
   }

   static native void nglProgramVertexLimitNV(int var0, int var1, long var2);

   public static void glFramebufferTextureEXT(int target, int attachment, int texture, int level) {
      EXTGeometryShader4.glFramebufferTextureEXT(target, attachment, texture, level);
   }

   public static void glFramebufferTextureLayerEXT(int target, int attachment, int texture, int level, int layer) {
      EXTGeometryShader4.glFramebufferTextureLayerEXT(target, attachment, texture, level, layer);
   }

   public static void glFramebufferTextureFaceEXT(int target, int attachment, int texture, int level, int face) {
      EXTGeometryShader4.glFramebufferTextureFaceEXT(target, attachment, texture, level, face);
   }
}
