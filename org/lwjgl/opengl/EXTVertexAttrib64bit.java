package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ARBVertexAttrib64bit;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class EXTVertexAttrib64bit {
   public static final int GL_DOUBLE_VEC2_EXT = 36860;
   public static final int GL_DOUBLE_VEC3_EXT = 36861;
   public static final int GL_DOUBLE_VEC4_EXT = 36862;
   public static final int GL_DOUBLE_MAT2_EXT = 36678;
   public static final int GL_DOUBLE_MAT3_EXT = 36679;
   public static final int GL_DOUBLE_MAT4_EXT = 36680;
   public static final int GL_DOUBLE_MAT2x3_EXT = 36681;
   public static final int GL_DOUBLE_MAT2x4_EXT = 36682;
   public static final int GL_DOUBLE_MAT3x2_EXT = 36683;
   public static final int GL_DOUBLE_MAT3x4_EXT = 36684;
   public static final int GL_DOUBLE_MAT4x2_EXT = 36685;
   public static final int GL_DOUBLE_MAT4x3_EXT = 36686;

   public static void glVertexAttribL1dEXT(int index, double x) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL1dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribL1dEXT(index, x, function_pointer);
   }

   static native void nglVertexAttribL1dEXT(int var0, double var1, long var3);

   public static void glVertexAttribL2dEXT(int index, double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL2dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribL2dEXT(index, x, y, function_pointer);
   }

   static native void nglVertexAttribL2dEXT(int var0, double var1, double var3, long var5);

   public static void glVertexAttribL3dEXT(int index, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL3dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribL3dEXT(index, x, y, z, function_pointer);
   }

   static native void nglVertexAttribL3dEXT(int var0, double var1, double var3, double var5, long var7);

   public static void glVertexAttribL4dEXT(int index, double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL4dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribL4dEXT(index, x, y, z, w, function_pointer);
   }

   static native void nglVertexAttribL4dEXT(int var0, double var1, double var3, double var5, double var7, long var9);

   public static void glVertexAttribL1EXT(int index, DoubleBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL1dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)v, 1);
      nglVertexAttribL1dvEXT(index, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglVertexAttribL1dvEXT(int var0, long var1, long var3);

   public static void glVertexAttribL2EXT(int index, DoubleBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL2dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)v, 2);
      nglVertexAttribL2dvEXT(index, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglVertexAttribL2dvEXT(int var0, long var1, long var3);

   public static void glVertexAttribL3EXT(int index, DoubleBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL3dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)v, 3);
      nglVertexAttribL3dvEXT(index, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglVertexAttribL3dvEXT(int var0, long var1, long var3);

   public static void glVertexAttribL4EXT(int index, DoubleBuffer v) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribL4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)v, 4);
      nglVertexAttribL4dvEXT(index, MemoryUtil.getAddress(v), function_pointer);
   }

   static native void nglVertexAttribL4dvEXT(int var0, long var1, long var3);

   public static void glVertexAttribLPointerEXT(int index, int size, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribLPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = pointer;
      }

      nglVertexAttribLPointerEXT(index, size, 5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglVertexAttribLPointerEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glVertexAttribLPointerEXT(int index, int size, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribLPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglVertexAttribLPointerEXTBO(index, size, 5130, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglVertexAttribLPointerEXTBO(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glGetVertexAttribLEXT(int index, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribLdvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetVertexAttribLdvEXT(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribLdvEXT(int var0, int var1, long var2, long var4);

   public static void glVertexArrayVertexAttribLOffsetEXT(int vaobj, int buffer, int index, int size, int type, int stride, long offset) {
      ARBVertexAttrib64bit.glVertexArrayVertexAttribLOffsetEXT(vaobj, buffer, index, size, type, stride, offset);
   }
}
