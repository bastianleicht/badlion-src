package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBTextureBufferObject {
   public static final int GL_TEXTURE_BUFFER_ARB = 35882;
   public static final int GL_MAX_TEXTURE_BUFFER_SIZE_ARB = 35883;
   public static final int GL_TEXTURE_BINDING_BUFFER_ARB = 35884;
   public static final int GL_TEXTURE_BUFFER_DATA_STORE_BINDING_ARB = 35885;
   public static final int GL_TEXTURE_BUFFER_FORMAT_ARB = 35886;

   public static void glTexBufferARB(int target, int internalformat, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexBufferARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexBufferARB(target, internalformat, buffer, function_pointer);
   }

   static native void nglTexBufferARB(int var0, int var1, int var2, long var3);
}
