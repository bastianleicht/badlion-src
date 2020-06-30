package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ATIVertexStreams {
   public static final int GL_MAX_VERTEX_STREAMS_ATI = 34667;
   public static final int GL_VERTEX_SOURCE_ATI = 34668;
   public static final int GL_VERTEX_STREAM0_ATI = 34669;
   public static final int GL_VERTEX_STREAM1_ATI = 34670;
   public static final int GL_VERTEX_STREAM2_ATI = 34671;
   public static final int GL_VERTEX_STREAM3_ATI = 34672;
   public static final int GL_VERTEX_STREAM4_ATI = 34673;
   public static final int GL_VERTEX_STREAM5_ATI = 34674;
   public static final int GL_VERTEX_STREAM6_ATI = 34675;
   public static final int GL_VERTEX_STREAM7_ATI = 34676;

   public static void glVertexStream2fATI(int stream, float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream2fATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream2fATI(stream, x, y, function_pointer);
   }

   static native void nglVertexStream2fATI(int var0, float var1, float var2, long var3);

   public static void glVertexStream2dATI(int stream, double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream2dATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream2dATI(stream, x, y, function_pointer);
   }

   static native void nglVertexStream2dATI(int var0, double var1, double var3, long var5);

   public static void glVertexStream2iATI(int stream, int x, int y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream2iATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream2iATI(stream, x, y, function_pointer);
   }

   static native void nglVertexStream2iATI(int var0, int var1, int var2, long var3);

   public static void glVertexStream2sATI(int stream, short x, short y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream2sATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream2sATI(stream, x, y, function_pointer);
   }

   static native void nglVertexStream2sATI(int var0, short var1, short var2, long var3);

   public static void glVertexStream3fATI(int stream, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream3fATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream3fATI(stream, x, y, z, function_pointer);
   }

   static native void nglVertexStream3fATI(int var0, float var1, float var2, float var3, long var4);

   public static void glVertexStream3dATI(int stream, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream3dATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream3dATI(stream, x, y, z, function_pointer);
   }

   static native void nglVertexStream3dATI(int var0, double var1, double var3, double var5, long var7);

   public static void glVertexStream3iATI(int stream, int x, int y, int z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream3iATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream3iATI(stream, x, y, z, function_pointer);
   }

   static native void nglVertexStream3iATI(int var0, int var1, int var2, int var3, long var4);

   public static void glVertexStream3sATI(int stream, short x, short y, short z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream3sATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream3sATI(stream, x, y, z, function_pointer);
   }

   static native void nglVertexStream3sATI(int var0, short var1, short var2, short var3, long var4);

   public static void glVertexStream4fATI(int stream, float x, float y, float z, float w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream4fATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream4fATI(stream, x, y, z, w, function_pointer);
   }

   static native void nglVertexStream4fATI(int var0, float var1, float var2, float var3, float var4, long var5);

   public static void glVertexStream4dATI(int stream, double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream4dATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream4dATI(stream, x, y, z, w, function_pointer);
   }

   static native void nglVertexStream4dATI(int var0, double var1, double var3, double var5, double var7, long var9);

   public static void glVertexStream4iATI(int stream, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream4iATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream4iATI(stream, x, y, z, w, function_pointer);
   }

   static native void nglVertexStream4iATI(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glVertexStream4sATI(int stream, short x, short y, short z, short w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexStream4sATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexStream4sATI(stream, x, y, z, w, function_pointer);
   }

   static native void nglVertexStream4sATI(int var0, short var1, short var2, short var3, short var4, long var5);

   public static void glNormalStream3bATI(int stream, byte x, byte y, byte z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalStream3bATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalStream3bATI(stream, x, y, z, function_pointer);
   }

   static native void nglNormalStream3bATI(int var0, byte var1, byte var2, byte var3, long var4);

   public static void glNormalStream3fATI(int stream, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalStream3fATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalStream3fATI(stream, x, y, z, function_pointer);
   }

   static native void nglNormalStream3fATI(int var0, float var1, float var2, float var3, long var4);

   public static void glNormalStream3dATI(int stream, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalStream3dATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalStream3dATI(stream, x, y, z, function_pointer);
   }

   static native void nglNormalStream3dATI(int var0, double var1, double var3, double var5, long var7);

   public static void glNormalStream3iATI(int stream, int x, int y, int z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalStream3iATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalStream3iATI(stream, x, y, z, function_pointer);
   }

   static native void nglNormalStream3iATI(int var0, int var1, int var2, int var3, long var4);

   public static void glNormalStream3sATI(int stream, short x, short y, short z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalStream3sATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalStream3sATI(stream, x, y, z, function_pointer);
   }

   static native void nglNormalStream3sATI(int var0, short var1, short var2, short var3, long var4);

   public static void glClientActiveVertexStreamATI(int stream) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClientActiveVertexStreamATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClientActiveVertexStreamATI(stream, function_pointer);
   }

   static native void nglClientActiveVertexStreamATI(int var0, long var1);

   public static void glVertexBlendEnvfATI(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexBlendEnvfATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexBlendEnvfATI(pname, param, function_pointer);
   }

   static native void nglVertexBlendEnvfATI(int var0, float var1, long var2);

   public static void glVertexBlendEnviATI(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexBlendEnviATI;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexBlendEnviATI(pname, param, function_pointer);
   }

   static native void nglVertexBlendEnviATI(int var0, int var1, long var2);
}
