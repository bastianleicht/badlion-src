package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class APPLEFlushBufferRange {
   public static final int GL_BUFFER_SERIALIZED_MODIFY_APPLE = 35346;
   public static final int GL_BUFFER_FLUSHING_UNMAP_APPLE = 35347;

   public static void glBufferParameteriAPPLE(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferParameteriAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBufferParameteriAPPLE(target, pname, param, function_pointer);
   }

   static native void nglBufferParameteriAPPLE(int var0, int var1, int var2, long var3);

   public static void glFlushMappedBufferRangeAPPLE(int target, long offset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFlushMappedBufferRangeAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFlushMappedBufferRangeAPPLE(target, offset, size, function_pointer);
   }

   static native void nglFlushMappedBufferRangeAPPLE(int var0, long var1, long var3, long var5);
}
