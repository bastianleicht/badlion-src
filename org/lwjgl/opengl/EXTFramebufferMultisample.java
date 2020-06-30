package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTFramebufferMultisample {
   public static final int GL_RENDERBUFFER_SAMPLES_EXT = 36011;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE_EXT = 36182;
   public static final int GL_MAX_SAMPLES_EXT = 36183;

   public static void glRenderbufferStorageMultisampleEXT(int target, int samples, int internalformat, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glRenderbufferStorageMultisampleEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglRenderbufferStorageMultisampleEXT(target, samples, internalformat, width, height, function_pointer);
   }

   static native void nglRenderbufferStorageMultisampleEXT(int var0, int var1, int var2, int var3, int var4, long var5);
}
