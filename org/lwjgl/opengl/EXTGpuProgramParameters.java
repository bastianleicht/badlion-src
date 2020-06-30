package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTGpuProgramParameters {
   public static void glProgramEnvParameters4EXT(int target, int index, int count, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramEnvParameters4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer(params, count << 2);
      nglProgramEnvParameters4fvEXT(target, index, count, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramEnvParameters4fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramLocalParameters4EXT(int target, int index, int count, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramLocalParameters4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer(params, count << 2);
      nglProgramLocalParameters4fvEXT(target, index, count, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglProgramLocalParameters4fvEXT(int var0, int var1, int var2, long var3, long var5);
}
