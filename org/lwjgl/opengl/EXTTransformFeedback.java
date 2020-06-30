package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class EXTTransformFeedback {
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_EXT = 35982;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_START_EXT = 35972;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_SIZE_EXT = 35973;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING_EXT = 35983;
   public static final int GL_INTERLEAVED_ATTRIBS_EXT = 35980;
   public static final int GL_SEPARATE_ATTRIBS_EXT = 35981;
   public static final int GL_PRIMITIVES_GENERATED_EXT = 35975;
   public static final int GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_EXT = 35976;
   public static final int GL_RASTERIZER_DISCARD_EXT = 35977;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS_EXT = 35978;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS_EXT = 35979;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS_EXT = 35968;
   public static final int GL_TRANSFORM_FEEDBACK_VARYINGS_EXT = 35971;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_MODE_EXT = 35967;
   public static final int GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH_EXT = 35958;

   public static void glBindBufferRangeEXT(int target, int index, int buffer, long offset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferRangeEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferRangeEXT(target, index, buffer, offset, size, function_pointer);
   }

   static native void nglBindBufferRangeEXT(int var0, int var1, int var2, long var3, long var5, long var7);

   public static void glBindBufferOffsetEXT(int target, int index, int buffer, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferOffsetEXT(target, index, buffer, offset, function_pointer);
   }

   static native void nglBindBufferOffsetEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glBindBufferBaseEXT(int target, int index, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferBaseEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferBaseEXT(target, index, buffer, function_pointer);
   }

   static native void nglBindBufferBaseEXT(int var0, int var1, int var2, long var3);

   public static void glBeginTransformFeedbackEXT(int primitiveMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginTransformFeedbackEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginTransformFeedbackEXT(primitiveMode, function_pointer);
   }

   static native void nglBeginTransformFeedbackEXT(int var0, long var1);

   public static void glEndTransformFeedbackEXT() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndTransformFeedbackEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndTransformFeedbackEXT(function_pointer);
   }

   static native void nglEndTransformFeedbackEXT(long var0);

   public static void glTransformFeedbackVaryingsEXT(int program, int count, ByteBuffer varyings, int bufferMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTransformFeedbackVaryingsEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(varyings);
      BufferChecks.checkNullTerminated(varyings, count);
      nglTransformFeedbackVaryingsEXT(program, count, MemoryUtil.getAddress(varyings), bufferMode, function_pointer);
   }

   static native void nglTransformFeedbackVaryingsEXT(int var0, int var1, long var2, int var4, long var5);

   public static void glTransformFeedbackVaryingsEXT(int program, CharSequence[] varyings, int bufferMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTransformFeedbackVaryingsEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkArray(varyings);
      nglTransformFeedbackVaryingsEXT(program, varyings.length, APIUtil.getBufferNT(caps, varyings), bufferMode, function_pointer);
   }

   public static void glGetTransformFeedbackVaryingEXT(int program, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTransformFeedbackVaryingEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkBuffer((IntBuffer)size, 1);
      BufferChecks.checkBuffer((IntBuffer)type, 1);
      BufferChecks.checkDirect(name);
      nglGetTransformFeedbackVaryingEXT(program, index, name.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(size), MemoryUtil.getAddress(type), MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglGetTransformFeedbackVaryingEXT(int var0, int var1, int var2, long var3, long var5, long var7, long var9, long var11);

   public static String glGetTransformFeedbackVaryingEXT(int program, int index, int bufSize, IntBuffer size, IntBuffer type) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTransformFeedbackVaryingEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)size, 1);
      BufferChecks.checkBuffer((IntBuffer)type, 1);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, bufSize);
      nglGetTransformFeedbackVaryingEXT(program, index, bufSize, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress(size), MemoryUtil.getAddress(type), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }
}
