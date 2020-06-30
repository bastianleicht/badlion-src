package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTBindableUniform {
   public static final int GL_MAX_VERTEX_BINDABLE_UNIFORMS_EXT = 36322;
   public static final int GL_MAX_FRAGMENT_BINDABLE_UNIFORMS_EXT = 36323;
   public static final int GL_MAX_GEOMETRY_BINDABLE_UNIFORMS_EXT = 36324;
   public static final int GL_MAX_BINDABLE_UNIFORM_SIZE_EXT = 36333;
   public static final int GL_UNIFORM_BUFFER_BINDING_EXT = 36335;
   public static final int GL_UNIFORM_BUFFER_EXT = 36334;

   public static void glUniformBufferEXT(int program, int location, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniformBufferEXT(program, location, buffer, function_pointer);
   }

   static native void nglUniformBufferEXT(int var0, int var1, int var2, long var3);

   public static int glGetUniformBufferSizeEXT(int program, int location) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformBufferSizeEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetUniformBufferSizeEXT(program, location, function_pointer);
      return __result;
   }

   static native int nglGetUniformBufferSizeEXT(int var0, int var1, long var2);

   public static long glGetUniformOffsetEXT(int program, int location) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      long __result = nglGetUniformOffsetEXT(program, location, function_pointer);
      return __result;
   }

   static native long nglGetUniformOffsetEXT(int var0, int var1, long var2);
}
