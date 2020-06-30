package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLContext;

public final class ARBTextureBufferRange {
   public static final int GL_TEXTURE_BUFFER_OFFSET = 37277;
   public static final int GL_TEXTURE_BUFFER_SIZE = 37278;
   public static final int GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT = 37279;

   public static void glTexBufferRange(int target, int internalformat, int buffer, long offset, long size) {
      GL43.glTexBufferRange(target, internalformat, buffer, offset, size);
   }

   public static void glTextureBufferRangeEXT(int texture, int target, int internalformat, int buffer, long offset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureBufferRangeEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureBufferRangeEXT(texture, target, internalformat, buffer, offset, size, function_pointer);
   }

   static native void nglTextureBufferRangeEXT(int var0, int var1, int var2, int var3, long var4, long var6, long var8);
}
