package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTTimerQuery {
   public static final int GL_TIME_ELAPSED_EXT = 35007;

   public static void glGetQueryObjectEXT(int id, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjecti64vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 1);
      nglGetQueryObjecti64vEXT(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetQueryObjecti64vEXT(int var0, int var1, long var2, long var4);

   public static long glGetQueryObjectEXT(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjecti64vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetQueryObjecti64vEXT(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetQueryObjectuEXT(int id, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectui64vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 1);
      nglGetQueryObjectui64vEXT(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetQueryObjectui64vEXT(int var0, int var1, long var2, long var4);

   public static long glGetQueryObjectuEXT(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectui64vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetQueryObjectui64vEXT(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }
}
