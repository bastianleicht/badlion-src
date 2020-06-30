package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVTransformFeedback2 {
   public static final int GL_TRANSFORM_FEEDBACK_NV = 36386;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_PAUSED_NV = 36387;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_ACTIVE_NV = 36388;
   public static final int GL_TRANSFORM_FEEDBACK_BINDING_NV = 36389;

   public static void glBindTransformFeedbackNV(int target, int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindTransformFeedbackNV(target, id, function_pointer);
   }

   static native void nglBindTransformFeedbackNV(int var0, int var1, long var2);

   public static void glDeleteTransformFeedbacksNV(IntBuffer ids) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteTransformFeedbacksNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ids);
      nglDeleteTransformFeedbacksNV(ids.remaining(), MemoryUtil.getAddress(ids), function_pointer);
   }

   static native void nglDeleteTransformFeedbacksNV(int var0, long var1, long var3);

   public static void glDeleteTransformFeedbacksNV(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteTransformFeedbacksNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteTransformFeedbacksNV(1, APIUtil.getInt(caps, id), function_pointer);
   }

   public static void glGenTransformFeedbacksNV(IntBuffer ids) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenTransformFeedbacksNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ids);
      nglGenTransformFeedbacksNV(ids.remaining(), MemoryUtil.getAddress(ids), function_pointer);
   }

   static native void nglGenTransformFeedbacksNV(int var0, long var1, long var3);

   public static int glGenTransformFeedbacksNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenTransformFeedbacksNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer ids = APIUtil.getBufferInt(caps);
      nglGenTransformFeedbacksNV(1, MemoryUtil.getAddress(ids), function_pointer);
      return ids.get(0);
   }

   public static boolean glIsTransformFeedbackNV(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsTransformFeedbackNV(id, function_pointer);
      return __result;
   }

   static native boolean nglIsTransformFeedbackNV(int var0, long var1);

   public static void glPauseTransformFeedbackNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPauseTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPauseTransformFeedbackNV(function_pointer);
   }

   static native void nglPauseTransformFeedbackNV(long var0);

   public static void glResumeTransformFeedbackNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glResumeTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglResumeTransformFeedbackNV(function_pointer);
   }

   static native void nglResumeTransformFeedbackNV(long var0);

   public static void glDrawTransformFeedbackNV(int mode, int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawTransformFeedbackNV(mode, id, function_pointer);
   }

   static native void nglDrawTransformFeedbackNV(int var0, int var1, long var2);
}
