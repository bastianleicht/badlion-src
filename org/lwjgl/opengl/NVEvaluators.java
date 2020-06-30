package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVEvaluators {
   public static final int GL_EVAL_2D_NV = 34496;
   public static final int GL_EVAL_TRIANGULAR_2D_NV = 34497;
   public static final int GL_MAP_TESSELLATION_NV = 34498;
   public static final int GL_MAP_ATTRIB_U_ORDER_NV = 34499;
   public static final int GL_MAP_ATTRIB_V_ORDER_NV = 34500;
   public static final int GL_EVAL_FRACTIONAL_TESSELLATION_NV = 34501;
   public static final int GL_EVAL_VERTEX_ATTRIB0_NV = 34502;
   public static final int GL_EVAL_VERTEX_ATTRIB1_NV = 34503;
   public static final int GL_EVAL_VERTEX_ATTRIB2_NV = 34504;
   public static final int GL_EVAL_VERTEX_ATTRIB3_NV = 34505;
   public static final int GL_EVAL_VERTEX_ATTRIB4_NV = 34506;
   public static final int GL_EVAL_VERTEX_ATTRIB5_NV = 34507;
   public static final int GL_EVAL_VERTEX_ATTRIB6_NV = 34508;
   public static final int GL_EVAL_VERTEX_ATTRIB7_NV = 34509;
   public static final int GL_EVAL_VERTEX_ATTRIB8_NV = 34510;
   public static final int GL_EVAL_VERTEX_ATTRIB9_NV = 34511;
   public static final int GL_EVAL_VERTEX_ATTRIB10_NV = 34512;
   public static final int GL_EVAL_VERTEX_ATTRIB11_NV = 34513;
   public static final int GL_EVAL_VERTEX_ATTRIB12_NV = 34514;
   public static final int GL_EVAL_VERTEX_ATTRIB13_NV = 34515;
   public static final int GL_EVAL_VERTEX_ATTRIB14_NV = 34516;
   public static final int GL_EVAL_VERTEX_ATTRIB15_NV = 34517;
   public static final int GL_MAX_MAP_TESSELLATION_NV = 34518;
   public static final int GL_MAX_RATIONAL_EVAL_ORDER_NV = 34519;

   public static void glGetMapControlPointsNV(int target, int index, int type, int ustride, int vstride, boolean packed, FloatBuffer pPoints) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapControlPointsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pPoints);
      nglGetMapControlPointsNV(target, index, type, ustride, vstride, packed, MemoryUtil.getAddress(pPoints), function_pointer);
   }

   static native void nglGetMapControlPointsNV(int var0, int var1, int var2, int var3, int var4, boolean var5, long var6, long var8);

   public static void glMapControlPointsNV(int target, int index, int type, int ustride, int vstride, int uorder, int vorder, boolean packed, FloatBuffer pPoints) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapControlPointsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pPoints);
      nglMapControlPointsNV(target, index, type, ustride, vstride, uorder, vorder, packed, MemoryUtil.getAddress(pPoints), function_pointer);
   }

   static native void nglMapControlPointsNV(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, long var8, long var10);

   public static void glMapParameterNV(int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglMapParameterfvNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMapParameterfvNV(int var0, int var1, long var2, long var4);

   public static void glMapParameterNV(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMapParameterivNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMapParameterivNV(int var0, int var1, long var2, long var4);

   public static void glGetMapParameterNV(int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMapParameterfvNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMapParameterfvNV(int var0, int var1, long var2, long var4);

   public static void glGetMapParameterNV(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMapParameterivNV(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMapParameterivNV(int var0, int var1, long var2, long var4);

   public static void glGetMapAttribParameterNV(int target, int index, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapAttribParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMapAttribParameterfvNV(target, index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMapAttribParameterfvNV(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMapAttribParameterNV(int target, int index, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMapAttribParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMapAttribParameterivNV(target, index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMapAttribParameterivNV(int var0, int var1, int var2, long var3, long var5);

   public static void glEvalMapsNV(int target, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEvalMapsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEvalMapsNV(target, mode, function_pointer);
   }

   static native void nglEvalMapsNV(int var0, int var1, long var2);
}
