package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTGeometryShader4 {
   public static final int GL_GEOMETRY_SHADER_EXT = 36313;
   public static final int GL_GEOMETRY_VERTICES_OUT_EXT = 36314;
   public static final int GL_GEOMETRY_INPUT_TYPE_EXT = 36315;
   public static final int GL_GEOMETRY_OUTPUT_TYPE_EXT = 36316;
   public static final int GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS_EXT = 35881;
   public static final int GL_MAX_GEOMETRY_VARYING_COMPONENTS_EXT = 36317;
   public static final int GL_MAX_VERTEX_VARYING_COMPONENTS_EXT = 36318;
   public static final int GL_MAX_VARYING_COMPONENTS_EXT = 35659;
   public static final int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS_EXT = 36319;
   public static final int GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT = 36320;
   public static final int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS_EXT = 36321;
   public static final int GL_LINES_ADJACENCY_EXT = 10;
   public static final int GL_LINE_STRIP_ADJACENCY_EXT = 11;
   public static final int GL_TRIANGLES_ADJACENCY_EXT = 12;
   public static final int GL_TRIANGLE_STRIP_ADJACENCY_EXT = 13;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT = 36264;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_EXT = 36265;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_LAYERED_EXT = 36263;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER_EXT = 36052;
   public static final int GL_PROGRAM_POINT_SIZE_EXT = 34370;

   public static void glProgramParameteriEXT(int program, int pname, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramParameteriEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramParameteriEXT(program, pname, value, function_pointer);
   }

   static native void nglProgramParameteriEXT(int var0, int var1, int var2, long var3);

   public static void glFramebufferTextureEXT(int target, int attachment, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTextureEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTextureEXT(target, attachment, texture, level, function_pointer);
   }

   static native void nglFramebufferTextureEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glFramebufferTextureLayerEXT(int target, int attachment, int texture, int level, int layer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTextureLayerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTextureLayerEXT(target, attachment, texture, level, layer, function_pointer);
   }

   static native void nglFramebufferTextureLayerEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glFramebufferTextureFaceEXT(int target, int attachment, int texture, int level, int face) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTextureFaceEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTextureFaceEXT(target, attachment, texture, level, face, function_pointer);
   }

   static native void nglFramebufferTextureFaceEXT(int var0, int var1, int var2, int var3, int var4, long var5);
}
