package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVVideoCapture {
   public static final int GL_VIDEO_BUFFER_NV = 36896;
   public static final int GL_VIDEO_BUFFER_BINDING_NV = 36897;
   public static final int GL_FIELD_UPPER_NV = 36898;
   public static final int GL_FIELD_LOWER_NV = 36899;
   public static final int GL_NUM_VIDEO_CAPTURE_STREAMS_NV = 36900;
   public static final int GL_NEXT_VIDEO_CAPTURE_BUFFER_STATUS_NV = 36901;
   public static final int GL_LAST_VIDEO_CAPTURE_STATUS_NV = 36903;
   public static final int GL_VIDEO_BUFFER_PITCH_NV = 36904;
   public static final int GL_VIDEO_CAPTURE_FRAME_WIDTH_NV = 36920;
   public static final int GL_VIDEO_CAPTURE_FRAME_HEIGHT_NV = 36921;
   public static final int GL_VIDEO_CAPTURE_FIELD_UPPER_HEIGHT_NV = 36922;
   public static final int GL_VIDEO_CAPTURE_FIELD_LOWER_HEIGHT_NV = 36923;
   public static final int GL_VIDEO_CAPTURE_TO_422_SUPPORTED_NV = 36902;
   public static final int GL_VIDEO_COLOR_CONVERSION_MATRIX_NV = 36905;
   public static final int GL_VIDEO_COLOR_CONVERSION_MAX_NV = 36906;
   public static final int GL_VIDEO_COLOR_CONVERSION_MIN_NV = 36907;
   public static final int GL_VIDEO_COLOR_CONVERSION_OFFSET_NV = 36908;
   public static final int GL_VIDEO_BUFFER_INTERNAL_FORMAT_NV = 36909;
   public static final int GL_VIDEO_CAPTURE_SURFACE_ORIGIN_NV = 36924;
   public static final int GL_PARTIAL_SUCCESS_NV = 36910;
   public static final int GL_SUCCESS_NV = 36911;
   public static final int GL_FAILURE_NV = 36912;
   public static final int GL_YCBYCR8_422_NV = 36913;
   public static final int GL_YCBAYCR8A_4224_NV = 36914;
   public static final int GL_Z6Y10Z6CB10Z6Y10Z6CR10_422_NV = 36915;
   public static final int GL_Z6Y10Z6CB10Z6A10Z6Y10Z6CR10Z6A10_4224_NV = 36916;
   public static final int GL_Z4Y12Z4CB12Z4Y12Z4CR12_422_NV = 36917;
   public static final int GL_Z4Y12Z4CB12Z4A12Z4Y12Z4CR12Z4A12_4224_NV = 36918;
   public static final int GL_Z4Y12Z4CB12Z4CR12_444_NV = 36919;
   public static final int GL_NUM_VIDEO_CAPTURE_SLOTS_NV = 8399;
   public static final int GL_UNIQUE_ID_NV = 8398;

   public static void glBeginVideoCaptureNV(int video_capture_slot) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginVideoCaptureNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginVideoCaptureNV(video_capture_slot, function_pointer);
   }

   static native void nglBeginVideoCaptureNV(int var0, long var1);

   public static void glBindVideoCaptureStreamBufferNV(int video_capture_slot, int stream, int frame_region, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindVideoCaptureStreamBufferNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindVideoCaptureStreamBufferNV(video_capture_slot, stream, frame_region, offset, function_pointer);
   }

   static native void nglBindVideoCaptureStreamBufferNV(int var0, int var1, int var2, long var3, long var5);

   public static void glBindVideoCaptureStreamTextureNV(int video_capture_slot, int stream, int frame_region, int target, int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindVideoCaptureStreamTextureNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindVideoCaptureStreamTextureNV(video_capture_slot, stream, frame_region, target, texture, function_pointer);
   }

   static native void nglBindVideoCaptureStreamTextureNV(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glEndVideoCaptureNV(int video_capture_slot) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndVideoCaptureNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndVideoCaptureNV(video_capture_slot, function_pointer);
   }

   static native void nglEndVideoCaptureNV(int var0, long var1);

   public static void glGetVideoCaptureNV(int video_capture_slot, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetVideoCaptureivNV(video_capture_slot, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVideoCaptureivNV(int var0, int var1, long var2, long var4);

   public static int glGetVideoCaptureiNV(int video_capture_slot, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetVideoCaptureivNV(video_capture_slot, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetVideoCaptureStreamNV(int video_capture_slot, int stream, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetVideoCaptureStreamivNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVideoCaptureStreamivNV(int var0, int var1, int var2, long var3, long var5);

   public static int glGetVideoCaptureStreamiNV(int video_capture_slot, int stream, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetVideoCaptureStreamivNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetVideoCaptureStreamNV(int video_capture_slot, int stream, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 1);
      nglGetVideoCaptureStreamfvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVideoCaptureStreamfvNV(int var0, int var1, int var2, long var3, long var5);

   public static float glGetVideoCaptureStreamfNV(int video_capture_slot, int stream, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetVideoCaptureStreamfvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetVideoCaptureStreamNV(int video_capture_slot, int stream, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamdvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 1);
      nglGetVideoCaptureStreamdvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVideoCaptureStreamdvNV(int var0, int var1, int var2, long var3, long var5);

   public static double glGetVideoCaptureStreamdNV(int video_capture_slot, int stream, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVideoCaptureStreamdvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      DoubleBuffer params = APIUtil.getBufferDouble(caps);
      nglGetVideoCaptureStreamdvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static int glVideoCaptureNV(int video_capture_slot, IntBuffer sequence_num, LongBuffer capture_time) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVideoCaptureNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)sequence_num, 1);
      BufferChecks.checkBuffer((LongBuffer)capture_time, 1);
      int __result = nglVideoCaptureNV(video_capture_slot, MemoryUtil.getAddress(sequence_num), MemoryUtil.getAddress(capture_time), function_pointer);
      return __result;
   }

   static native int nglVideoCaptureNV(int var0, long var1, long var3, long var5);

   public static void glVideoCaptureStreamParameterNV(int video_capture_slot, int stream, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVideoCaptureStreamParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 16);
      nglVideoCaptureStreamParameterivNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglVideoCaptureStreamParameterivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glVideoCaptureStreamParameterNV(int video_capture_slot, int stream, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVideoCaptureStreamParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 16);
      nglVideoCaptureStreamParameterfvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglVideoCaptureStreamParameterfvNV(int var0, int var1, int var2, long var3, long var5);

   public static void glVideoCaptureStreamParameterNV(int video_capture_slot, int stream, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVideoCaptureStreamParameterdvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 16);
      nglVideoCaptureStreamParameterdvNV(video_capture_slot, stream, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglVideoCaptureStreamParameterdvNV(int var0, int var1, int var2, long var3, long var5);
}
