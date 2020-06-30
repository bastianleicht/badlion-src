package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTDrawBuffers2 {
   public static void glColorMaskIndexedEXT(int buf, boolean r, boolean g, boolean b, boolean a) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorMaskIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColorMaskIndexedEXT(buf, r, g, b, a, function_pointer);
   }

   static native void nglColorMaskIndexedEXT(int var0, boolean var1, boolean var2, boolean var3, boolean var4, long var5);

   public static void glGetBooleanIndexedEXT(int value, int index, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBooleanIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)data, 4);
      nglGetBooleanIndexedvEXT(value, index, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetBooleanIndexedvEXT(int var0, int var1, long var2, long var4);

   public static boolean glGetBooleanIndexedEXT(int value, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBooleanIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer data = APIUtil.getBufferByte(caps, 1);
      nglGetBooleanIndexedvEXT(value, index, MemoryUtil.getAddress(data), function_pointer);
      return data.get(0) == 1;
   }

   public static void glGetIntegerIndexedEXT(int value, int index, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)data, 4);
      nglGetIntegerIndexedvEXT(value, index, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetIntegerIndexedvEXT(int var0, int var1, long var2, long var4);

   public static int glGetIntegerIndexedEXT(int value, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer data = APIUtil.getBufferInt(caps);
      nglGetIntegerIndexedvEXT(value, index, MemoryUtil.getAddress(data), function_pointer);
      return data.get(0);
   }

   public static void glEnableIndexedEXT(int target, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableIndexedEXT(target, index, function_pointer);
   }

   static native void nglEnableIndexedEXT(int var0, int var1, long var2);

   public static void glDisableIndexedEXT(int target, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableIndexedEXT(target, index, function_pointer);
   }

   static native void nglDisableIndexedEXT(int var0, int var1, long var2);

   public static boolean glIsEnabledIndexedEXT(int target, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsEnabledIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsEnabledIndexedEXT(target, index, function_pointer);
      return __result;
   }

   static native boolean nglIsEnabledIndexedEXT(int var0, int var1, long var2);
}
