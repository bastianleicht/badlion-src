package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVDrawTexture {
   public static void glDrawTextureNV(int texture, int sampler, float x0, float y0, float x1, float y1, float z, float s0, float t0, float s1, float t1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawTextureNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawTextureNV(texture, sampler, x0, y0, x1, y1, z, s0, t0, s1, t1, function_pointer);
   }

   static native void nglDrawTextureNV(int var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, long var11);
}
