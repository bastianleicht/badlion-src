package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVFence {
   public static final int GL_ALL_COMPLETED_NV = 34034;
   public static final int GL_FENCE_STATUS_NV = 34035;
   public static final int GL_FENCE_CONDITION_NV = 34036;

   public static void glGenFencesNV(IntBuffer piFences) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFencesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piFences);
      nglGenFencesNV(piFences.remaining(), MemoryUtil.getAddress(piFences), function_pointer);
   }

   static native void nglGenFencesNV(int var0, long var1, long var3);

   public static int glGenFencesNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFencesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer piFences = APIUtil.getBufferInt(caps);
      nglGenFencesNV(1, MemoryUtil.getAddress(piFences), function_pointer);
      return piFences.get(0);
   }

   public static void glDeleteFencesNV(IntBuffer piFences) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFencesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piFences);
      nglDeleteFencesNV(piFences.remaining(), MemoryUtil.getAddress(piFences), function_pointer);
   }

   static native void nglDeleteFencesNV(int var0, long var1, long var3);

   public static void glDeleteFencesNV(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFencesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteFencesNV(1, APIUtil.getInt(caps, fence), function_pointer);
   }

   public static void glSetFenceNV(int fence, int condition) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetFenceNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSetFenceNV(fence, condition, function_pointer);
   }

   static native void nglSetFenceNV(int var0, int var1, long var2);

   public static boolean glTestFenceNV(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTestFenceNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglTestFenceNV(fence, function_pointer);
      return __result;
   }

   static native boolean nglTestFenceNV(int var0, long var1);

   public static void glFinishFenceNV(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFinishFenceNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFinishFenceNV(fence, function_pointer);
   }

   static native void nglFinishFenceNV(int var0, long var1);

   public static boolean glIsFenceNV(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsFenceNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsFenceNV(fence, function_pointer);
      return __result;
   }

   static native boolean nglIsFenceNV(int var0, long var1);

   public static void glGetFenceivNV(int fence, int pname, IntBuffer piParams) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFenceivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)piParams, 4);
      nglGetFenceivNV(fence, pname, MemoryUtil.getAddress(piParams), function_pointer);
   }

   static native void nglGetFenceivNV(int var0, int var1, long var2, long var4);
}
