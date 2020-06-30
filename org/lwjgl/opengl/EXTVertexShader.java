package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

public final class EXTVertexShader {
   public static final int GL_VERTEX_SHADER_EXT = 34688;
   public static final int GL_VERTEX_SHADER_BINDING_EXT = 34689;
   public static final int GL_OP_INDEX_EXT = 34690;
   public static final int GL_OP_NEGATE_EXT = 34691;
   public static final int GL_OP_DOT3_EXT = 34692;
   public static final int GL_OP_DOT4_EXT = 34693;
   public static final int GL_OP_MUL_EXT = 34694;
   public static final int GL_OP_ADD_EXT = 34695;
   public static final int GL_OP_MADD_EXT = 34696;
   public static final int GL_OP_FRAC_EXT = 34697;
   public static final int GL_OP_MAX_EXT = 34698;
   public static final int GL_OP_MIN_EXT = 34699;
   public static final int GL_OP_SET_GE_EXT = 34700;
   public static final int GL_OP_SET_LT_EXT = 34701;
   public static final int GL_OP_CLAMP_EXT = 34702;
   public static final int GL_OP_FLOOR_EXT = 34703;
   public static final int GL_OP_ROUND_EXT = 34704;
   public static final int GL_OP_EXP_BASE_2_EXT = 34705;
   public static final int GL_OP_LOG_BASE_2_EXT = 34706;
   public static final int GL_OP_POWER_EXT = 34707;
   public static final int GL_OP_RECIP_EXT = 34708;
   public static final int GL_OP_RECIP_SQRT_EXT = 34709;
   public static final int GL_OP_SUB_EXT = 34710;
   public static final int GL_OP_CROSS_PRODUCT_EXT = 34711;
   public static final int GL_OP_MULTIPLY_MATRIX_EXT = 34712;
   public static final int GL_OP_MOV_EXT = 34713;
   public static final int GL_OUTPUT_VERTEX_EXT = 34714;
   public static final int GL_OUTPUT_COLOR0_EXT = 34715;
   public static final int GL_OUTPUT_COLOR1_EXT = 34716;
   public static final int GL_OUTPUT_TEXTURE_COORD0_EXT = 34717;
   public static final int GL_OUTPUT_TEXTURE_COORD1_EXT = 34718;
   public static final int GL_OUTPUT_TEXTURE_COORD2_EXT = 34719;
   public static final int GL_OUTPUT_TEXTURE_COORD3_EXT = 34720;
   public static final int GL_OUTPUT_TEXTURE_COORD4_EXT = 34721;
   public static final int GL_OUTPUT_TEXTURE_COORD5_EXT = 34722;
   public static final int GL_OUTPUT_TEXTURE_COORD6_EXT = 34723;
   public static final int GL_OUTPUT_TEXTURE_COORD7_EXT = 34724;
   public static final int GL_OUTPUT_TEXTURE_COORD8_EXT = 34725;
   public static final int GL_OUTPUT_TEXTURE_COORD9_EXT = 34726;
   public static final int GL_OUTPUT_TEXTURE_COORD10_EXT = 34727;
   public static final int GL_OUTPUT_TEXTURE_COORD11_EXT = 34728;
   public static final int GL_OUTPUT_TEXTURE_COORD12_EXT = 34729;
   public static final int GL_OUTPUT_TEXTURE_COORD13_EXT = 34730;
   public static final int GL_OUTPUT_TEXTURE_COORD14_EXT = 34731;
   public static final int GL_OUTPUT_TEXTURE_COORD15_EXT = 34732;
   public static final int GL_OUTPUT_TEXTURE_COORD16_EXT = 34733;
   public static final int GL_OUTPUT_TEXTURE_COORD17_EXT = 34734;
   public static final int GL_OUTPUT_TEXTURE_COORD18_EXT = 34735;
   public static final int GL_OUTPUT_TEXTURE_COORD19_EXT = 34736;
   public static final int GL_OUTPUT_TEXTURE_COORD20_EXT = 34737;
   public static final int GL_OUTPUT_TEXTURE_COORD21_EXT = 34738;
   public static final int GL_OUTPUT_TEXTURE_COORD22_EXT = 34739;
   public static final int GL_OUTPUT_TEXTURE_COORD23_EXT = 34740;
   public static final int GL_OUTPUT_TEXTURE_COORD24_EXT = 34741;
   public static final int GL_OUTPUT_TEXTURE_COORD25_EXT = 34742;
   public static final int GL_OUTPUT_TEXTURE_COORD26_EXT = 34743;
   public static final int GL_OUTPUT_TEXTURE_COORD27_EXT = 34744;
   public static final int GL_OUTPUT_TEXTURE_COORD28_EXT = 34745;
   public static final int GL_OUTPUT_TEXTURE_COORD29_EXT = 34746;
   public static final int GL_OUTPUT_TEXTURE_COORD30_EXT = 34747;
   public static final int GL_OUTPUT_TEXTURE_COORD31_EXT = 34748;
   public static final int GL_OUTPUT_FOG_EXT = 34749;
   public static final int GL_SCALAR_EXT = 34750;
   public static final int GL_VECTOR_EXT = 34751;
   public static final int GL_MATRIX_EXT = 34752;
   public static final int GL_VARIANT_EXT = 34753;
   public static final int GL_INVARIANT_EXT = 34754;
   public static final int GL_LOCAL_CONSTANT_EXT = 34755;
   public static final int GL_LOCAL_EXT = 34756;
   public static final int GL_MAX_VERTEX_SHADER_INSTRUCTIONS_EXT = 34757;
   public static final int GL_MAX_VERTEX_SHADER_VARIANTS_EXT = 34758;
   public static final int GL_MAX_VERTEX_SHADER_INVARIANTS_EXT = 34759;
   public static final int GL_MAX_VERTEX_SHADER_LOCAL_CONSTANTS_EXT = 34760;
   public static final int GL_MAX_VERTEX_SHADER_LOCALS_EXT = 34761;
   public static final int GL_MAX_OPTIMIZED_VERTEX_SHADER_INSTRUCTIONS_EXT = 34762;
   public static final int GL_MAX_OPTIMIZED_VERTEX_SHADER_VARIANTS_EXT = 34763;
   public static final int GL_MAX_OPTIMIZED_VERTEX_SHADER_INVARIANTS_EXT = 34764;
   public static final int GL_MAX_OPTIMIZED_VERTEX_SHADER_LOCAL_CONSTANTS_EXT = 34765;
   public static final int GL_MAX_OPTIMIZED_VERTEX_SHADER_LOCALS_EXT = 34766;
   public static final int GL_VERTEX_SHADER_INSTRUCTIONS_EXT = 34767;
   public static final int GL_VERTEX_SHADER_VARIANTS_EXT = 34768;
   public static final int GL_VERTEX_SHADER_INVARIANTS_EXT = 34769;
   public static final int GL_VERTEX_SHADER_LOCAL_CONSTANTS_EXT = 34770;
   public static final int GL_VERTEX_SHADER_LOCALS_EXT = 34771;
   public static final int GL_VERTEX_SHADER_OPTIMIZED_EXT = 34772;
   public static final int GL_X_EXT = 34773;
   public static final int GL_Y_EXT = 34774;
   public static final int GL_Z_EXT = 34775;
   public static final int GL_W_EXT = 34776;
   public static final int GL_NEGATIVE_X_EXT = 34777;
   public static final int GL_NEGATIVE_Y_EXT = 34778;
   public static final int GL_NEGATIVE_Z_EXT = 34779;
   public static final int GL_NEGATIVE_W_EXT = 34780;
   public static final int GL_ZERO_EXT = 34781;
   public static final int GL_ONE_EXT = 34782;
   public static final int GL_NEGATIVE_ONE_EXT = 34783;
   public static final int GL_NORMALIZED_RANGE_EXT = 34784;
   public static final int GL_FULL_RANGE_EXT = 34785;
   public static final int GL_CURRENT_VERTEX_EXT = 34786;
   public static final int GL_MVP_MATRIX_EXT = 34787;
   public static final int GL_VARIANT_VALUE_EXT = 34788;
   public static final int GL_VARIANT_DATATYPE_EXT = 34789;
   public static final int GL_VARIANT_ARRAY_STRIDE_EXT = 34790;
   public static final int GL_VARIANT_ARRAY_TYPE_EXT = 34791;
   public static final int GL_VARIANT_ARRAY_EXT = 34792;
   public static final int GL_VARIANT_ARRAY_POINTER_EXT = 34793;
   public static final int GL_INVARIANT_VALUE_EXT = 34794;
   public static final int GL_INVARIANT_DATATYPE_EXT = 34795;
   public static final int GL_LOCAL_CONSTANT_VALUE_EXT = 34796;
   public static final int GL_LOCAL_CONSTANT_DATATYPE_EXT = 34797;

   public static void glBeginVertexShaderEXT() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginVertexShaderEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginVertexShaderEXT(function_pointer);
   }

   static native void nglBeginVertexShaderEXT(long var0);

   public static void glEndVertexShaderEXT() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndVertexShaderEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndVertexShaderEXT(function_pointer);
   }

   static native void nglEndVertexShaderEXT(long var0);

   public static void glBindVertexShaderEXT(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindVertexShaderEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindVertexShaderEXT(id, function_pointer);
   }

   static native void nglBindVertexShaderEXT(int var0, long var1);

   public static int glGenVertexShadersEXT(int range) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenVertexShadersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGenVertexShadersEXT(range, function_pointer);
      return __result;
   }

   static native int nglGenVertexShadersEXT(int var0, long var1);

   public static void glDeleteVertexShaderEXT(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteVertexShaderEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteVertexShaderEXT(id, function_pointer);
   }

   static native void nglDeleteVertexShaderEXT(int var0, long var1);

   public static void glShaderOp1EXT(int op, int res, int arg1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderOp1EXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShaderOp1EXT(op, res, arg1, function_pointer);
   }

   static native void nglShaderOp1EXT(int var0, int var1, int var2, long var3);

   public static void glShaderOp2EXT(int op, int res, int arg1, int arg2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderOp2EXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShaderOp2EXT(op, res, arg1, arg2, function_pointer);
   }

   static native void nglShaderOp2EXT(int var0, int var1, int var2, int var3, long var4);

   public static void glShaderOp3EXT(int op, int res, int arg1, int arg2, int arg3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderOp3EXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShaderOp3EXT(op, res, arg1, arg2, arg3, function_pointer);
   }

   static native void nglShaderOp3EXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glSwizzleEXT(int res, int in, int outX, int outY, int outZ, int outW) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSwizzleEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSwizzleEXT(res, in, outX, outY, outZ, outW, function_pointer);
   }

   static native void nglSwizzleEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glWriteMaskEXT(int res, int in, int outX, int outY, int outZ, int outW) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWriteMaskEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWriteMaskEXT(res, in, outX, outY, outZ, outW, function_pointer);
   }

   static native void nglWriteMaskEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glInsertComponentEXT(int res, int src, int num) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInsertComponentEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInsertComponentEXT(res, src, num, function_pointer);
   }

   static native void nglInsertComponentEXT(int var0, int var1, int var2, long var3);

   public static void glExtractComponentEXT(int res, int src, int num) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glExtractComponentEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglExtractComponentEXT(res, src, num, function_pointer);
   }

   static native void nglExtractComponentEXT(int var0, int var1, int var2, long var3);

   public static int glGenSymbolsEXT(int dataType, int storageType, int range, int components) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenSymbolsEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGenSymbolsEXT(dataType, storageType, range, components, function_pointer);
      return __result;
   }

   static native int nglGenSymbolsEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glSetInvariantEXT(int id, DoubleBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetInvariantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)pAddr, 4);
      nglSetInvariantEXT(id, 5130, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetInvariantEXT(int id, FloatBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetInvariantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pAddr, 4);
      nglSetInvariantEXT(id, 5126, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetInvariantEXT(int id, boolean unsigned, ByteBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetInvariantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pAddr, 4);
      nglSetInvariantEXT(id, unsigned?5121:5120, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetInvariantEXT(int id, boolean unsigned, IntBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetInvariantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pAddr, 4);
      nglSetInvariantEXT(id, unsigned?5125:5124, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetInvariantEXT(int id, boolean unsigned, ShortBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetInvariantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ShortBuffer)pAddr, 4);
      nglSetInvariantEXT(id, unsigned?5123:5122, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglSetInvariantEXT(int var0, int var1, long var2, long var4);

   public static void glSetLocalConstantEXT(int id, DoubleBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetLocalConstantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)pAddr, 4);
      nglSetLocalConstantEXT(id, 5130, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetLocalConstantEXT(int id, FloatBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetLocalConstantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pAddr, 4);
      nglSetLocalConstantEXT(id, 5126, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetLocalConstantEXT(int id, boolean unsigned, ByteBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetLocalConstantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pAddr, 4);
      nglSetLocalConstantEXT(id, unsigned?5121:5120, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetLocalConstantEXT(int id, boolean unsigned, IntBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetLocalConstantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pAddr, 4);
      nglSetLocalConstantEXT(id, unsigned?5125:5124, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glSetLocalConstantEXT(int id, boolean unsigned, ShortBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSetLocalConstantEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ShortBuffer)pAddr, 4);
      nglSetLocalConstantEXT(id, unsigned?5123:5122, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglSetLocalConstantEXT(int var0, int var1, long var2, long var4);

   public static void glVariantEXT(int id, ByteBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantbvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pAddr, 4);
      nglVariantbvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantbvEXT(int var0, long var1, long var3);

   public static void glVariantEXT(int id, ShortBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantsvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ShortBuffer)pAddr, 4);
      nglVariantsvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantsvEXT(int var0, long var1, long var3);

   public static void glVariantEXT(int id, IntBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pAddr, 4);
      nglVariantivEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantivEXT(int var0, long var1, long var3);

   public static void glVariantEXT(int id, FloatBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pAddr, 4);
      nglVariantfvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantfvEXT(int var0, long var1, long var3);

   public static void glVariantEXT(int id, DoubleBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantdvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)pAddr, 4);
      nglVariantdvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantdvEXT(int var0, long var1, long var3);

   public static void glVariantuEXT(int id, ByteBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantubvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pAddr, 4);
      nglVariantubvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantubvEXT(int var0, long var1, long var3);

   public static void glVariantuEXT(int id, ShortBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantusvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ShortBuffer)pAddr, 4);
      nglVariantusvEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantusvEXT(int var0, long var1, long var3);

   public static void glVariantuEXT(int id, IntBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pAddr, 4);
      nglVariantuivEXT(id, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantuivEXT(int var0, long var1, long var3);

   public static void glVariantPointerEXT(int id, int stride, DoubleBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pAddr);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_shader_glVariantPointerEXT_pAddr = pAddr;
      }

      nglVariantPointerEXT(id, 5130, stride, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glVariantPointerEXT(int id, int stride, FloatBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pAddr);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_shader_glVariantPointerEXT_pAddr = pAddr;
      }

      nglVariantPointerEXT(id, 5126, stride, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glVariantPointerEXT(int id, boolean unsigned, int stride, ByteBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pAddr);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_shader_glVariantPointerEXT_pAddr = pAddr;
      }

      nglVariantPointerEXT(id, unsigned?5121:5120, stride, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glVariantPointerEXT(int id, boolean unsigned, int stride, IntBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pAddr);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_shader_glVariantPointerEXT_pAddr = pAddr;
      }

      nglVariantPointerEXT(id, unsigned?5125:5124, stride, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   public static void glVariantPointerEXT(int id, boolean unsigned, int stride, ShortBuffer pAddr) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pAddr);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_vertex_shader_glVariantPointerEXT_pAddr = pAddr;
      }

      nglVariantPointerEXT(id, unsigned?5123:5122, stride, MemoryUtil.getAddress(pAddr), function_pointer);
   }

   static native void nglVariantPointerEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glVariantPointerEXT(int id, int type, int stride, long pAddr_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVariantPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglVariantPointerEXTBO(id, type, stride, pAddr_buffer_offset, function_pointer);
   }

   static native void nglVariantPointerEXTBO(int var0, int var1, int var2, long var3, long var5);

   public static void glEnableVariantClientStateEXT(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableVariantClientStateEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableVariantClientStateEXT(id, function_pointer);
   }

   static native void nglEnableVariantClientStateEXT(int var0, long var1);

   public static void glDisableVariantClientStateEXT(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableVariantClientStateEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableVariantClientStateEXT(id, function_pointer);
   }

   static native void nglDisableVariantClientStateEXT(int var0, long var1);

   public static int glBindLightParameterEXT(int light, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindLightParameterEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglBindLightParameterEXT(light, value, function_pointer);
      return __result;
   }

   static native int nglBindLightParameterEXT(int var0, int var1, long var2);

   public static int glBindMaterialParameterEXT(int face, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindMaterialParameterEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglBindMaterialParameterEXT(face, value, function_pointer);
      return __result;
   }

   static native int nglBindMaterialParameterEXT(int var0, int var1, long var2);

   public static int glBindTexGenParameterEXT(int unit, int coord, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindTexGenParameterEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglBindTexGenParameterEXT(unit, coord, value, function_pointer);
      return __result;
   }

   static native int nglBindTexGenParameterEXT(int var0, int var1, int var2, long var3);

   public static int glBindTextureUnitParameterEXT(int unit, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindTextureUnitParameterEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglBindTextureUnitParameterEXT(unit, value, function_pointer);
      return __result;
   }

   static native int nglBindTextureUnitParameterEXT(int var0, int var1, long var2);

   public static int glBindParameterEXT(int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindParameterEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglBindParameterEXT(value, function_pointer);
      return __result;
   }

   static native int nglBindParameterEXT(int var0, long var1);

   public static boolean glIsVariantEnabledEXT(int id, int cap) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsVariantEnabledEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsVariantEnabledEXT(id, cap, function_pointer);
      return __result;
   }

   static native boolean nglIsVariantEnabledEXT(int var0, int var1, long var2);

   public static void glGetVariantBooleanEXT(int id, int value, ByteBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVariantBooleanvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pbData, 4);
      nglGetVariantBooleanvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetVariantBooleanvEXT(int var0, int var1, long var2, long var4);

   public static void glGetVariantIntegerEXT(int id, int value, IntBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVariantIntegervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pbData, 4);
      nglGetVariantIntegervEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetVariantIntegervEXT(int var0, int var1, long var2, long var4);

   public static void glGetVariantFloatEXT(int id, int value, FloatBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVariantFloatvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pbData, 4);
      nglGetVariantFloatvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetVariantFloatvEXT(int var0, int var1, long var2, long var4);

   public static ByteBuffer glGetVariantPointerEXT(int id, int value, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVariantPointervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetVariantPointervEXT(id, value, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetVariantPointervEXT(int var0, int var1, long var2, long var4);

   public static void glGetInvariantBooleanEXT(int id, int value, ByteBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInvariantBooleanvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pbData, 4);
      nglGetInvariantBooleanvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetInvariantBooleanvEXT(int var0, int var1, long var2, long var4);

   public static void glGetInvariantIntegerEXT(int id, int value, IntBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInvariantIntegervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pbData, 4);
      nglGetInvariantIntegervEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetInvariantIntegervEXT(int var0, int var1, long var2, long var4);

   public static void glGetInvariantFloatEXT(int id, int value, FloatBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInvariantFloatvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pbData, 4);
      nglGetInvariantFloatvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetInvariantFloatvEXT(int var0, int var1, long var2, long var4);

   public static void glGetLocalConstantBooleanEXT(int id, int value, ByteBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetLocalConstantBooleanvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)pbData, 4);
      nglGetLocalConstantBooleanvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetLocalConstantBooleanvEXT(int var0, int var1, long var2, long var4);

   public static void glGetLocalConstantIntegerEXT(int id, int value, IntBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetLocalConstantIntegervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)pbData, 4);
      nglGetLocalConstantIntegervEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetLocalConstantIntegervEXT(int var0, int var1, long var2, long var4);

   public static void glGetLocalConstantFloatEXT(int id, int value, FloatBuffer pbData) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetLocalConstantFloatvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)pbData, 4);
      nglGetLocalConstantFloatvEXT(id, value, MemoryUtil.getAddress(pbData), function_pointer);
   }

   static native void nglGetLocalConstantFloatvEXT(int var0, int var1, long var2, long var4);
}
