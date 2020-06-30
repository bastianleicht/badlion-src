package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class EXTVertexWeighting {
   public static final int GL_MODELVIEW0_STACK_DEPTH_EXT = 2979;
   public static final int GL_MODELVIEW1_STACK_DEPTH_EXT = 34050;
   public static final int GL_MODELVIEW0_MATRIX_EXT = 2982;
   public static final int GL_MODELVIEW1_MATRIX_EXT = 34054;
   public static final int GL_VERTEX_WEIGHTING_EXT = 34057;
   public static final int GL_MODELVIEW0_EXT = 5888;
   public static final int GL_MODELVIEW1_EXT = 34058;
   public static final int GL_CURRENT_VERTEX_WEIGHT_EXT = 34059;
   public static final int GL_VERTEX_WEIGHT_ARRAY_EXT = 34060;
   public static final int GL_VERTEX_WEIGHT_ARRAY_SIZE_EXT = 34061;
   public static final int GL_VERTEX_WEIGHT_ARRAY_TYPE_EXT = 34062;
   public static final int GL_VERTEX_WEIGHT_ARRAY_STRIDE_EXT = 34063;
   public static final int GL_VERTEX_WEIGHT_ARRAY_POINTER_EXT = 34064;

   public static void glVertexWeightfEXT(float weight) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexWeightfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexWeightfEXT(weight, function_pointer);
   }

   static native void nglVertexWeightfEXT(float var0, long var1);

   public static void glVertexWeightPointerEXT(int size, int stride, FloatBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexWeightPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_weighting_glVertexWeightPointerEXT_pPointer = pPointer;
      }

      nglVertexWeightPointerEXT(size, 5126, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   static native void nglVertexWeightPointerEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glVertexWeightPointerEXT(int size, int type, int stride, long pPointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexWeightPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglVertexWeightPointerEXTBO(size, type, stride, pPointer_buffer_offset, function_pointer);
   }

   static native void nglVertexWeightPointerEXTBO(int var0, int var1, int var2, long var3, long var5);
}
