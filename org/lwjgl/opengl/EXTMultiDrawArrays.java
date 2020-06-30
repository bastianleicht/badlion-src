package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTMultiDrawArrays {
   public static void glMultiDrawArraysEXT(int mode, IntBuffer piFirst, IntBuffer piCount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piFirst);
      BufferChecks.checkBuffer(piCount, piFirst.remaining());
      nglMultiDrawArraysEXT(mode, MemoryUtil.getAddress(piFirst), MemoryUtil.getAddress(piCount), piFirst.remaining(), function_pointer);
   }

   static native void nglMultiDrawArraysEXT(int var0, long var1, long var3, int var5, long var6);
}
