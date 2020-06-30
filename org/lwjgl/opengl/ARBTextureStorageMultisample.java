package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLContext;

public final class ARBTextureStorageMultisample {
   public static void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
      GL43.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
   }

   public static void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
      GL43.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
   }

   public static void glTextureStorage2DMultisampleEXT(int texture, int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureStorage2DMultisampleEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureStorage2DMultisampleEXT(texture, target, samples, internalformat, width, height, fixedsamplelocations, function_pointer);
   }

   static native void nglTextureStorage2DMultisampleEXT(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, long var7);

   public static void glTextureStorage3DMultisampleEXT(int texture, int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureStorage3DMultisampleEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureStorage3DMultisampleEXT(texture, target, samples, internalformat, width, height, depth, fixedsamplelocations, function_pointer);
   }

   static native void nglTextureStorage3DMultisampleEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, long var8);
}
