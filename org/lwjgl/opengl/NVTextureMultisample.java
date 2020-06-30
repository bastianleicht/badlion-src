package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVTextureMultisample {
   public static final int GL_TEXTURE_COVERAGE_SAMPLES_NV = 36933;
   public static final int GL_TEXTURE_COLOR_SAMPLES_NV = 36934;

   public static void glTexImage2DMultisampleCoverageNV(int target, int coverageSamples, int colorSamples, int internalFormat, int width, int height, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2DMultisampleCoverageNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexImage2DMultisampleCoverageNV(target, coverageSamples, colorSamples, internalFormat, width, height, fixedSampleLocations, function_pointer);
   }

   static native void nglTexImage2DMultisampleCoverageNV(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, long var7);

   public static void glTexImage3DMultisampleCoverageNV(int target, int coverageSamples, int colorSamples, int internalFormat, int width, int height, int depth, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3DMultisampleCoverageNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexImage3DMultisampleCoverageNV(target, coverageSamples, colorSamples, internalFormat, width, height, depth, fixedSampleLocations, function_pointer);
   }

   static native void nglTexImage3DMultisampleCoverageNV(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, long var8);

   public static void glTextureImage2DMultisampleNV(int texture, int target, int samples, int internalFormat, int width, int height, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DMultisampleNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureImage2DMultisampleNV(texture, target, samples, internalFormat, width, height, fixedSampleLocations, function_pointer);
   }

   static native void nglTextureImage2DMultisampleNV(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, long var7);

   public static void glTextureImage3DMultisampleNV(int texture, int target, int samples, int internalFormat, int width, int height, int depth, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DMultisampleNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureImage3DMultisampleNV(texture, target, samples, internalFormat, width, height, depth, fixedSampleLocations, function_pointer);
   }

   static native void nglTextureImage3DMultisampleNV(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, long var8);

   public static void glTextureImage2DMultisampleCoverageNV(int texture, int target, int coverageSamples, int colorSamples, int internalFormat, int width, int height, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DMultisampleCoverageNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureImage2DMultisampleCoverageNV(texture, target, coverageSamples, colorSamples, internalFormat, width, height, fixedSampleLocations, function_pointer);
   }

   static native void nglTextureImage2DMultisampleCoverageNV(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, long var8);

   public static void glTextureImage3DMultisampleCoverageNV(int texture, int target, int coverageSamples, int colorSamples, int internalFormat, int width, int height, int depth, boolean fixedSampleLocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DMultisampleCoverageNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureImage3DMultisampleCoverageNV(texture, target, coverageSamples, colorSamples, internalFormat, width, height, depth, fixedSampleLocations, function_pointer);
   }

   static native void nglTextureImage3DMultisampleCoverageNV(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, long var9);
}
