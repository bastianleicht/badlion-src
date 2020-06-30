package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class APPLEElementArray {
   public static final int GL_ELEMENT_ARRAY_APPLE = 34664;
   public static final int GL_ELEMENT_ARRAY_TYPE_APPLE = 34665;
   public static final int GL_ELEMENT_ARRAY_POINTER_APPLE = 34666;

   public static void glElementPointerAPPLE(ByteBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glElementPointerAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pointer);
      nglElementPointerAPPLE(5121, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glElementPointerAPPLE(IntBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glElementPointerAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pointer);
      nglElementPointerAPPLE(5125, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glElementPointerAPPLE(ShortBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glElementPointerAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pointer);
      nglElementPointerAPPLE(5123, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglElementPointerAPPLE(int var0, long var1, long var3);

   public static void glDrawElementArrayAPPLE(int mode, int first, int count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementArrayAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawElementArrayAPPLE(mode, first, count, function_pointer);
   }

   static native void nglDrawElementArrayAPPLE(int var0, int var1, int var2, long var3);

   public static void glDrawRangeElementArrayAPPLE(int mode, int start, int end, int first, int count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElementArrayAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawRangeElementArrayAPPLE(mode, start, end, first, count, function_pointer);
   }

   static native void nglDrawRangeElementArrayAPPLE(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glMultiDrawElementArrayAPPLE(int mode, IntBuffer first, IntBuffer count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementArrayAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(first);
      BufferChecks.checkBuffer(count, first.remaining());
      nglMultiDrawElementArrayAPPLE(mode, MemoryUtil.getAddress(first), MemoryUtil.getAddress(count), first.remaining(), function_pointer);
   }

   static native void nglMultiDrawElementArrayAPPLE(int var0, long var1, long var3, int var5, long var6);

   public static void glMultiDrawRangeElementArrayAPPLE(int mode, int start, int end, IntBuffer first, IntBuffer count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawRangeElementArrayAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(first);
      BufferChecks.checkBuffer(count, first.remaining());
      nglMultiDrawRangeElementArrayAPPLE(mode, start, end, MemoryUtil.getAddress(first), MemoryUtil.getAddress(count), first.remaining(), function_pointer);
   }

   static native void nglMultiDrawRangeElementArrayAPPLE(int var0, int var1, int var2, long var3, long var5, int var7, long var8);
}
