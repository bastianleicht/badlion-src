package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class APPLEFence {
   public static final int GL_DRAW_PIXELS_APPLE = 35338;
   public static final int GL_FENCE_APPLE = 35339;

   public static void glGenFencesAPPLE(IntBuffer fences) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFencesAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(fences);
      nglGenFencesAPPLE(fences.remaining(), MemoryUtil.getAddress(fences), function_pointer);
   }

   static native void nglGenFencesAPPLE(int var0, long var1, long var3);

   public static int glGenFencesAPPLE() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenFencesAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer fences = APIUtil.getBufferInt(caps);
      nglGenFencesAPPLE(1, MemoryUtil.getAddress(fences), function_pointer);
      return fences.get(0);
   }

   public static void glDeleteFencesAPPLE(IntBuffer fences) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFencesAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(fences);
      nglDeleteFencesAPPLE(fences.remaining(), MemoryUtil.getAddress(fences), function_pointer);
   }

   static native void nglDeleteFencesAPPLE(int var0, long var1, long var3);

   public static void glDeleteFencesAPPLE(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteFencesAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteFencesAPPLE(1, APIUtil.getInt(caps, fence), function_pointer);
   }

   public static void glSetFenceAPPLE(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetFenceAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSetFenceAPPLE(fence, function_pointer);
   }

   static native void nglSetFenceAPPLE(int var0, long var1);

   public static boolean glIsFenceAPPLE(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsFenceAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsFenceAPPLE(fence, function_pointer);
      return __result;
   }

   static native boolean nglIsFenceAPPLE(int var0, long var1);

   public static boolean glTestFenceAPPLE(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTestFenceAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglTestFenceAPPLE(fence, function_pointer);
      return __result;
   }

   static native boolean nglTestFenceAPPLE(int var0, long var1);

   public static void glFinishFenceAPPLE(int fence) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFinishFenceAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFinishFenceAPPLE(fence, function_pointer);
   }

   static native void nglFinishFenceAPPLE(int var0, long var1);

   public static boolean glTestObjectAPPLE(int object, int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTestObjectAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglTestObjectAPPLE(object, name, function_pointer);
      return __result;
   }

   static native boolean nglTestObjectAPPLE(int var0, int var1, long var2);

   public static void glFinishObjectAPPLE(int object, int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFinishObjectAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFinishObjectAPPLE(object, name, function_pointer);
   }

   static native void nglFinishObjectAPPLE(int var0, int var1, long var2);
}
