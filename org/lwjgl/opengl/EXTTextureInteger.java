package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTTextureInteger {
   public static final int GL_RGBA_INTEGER_MODE_EXT = 36254;
   public static final int GL_RGBA32UI_EXT = 36208;
   public static final int GL_RGB32UI_EXT = 36209;
   public static final int GL_ALPHA32UI_EXT = 36210;
   public static final int GL_INTENSITY32UI_EXT = 36211;
   public static final int GL_LUMINANCE32UI_EXT = 36212;
   public static final int GL_LUMINANCE_ALPHA32UI_EXT = 36213;
   public static final int GL_RGBA16UI_EXT = 36214;
   public static final int GL_RGB16UI_EXT = 36215;
   public static final int GL_ALPHA16UI_EXT = 36216;
   public static final int GL_INTENSITY16UI_EXT = 36217;
   public static final int GL_LUMINANCE16UI_EXT = 36218;
   public static final int GL_LUMINANCE_ALPHA16UI_EXT = 36219;
   public static final int GL_RGBA8UI_EXT = 36220;
   public static final int GL_RGB8UI_EXT = 36221;
   public static final int GL_ALPHA8UI_EXT = 36222;
   public static final int GL_INTENSITY8UI_EXT = 36223;
   public static final int GL_LUMINANCE8UI_EXT = 36224;
   public static final int GL_LUMINANCE_ALPHA8UI_EXT = 36225;
   public static final int GL_RGBA32I_EXT = 36226;
   public static final int GL_RGB32I_EXT = 36227;
   public static final int GL_ALPHA32I_EXT = 36228;
   public static final int GL_INTENSITY32I_EXT = 36229;
   public static final int GL_LUMINANCE32I_EXT = 36230;
   public static final int GL_LUMINANCE_ALPHA32I_EXT = 36231;
   public static final int GL_RGBA16I_EXT = 36232;
   public static final int GL_RGB16I_EXT = 36233;
   public static final int GL_ALPHA16I_EXT = 36234;
   public static final int GL_INTENSITY16I_EXT = 36235;
   public static final int GL_LUMINANCE16I_EXT = 36236;
   public static final int GL_LUMINANCE_ALPHA16I_EXT = 36237;
   public static final int GL_RGBA8I_EXT = 36238;
   public static final int GL_RGB8I_EXT = 36239;
   public static final int GL_ALPHA8I_EXT = 36240;
   public static final int GL_INTENSITY8I_EXT = 36241;
   public static final int GL_LUMINANCE8I_EXT = 36242;
   public static final int GL_LUMINANCE_ALPHA8I_EXT = 36243;
   public static final int GL_RED_INTEGER_EXT = 36244;
   public static final int GL_GREEN_INTEGER_EXT = 36245;
   public static final int GL_BLUE_INTEGER_EXT = 36246;
   public static final int GL_ALPHA_INTEGER_EXT = 36247;
   public static final int GL_RGB_INTEGER_EXT = 36248;
   public static final int GL_RGBA_INTEGER_EXT = 36249;
   public static final int GL_BGR_INTEGER_EXT = 36250;
   public static final int GL_BGRA_INTEGER_EXT = 36251;
   public static final int GL_LUMINANCE_INTEGER_EXT = 36252;
   public static final int GL_LUMINANCE_ALPHA_INTEGER_EXT = 36253;

   public static void glClearColorIiEXT(int r, int g, int b, int a) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearColorIiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearColorIiEXT(r, g, b, a, function_pointer);
   }

   static native void nglClearColorIiEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glClearColorIuiEXT(int r, int g, int b, int a) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearColorIuiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClearColorIuiEXT(r, g, b, a, function_pointer);
   }

   static native void nglClearColorIuiEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glTexParameterIEXT(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTexParameterIivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexParameterIivEXT(int var0, int var1, long var2, long var4);

   public static void glTexParameterIiEXT(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexParameterIivEXT(target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glTexParameterIuEXT(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTexParameterIuivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTexParameterIuivEXT(int var0, int var1, long var2, long var4);

   public static void glTexParameterIuiEXT(int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexParameterIuivEXT(target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glGetTexParameterIEXT(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexParameterIivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexParameterIivEXT(int var0, int var1, long var2, long var4);

   public static int glGetTexParameterIiEXT(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexParameterIivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTexParameterIuEXT(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTexParameterIuivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTexParameterIuivEXT(int var0, int var1, long var2, long var4);

   public static int glGetTexParameterIuiEXT(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTexParameterIuivEXT(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }
}
