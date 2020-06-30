package org.lwjgl.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLException;
import org.lwjgl.MemoryUtil;

public final class AL11 {
   public static final int AL_SEC_OFFSET = 4132;
   public static final int AL_SAMPLE_OFFSET = 4133;
   public static final int AL_BYTE_OFFSET = 4134;
   public static final int AL_STATIC = 4136;
   public static final int AL_STREAMING = 4137;
   public static final int AL_UNDETERMINED = 4144;
   public static final int AL_ILLEGAL_COMMAND = 40964;
   public static final int AL_SPEED_OF_SOUND = 49155;
   public static final int AL_LINEAR_DISTANCE = 53251;
   public static final int AL_LINEAR_DISTANCE_CLAMPED = 53252;
   public static final int AL_EXPONENT_DISTANCE = 53253;
   public static final int AL_EXPONENT_DISTANCE_CLAMPED = 53254;

   static native void initNativeStubs() throws LWJGLException;

   public static void alListener3i(int pname, int v1, int v2, int v3) {
      nalListener3i(pname, v1, v2, v3);
   }

   static native void nalListener3i(int var0, int var1, int var2, int var3);

   public static void alGetListeneri(int pname, FloatBuffer intdata) {
      BufferChecks.checkBuffer((FloatBuffer)intdata, 1);
      nalGetListeneriv(pname, MemoryUtil.getAddress(intdata));
   }

   static native void nalGetListeneriv(int var0, long var1);

   public static void alSource3i(int source, int pname, int v1, int v2, int v3) {
      nalSource3i(source, pname, v1, v2, v3);
   }

   static native void nalSource3i(int var0, int var1, int var2, int var3, int var4);

   public static void alSource(int source, int pname, IntBuffer value) {
      BufferChecks.checkBuffer((IntBuffer)value, 1);
      nalSourceiv(source, pname, MemoryUtil.getAddress(value));
   }

   static native void nalSourceiv(int var0, int var1, long var2);

   public static void alBufferf(int buffer, int pname, float value) {
      nalBufferf(buffer, pname, value);
   }

   static native void nalBufferf(int var0, int var1, float var2);

   public static void alBuffer3f(int buffer, int pname, float v1, float v2, float v3) {
      nalBuffer3f(buffer, pname, v1, v2, v3);
   }

   static native void nalBuffer3f(int var0, int var1, float var2, float var3, float var4);

   public static void alBuffer(int buffer, int pname, FloatBuffer value) {
      BufferChecks.checkBuffer((FloatBuffer)value, 1);
      nalBufferfv(buffer, pname, MemoryUtil.getAddress(value));
   }

   static native void nalBufferfv(int var0, int var1, long var2);

   public static void alBufferi(int buffer, int pname, int value) {
      nalBufferi(buffer, pname, value);
   }

   static native void nalBufferi(int var0, int var1, int var2);

   public static void alBuffer3i(int buffer, int pname, int v1, int v2, int v3) {
      nalBuffer3i(buffer, pname, v1, v2, v3);
   }

   static native void nalBuffer3i(int var0, int var1, int var2, int var3, int var4);

   public static void alBuffer(int buffer, int pname, IntBuffer value) {
      BufferChecks.checkBuffer((IntBuffer)value, 1);
      nalBufferiv(buffer, pname, MemoryUtil.getAddress(value));
   }

   static native void nalBufferiv(int var0, int var1, long var2);

   public static int alGetBufferi(int buffer, int pname) {
      int __result = nalGetBufferi(buffer, pname);
      return __result;
   }

   static native int nalGetBufferi(int var0, int var1);

   public static void alGetBuffer(int buffer, int pname, IntBuffer values) {
      BufferChecks.checkBuffer((IntBuffer)values, 1);
      nalGetBufferiv(buffer, pname, MemoryUtil.getAddress(values));
   }

   static native void nalGetBufferiv(int var0, int var1, long var2);

   public static float alGetBufferf(int buffer, int pname) {
      float __result = nalGetBufferf(buffer, pname);
      return __result;
   }

   static native float nalGetBufferf(int var0, int var1);

   public static void alGetBuffer(int buffer, int pname, FloatBuffer values) {
      BufferChecks.checkBuffer((FloatBuffer)values, 1);
      nalGetBufferfv(buffer, pname, MemoryUtil.getAddress(values));
   }

   static native void nalGetBufferfv(int var0, int var1, long var2);

   public static void alSpeedOfSound(float value) {
      nalSpeedOfSound(value);
   }

   static native void nalSpeedOfSound(float var0);
}
