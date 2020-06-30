package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.AMDDebugOutputCallback;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.CallbackUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class AMDDebugOutput {
   public static final int GL_MAX_DEBUG_MESSAGE_LENGTH_AMD = 37187;
   public static final int GL_MAX_DEBUG_LOGGED_MESSAGES_AMD = 37188;
   public static final int GL_DEBUG_LOGGED_MESSAGES_AMD = 37189;
   public static final int GL_DEBUG_SEVERITY_HIGH_AMD = 37190;
   public static final int GL_DEBUG_SEVERITY_MEDIUM_AMD = 37191;
   public static final int GL_DEBUG_SEVERITY_LOW_AMD = 37192;
   public static final int GL_DEBUG_CATEGORY_API_ERROR_AMD = 37193;
   public static final int GL_DEBUG_CATEGORY_WINDOW_SYSTEM_AMD = 37194;
   public static final int GL_DEBUG_CATEGORY_DEPRECATION_AMD = 37195;
   public static final int GL_DEBUG_CATEGORY_UNDEFINED_BEHAVIOR_AMD = 37196;
   public static final int GL_DEBUG_CATEGORY_PERFORMANCE_AMD = 37197;
   public static final int GL_DEBUG_CATEGORY_SHADER_COMPILER_AMD = 37198;
   public static final int GL_DEBUG_CATEGORY_APPLICATION_AMD = 37199;
   public static final int GL_DEBUG_CATEGORY_OTHER_AMD = 37200;

   public static void glDebugMessageEnableAMD(int category, int severity, IntBuffer ids, boolean enabled) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageEnableAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(ids != null) {
         BufferChecks.checkDirect(ids);
      }

      nglDebugMessageEnableAMD(category, severity, ids == null?0:ids.remaining(), MemoryUtil.getAddressSafe(ids), enabled, function_pointer);
   }

   static native void nglDebugMessageEnableAMD(int var0, int var1, int var2, long var3, boolean var5, long var6);

   public static void glDebugMessageInsertAMD(int category, int severity, int id, ByteBuffer buf) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageInsertAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buf);
      nglDebugMessageInsertAMD(category, severity, id, buf.remaining(), MemoryUtil.getAddress(buf), function_pointer);
   }

   static native void nglDebugMessageInsertAMD(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glDebugMessageInsertAMD(int category, int severity, int id, CharSequence buf) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageInsertAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDebugMessageInsertAMD(category, severity, id, buf.length(), APIUtil.getBuffer(caps, buf), function_pointer);
   }

   public static void glDebugMessageCallbackAMD(AMDDebugOutputCallback callback) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDebugMessageCallbackAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      long userParam = callback == null?0L:CallbackUtil.createGlobalRef(callback.getHandler());
      CallbackUtil.registerContextCallbackAMD(userParam);
      nglDebugMessageCallbackAMD(callback == null?0L:callback.getPointer(), userParam, function_pointer);
   }

   static native void nglDebugMessageCallbackAMD(long var0, long var2, long var4);

   public static int glGetDebugMessageLogAMD(int count, IntBuffer categories, IntBuffer severities, IntBuffer ids, IntBuffer lengths, ByteBuffer messageLog) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDebugMessageLogAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(categories != null) {
         BufferChecks.checkBuffer(categories, count);
      }

      if(severities != null) {
         BufferChecks.checkBuffer(severities, count);
      }

      if(ids != null) {
         BufferChecks.checkBuffer(ids, count);
      }

      if(lengths != null) {
         BufferChecks.checkBuffer(lengths, count);
      }

      if(messageLog != null) {
         BufferChecks.checkDirect(messageLog);
      }

      int __result = nglGetDebugMessageLogAMD(count, messageLog == null?0:messageLog.remaining(), MemoryUtil.getAddressSafe(categories), MemoryUtil.getAddressSafe(severities), MemoryUtil.getAddressSafe(ids), MemoryUtil.getAddressSafe(lengths), MemoryUtil.getAddressSafe(messageLog), function_pointer);
      return __result;
   }

   static native int nglGetDebugMessageLogAMD(int var0, int var1, long var2, long var4, long var6, long var8, long var10, long var12);
}
