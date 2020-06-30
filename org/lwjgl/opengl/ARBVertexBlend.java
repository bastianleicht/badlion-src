package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class ARBVertexBlend {
   public static final int GL_MAX_VERTEX_UNITS_ARB = 34468;
   public static final int GL_ACTIVE_VERTEX_UNITS_ARB = 34469;
   public static final int GL_WEIGHT_SUM_UNITY_ARB = 34470;
   public static final int GL_VERTEX_BLEND_ARB = 34471;
   public static final int GL_CURRENT_WEIGHT_ARB = 34472;
   public static final int GL_WEIGHT_ARRAY_TYPE_ARB = 34473;
   public static final int GL_WEIGHT_ARRAY_STRIDE_ARB = 34474;
   public static final int GL_WEIGHT_ARRAY_SIZE_ARB = 34475;
   public static final int GL_WEIGHT_ARRAY_POINTER_ARB = 34476;
   public static final int GL_WEIGHT_ARRAY_ARB = 34477;
   public static final int GL_MODELVIEW0_ARB = 5888;
   public static final int GL_MODELVIEW1_ARB = 34058;
   public static final int GL_MODELVIEW2_ARB = 34594;
   public static final int GL_MODELVIEW3_ARB = 34595;
   public static final int GL_MODELVIEW4_ARB = 34596;
   public static final int GL_MODELVIEW5_ARB = 34597;
   public static final int GL_MODELVIEW6_ARB = 34598;
   public static final int GL_MODELVIEW7_ARB = 34599;
   public static final int GL_MODELVIEW8_ARB = 34600;
   public static final int GL_MODELVIEW9_ARB = 34601;
   public static final int GL_MODELVIEW10_ARB = 34602;
   public static final int GL_MODELVIEW11_ARB = 34603;
   public static final int GL_MODELVIEW12_ARB = 34604;
   public static final int GL_MODELVIEW13_ARB = 34605;
   public static final int GL_MODELVIEW14_ARB = 34606;
   public static final int GL_MODELVIEW15_ARB = 34607;
   public static final int GL_MODELVIEW16_ARB = 34608;
   public static final int GL_MODELVIEW17_ARB = 34609;
   public static final int GL_MODELVIEW18_ARB = 34610;
   public static final int GL_MODELVIEW19_ARB = 34611;
   public static final int GL_MODELVIEW20_ARB = 34612;
   public static final int GL_MODELVIEW21_ARB = 34613;
   public static final int GL_MODELVIEW22_ARB = 34614;
   public static final int GL_MODELVIEW23_ARB = 34615;
   public static final int GL_MODELVIEW24_ARB = 34616;
   public static final int GL_MODELVIEW25_ARB = 34617;
   public static final int GL_MODELVIEW26_ARB = 34618;
   public static final int GL_MODELVIEW27_ARB = 34619;
   public static final int GL_MODELVIEW28_ARB = 34620;
   public static final int GL_MODELVIEW29_ARB = 34621;
   public static final int GL_MODELVIEW30_ARB = 34622;
   public static final int GL_MODELVIEW31_ARB = 34623;

   public static void glWeightARB(ByteBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightbvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightbvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightbvARB(int var0, long var1, long var3);

   public static void glWeightARB(ShortBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightsvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightsvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightsvARB(int var0, long var1, long var3);

   public static void glWeightARB(IntBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightivARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightivARB(int var0, long var1, long var3);

   public static void glWeightARB(FloatBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightfvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightfvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightfvARB(int var0, long var1, long var3);

   public static void glWeightARB(DoubleBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightdvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightdvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightdvARB(int var0, long var1, long var3);

   public static void glWeightuARB(ByteBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightubvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightubvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightubvARB(int var0, long var1, long var3);

   public static void glWeightuARB(ShortBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightusvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightusvARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightusvARB(int var0, long var1, long var3);

   public static void glWeightuARB(IntBuffer pWeights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightuivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pWeights);
      nglWeightuivARB(pWeights.remaining(), MemoryUtil.getAddress(pWeights), function_pointer);
   }

   static native void nglWeightuivARB(int var0, long var1, long var3);

   public static void glWeightPointerARB(int size, int stride, DoubleBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).ARB_vertex_blend_glWeightPointerARB_pPointer = pPointer;
      }

      nglWeightPointerARB(size, 5130, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   public static void glWeightPointerARB(int size, int stride, FloatBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).ARB_vertex_blend_glWeightPointerARB_pPointer = pPointer;
      }

      nglWeightPointerARB(size, 5126, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   public static void glWeightPointerARB(int size, boolean unsigned, int stride, ByteBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).ARB_vertex_blend_glWeightPointerARB_pPointer = pPointer;
      }

      nglWeightPointerARB(size, unsigned?5121:5120, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   public static void glWeightPointerARB(int size, boolean unsigned, int stride, IntBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).ARB_vertex_blend_glWeightPointerARB_pPointer = pPointer;
      }

      nglWeightPointerARB(size, unsigned?5125:5124, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   public static void glWeightPointerARB(int size, boolean unsigned, int stride, ShortBuffer pPointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pPointer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).ARB_vertex_blend_glWeightPointerARB_pPointer = pPointer;
      }

      nglWeightPointerARB(size, unsigned?5123:5122, stride, MemoryUtil.getAddress(pPointer), function_pointer);
   }

   static native void nglWeightPointerARB(int var0, int var1, int var2, long var3, long var5);

   public static void glWeightPointerARB(int size, int type, int stride, long pPointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglWeightPointerARBBO(size, type, stride, pPointer_buffer_offset, function_pointer);
   }

   static native void nglWeightPointerARBBO(int var0, int var1, int var2, long var3, long var5);

   public static void glVertexBlendARB(int count) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexBlendARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexBlendARB(count, function_pointer);
   }

   static native void nglVertexBlendARB(int var0, long var1);
}
