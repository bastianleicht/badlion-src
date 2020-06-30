package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GLContext;

public final class ARBVertexAttrib64bit {
   public static final int GL_DOUBLE_VEC2 = 36860;
   public static final int GL_DOUBLE_VEC3 = 36861;
   public static final int GL_DOUBLE_VEC4 = 36862;
   public static final int GL_DOUBLE_MAT2 = 36678;
   public static final int GL_DOUBLE_MAT3 = 36679;
   public static final int GL_DOUBLE_MAT4 = 36680;
   public static final int GL_DOUBLE_MAT2x3 = 36681;
   public static final int GL_DOUBLE_MAT2x4 = 36682;
   public static final int GL_DOUBLE_MAT3x2 = 36683;
   public static final int GL_DOUBLE_MAT3x4 = 36684;
   public static final int GL_DOUBLE_MAT4x2 = 36685;
   public static final int GL_DOUBLE_MAT4x3 = 36686;

   public static void glVertexAttribL1d(int index, double x) {
      GL41.glVertexAttribL1d(index, x);
   }

   public static void glVertexAttribL2d(int index, double x, double y) {
      GL41.glVertexAttribL2d(index, x, y);
   }

   public static void glVertexAttribL3d(int index, double x, double y, double z) {
      GL41.glVertexAttribL3d(index, x, y, z);
   }

   public static void glVertexAttribL4d(int index, double x, double y, double z, double w) {
      GL41.glVertexAttribL4d(index, x, y, z, w);
   }

   public static void glVertexAttribL1(int index, DoubleBuffer v) {
      GL41.glVertexAttribL1(index, v);
   }

   public static void glVertexAttribL2(int index, DoubleBuffer v) {
      GL41.glVertexAttribL2(index, v);
   }

   public static void glVertexAttribL3(int index, DoubleBuffer v) {
      GL41.glVertexAttribL3(index, v);
   }

   public static void glVertexAttribL4(int index, DoubleBuffer v) {
      GL41.glVertexAttribL4(index, v);
   }

   public static void glVertexAttribLPointer(int index, int size, int stride, DoubleBuffer pointer) {
      GL41.glVertexAttribLPointer(index, size, stride, pointer);
   }

   public static void glVertexAttribLPointer(int index, int size, int stride, long pointer_buffer_offset) {
      GL41.glVertexAttribLPointer(index, size, stride, pointer_buffer_offset);
   }

   public static void glGetVertexAttribL(int index, int pname, DoubleBuffer params) {
      GL41.glGetVertexAttribL(index, pname, params);
   }

   public static void glVertexArrayVertexAttribLOffsetEXT(int vaobj, int buffer, int index, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayVertexAttribLOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayVertexAttribLOffsetEXT(vaobj, buffer, index, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayVertexAttribLOffsetEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);
}
