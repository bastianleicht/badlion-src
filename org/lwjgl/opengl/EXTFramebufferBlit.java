package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTFramebufferBlit {
   public static final int GL_READ_FRAMEBUFFER_EXT = 36008;
   public static final int GL_DRAW_FRAMEBUFFER_EXT = 36009;
   public static final int GL_DRAW_FRAMEBUFFER_BINDING_EXT = 36006;
   public static final int GL_READ_FRAMEBUFFER_BINDING_EXT = 36010;

   public static void glBlitFramebufferEXT(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBlitFramebufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter, function_pointer);
   }

   static native void nglBlitFramebufferEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10);
}
