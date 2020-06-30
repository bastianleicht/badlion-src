package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTSeparateShaderObjects {
   public static final int GL_ACTIVE_PROGRAM_EXT = 35725;

   public static void glUseShaderProgramEXT(int type, int program) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUseShaderProgramEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUseShaderProgramEXT(type, program, function_pointer);
   }

   static native void nglUseShaderProgramEXT(int var0, int var1, long var2);

   public static void glActiveProgramEXT(int program) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glActiveProgramEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglActiveProgramEXT(program, function_pointer);
   }

   static native void nglActiveProgramEXT(int var0, long var1);

   public static int glCreateShaderProgramEXT(int type, ByteBuffer string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCreateShaderProgramEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(string);
      BufferChecks.checkNullTerminated(string);
      int __result = nglCreateShaderProgramEXT(type, MemoryUtil.getAddress(string), function_pointer);
      return __result;
   }

   static native int nglCreateShaderProgramEXT(int var0, long var1, long var3);

   public static int glCreateShaderProgramEXT(int type, CharSequence string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCreateShaderProgramEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglCreateShaderProgramEXT(type, APIUtil.getBufferNT(caps, string), function_pointer);
      return __result;
   }
}
