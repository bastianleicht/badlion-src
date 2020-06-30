package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ATIVertexAttribArrayObject {
   public static void glVertexAttribArrayObjectATI(int index, int size, int type, boolean normalized, int stride, int buffer, int offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribArrayObjectATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribArrayObjectATI(index, size, type, normalized, stride, buffer, offset, function_pointer);
   }

   static native void nglVertexAttribArrayObjectATI(int var0, int var1, int var2, boolean var3, int var4, int var5, int var6, long var7);

   public static void glGetVertexAttribArrayObjectATI(int index, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribArrayObjectfvATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetVertexAttribArrayObjectfvATI(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribArrayObjectfvATI(int var0, int var1, long var2, long var4);

   public static void glGetVertexAttribArrayObjectATI(int index, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribArrayObjectivATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetVertexAttribArrayObjectivATI(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribArrayObjectivATI(int var0, int var1, long var2, long var4);
}
