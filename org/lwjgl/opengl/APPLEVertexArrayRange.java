package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class APPLEVertexArrayRange {
   public static final int GL_VERTEX_ARRAY_RANGE_APPLE = 34077;
   public static final int GL_VERTEX_ARRAY_RANGE_LENGTH_APPLE = 34078;
   public static final int GL_MAX_VERTEX_ARRAY_RANGE_ELEMENT_APPLE = 34080;
   public static final int GL_VERTEX_ARRAY_RANGE_POINTER_APPLE = 34081;
   public static final int GL_VERTEX_ARRAY_STORAGE_HINT_APPLE = 34079;
   public static final int GL_STORAGE_CACHED_APPLE = 34238;
   public static final int GL_STORAGE_SHARED_APPLE = 34239;
   public static final int GL_DRAW_PIXELS_APPLE = 35338;
   public static final int GL_FENCE_APPLE = 35339;

   public static void glVertexArrayRangeAPPLE(ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayRangeAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pointer);
      nglVertexArrayRangeAPPLE(pointer.remaining(), MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglVertexArrayRangeAPPLE(int var0, long var1, long var3);

   public static void glFlushVertexArrayRangeAPPLE(ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFlushVertexArrayRangeAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pointer);
      nglFlushVertexArrayRangeAPPLE(pointer.remaining(), MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglFlushVertexArrayRangeAPPLE(int var0, long var1, long var3);

   public static void glVertexArrayParameteriAPPLE(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayParameteriAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayParameteriAPPLE(pname, param, function_pointer);
   }

   static native void nglVertexArrayParameteriAPPLE(int var0, int var1, long var2);
}
