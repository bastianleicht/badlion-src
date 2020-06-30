package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class GL13 {
   public static final int GL_TEXTURE0 = 33984;
   public static final int GL_TEXTURE1 = 33985;
   public static final int GL_TEXTURE2 = 33986;
   public static final int GL_TEXTURE3 = 33987;
   public static final int GL_TEXTURE4 = 33988;
   public static final int GL_TEXTURE5 = 33989;
   public static final int GL_TEXTURE6 = 33990;
   public static final int GL_TEXTURE7 = 33991;
   public static final int GL_TEXTURE8 = 33992;
   public static final int GL_TEXTURE9 = 33993;
   public static final int GL_TEXTURE10 = 33994;
   public static final int GL_TEXTURE11 = 33995;
   public static final int GL_TEXTURE12 = 33996;
   public static final int GL_TEXTURE13 = 33997;
   public static final int GL_TEXTURE14 = 33998;
   public static final int GL_TEXTURE15 = 33999;
   public static final int GL_TEXTURE16 = 34000;
   public static final int GL_TEXTURE17 = 34001;
   public static final int GL_TEXTURE18 = 34002;
   public static final int GL_TEXTURE19 = 34003;
   public static final int GL_TEXTURE20 = 34004;
   public static final int GL_TEXTURE21 = 34005;
   public static final int GL_TEXTURE22 = 34006;
   public static final int GL_TEXTURE23 = 34007;
   public static final int GL_TEXTURE24 = 34008;
   public static final int GL_TEXTURE25 = 34009;
   public static final int GL_TEXTURE26 = 34010;
   public static final int GL_TEXTURE27 = 34011;
   public static final int GL_TEXTURE28 = 34012;
   public static final int GL_TEXTURE29 = 34013;
   public static final int GL_TEXTURE30 = 34014;
   public static final int GL_TEXTURE31 = 34015;
   public static final int GL_ACTIVE_TEXTURE = 34016;
   public static final int GL_CLIENT_ACTIVE_TEXTURE = 34017;
   public static final int GL_MAX_TEXTURE_UNITS = 34018;
   public static final int GL_NORMAL_MAP = 34065;
   public static final int GL_REFLECTION_MAP = 34066;
   public static final int GL_TEXTURE_CUBE_MAP = 34067;
   public static final int GL_TEXTURE_BINDING_CUBE_MAP = 34068;
   public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
   public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
   public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
   public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
   public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
   public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
   public static final int GL_PROXY_TEXTURE_CUBE_MAP = 34075;
   public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
   public static final int GL_COMPRESSED_ALPHA = 34025;
   public static final int GL_COMPRESSED_LUMINANCE = 34026;
   public static final int GL_COMPRESSED_LUMINANCE_ALPHA = 34027;
   public static final int GL_COMPRESSED_INTENSITY = 34028;
   public static final int GL_COMPRESSED_RGB = 34029;
   public static final int GL_COMPRESSED_RGBA = 34030;
   public static final int GL_TEXTURE_COMPRESSION_HINT = 34031;
   public static final int GL_TEXTURE_COMPRESSED_IMAGE_SIZE = 34464;
   public static final int GL_TEXTURE_COMPRESSED = 34465;
   public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
   public static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467;
   public static final int GL_MULTISAMPLE = 32925;
   public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
   public static final int GL_SAMPLE_ALPHA_TO_ONE = 32927;
   public static final int GL_SAMPLE_COVERAGE = 32928;
   public static final int GL_SAMPLE_BUFFERS = 32936;
   public static final int GL_SAMPLES = 32937;
   public static final int GL_SAMPLE_COVERAGE_VALUE = 32938;
   public static final int GL_SAMPLE_COVERAGE_INVERT = 32939;
   public static final int GL_MULTISAMPLE_BIT = 536870912;
   public static final int GL_TRANSPOSE_MODELVIEW_MATRIX = 34019;
   public static final int GL_TRANSPOSE_PROJECTION_MATRIX = 34020;
   public static final int GL_TRANSPOSE_TEXTURE_MATRIX = 34021;
   public static final int GL_TRANSPOSE_COLOR_MATRIX = 34022;
   public static final int GL_COMBINE = 34160;
   public static final int GL_COMBINE_RGB = 34161;
   public static final int GL_COMBINE_ALPHA = 34162;
   public static final int GL_SOURCE0_RGB = 34176;
   public static final int GL_SOURCE1_RGB = 34177;
   public static final int GL_SOURCE2_RGB = 34178;
   public static final int GL_SOURCE0_ALPHA = 34184;
   public static final int GL_SOURCE1_ALPHA = 34185;
   public static final int GL_SOURCE2_ALPHA = 34186;
   public static final int GL_OPERAND0_RGB = 34192;
   public static final int GL_OPERAND1_RGB = 34193;
   public static final int GL_OPERAND2_RGB = 34194;
   public static final int GL_OPERAND0_ALPHA = 34200;
   public static final int GL_OPERAND1_ALPHA = 34201;
   public static final int GL_OPERAND2_ALPHA = 34202;
   public static final int GL_RGB_SCALE = 34163;
   public static final int GL_ADD_SIGNED = 34164;
   public static final int GL_INTERPOLATE = 34165;
   public static final int GL_SUBTRACT = 34023;
   public static final int GL_CONSTANT = 34166;
   public static final int GL_PRIMARY_COLOR = 34167;
   public static final int GL_PREVIOUS = 34168;
   public static final int GL_DOT3_RGB = 34478;
   public static final int GL_DOT3_RGBA = 34479;
   public static final int GL_CLAMP_TO_BORDER = 33069;

   public static void glActiveTexture(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glActiveTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglActiveTexture(texture, function_pointer);
   }

   static native void nglActiveTexture(int var0, long var1);

   public static void glClientActiveTexture(int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClientActiveTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      StateTracker.getReferences(caps).glClientActiveTexture = texture - '蓀';
      nglClientActiveTexture(texture, function_pointer);
   }

   static native void nglClientActiveTexture(int var0, long var1);

   public static void glCompressedTexImage1D(int target, int level, int internalformat, int width, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexImage1D(target, level, internalformat, width, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexImage1D(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glCompressedTexImage1D(int target, int level, int internalformat, int width, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexImage1DBO(target, level, internalformat, width, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexImage1DBO(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glCompressedTexImage1D(int target, int level, int internalformat, int width, int border, int imageSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      nglCompressedTexImage1D(target, level, internalformat, width, border, imageSize, 0L, function_pointer);
   }

   public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexImage2D(target, level, internalformat, width, height, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexImage2DBO(target, level, internalformat, width, height, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexImage2DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      nglCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, 0L, function_pointer);
   }

   public static void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexImage3D(target, level, internalformat, width, height, depth, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexImage3D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexImage3DBO(target, level, internalformat, width, height, depth, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexImage3DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      nglCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, 0L, function_pointer);
   }

   public static void glCompressedTexSubImage1D(int target, int level, int xoffset, int width, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexSubImage1D(target, level, xoffset, width, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexSubImage1D(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glCompressedTexSubImage1D(int target, int level, int xoffset, int width, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage1D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexSubImage1DBO(target, level, xoffset, width, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexSubImage1DBO(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexSubImage2DBO(target, level, xoffset, yoffset, width, height, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexSubImage2DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTexSubImage3D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTexSubImage3DBO(target, level, xoffset, yoffset, zoffset, width, height, depth, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTexSubImage3DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glGetCompressedTexImage(int target, int lod, ByteBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTexImage(target, lod, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedTexImage(int target, int lod, IntBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTexImage(target, lod, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedTexImage(int target, int lod, ShortBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTexImage(target, lod, MemoryUtil.getAddress(img), function_pointer);
   }

   static native void nglGetCompressedTexImage(int var0, int var1, long var2, long var4);

   public static void glGetCompressedTexImage(int target, int lod, long img_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetCompressedTexImageBO(target, lod, img_buffer_offset, function_pointer);
   }

   static native void nglGetCompressedTexImageBO(int var0, int var1, long var2, long var4);

   public static void glMultiTexCoord1f(int target, float s) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord1f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord1f(target, s, function_pointer);
   }

   static native void nglMultiTexCoord1f(int var0, float var1, long var2);

   public static void glMultiTexCoord1d(int target, double s) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord1d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord1d(target, s, function_pointer);
   }

   static native void nglMultiTexCoord1d(int var0, double var1, long var3);

   public static void glMultiTexCoord2f(int target, float s, float t) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord2f(target, s, t, function_pointer);
   }

   static native void nglMultiTexCoord2f(int var0, float var1, float var2, long var3);

   public static void glMultiTexCoord2d(int target, double s, double t) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord2d(target, s, t, function_pointer);
   }

   static native void nglMultiTexCoord2d(int var0, double var1, double var3, long var5);

   public static void glMultiTexCoord3f(int target, float s, float t, float r) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord3f(target, s, t, r, function_pointer);
   }

   static native void nglMultiTexCoord3f(int var0, float var1, float var2, float var3, long var4);

   public static void glMultiTexCoord3d(int target, double s, double t, double r) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord3d(target, s, t, r, function_pointer);
   }

   static native void nglMultiTexCoord3d(int var0, double var1, double var3, double var5, long var7);

   public static void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord4f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord4f(target, s, t, r, q, function_pointer);
   }

   static native void nglMultiTexCoord4f(int var0, float var1, float var2, float var3, float var4, long var5);

   public static void glMultiTexCoord4d(int target, double s, double t, double r, double q) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoord4d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexCoord4d(target, s, t, r, q, function_pointer);
   }

   static native void nglMultiTexCoord4d(int var0, double var1, double var3, double var5, double var7, long var9);

   public static void glLoadTransposeMatrix(FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadTransposeMatrixf;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglLoadTransposeMatrixf(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglLoadTransposeMatrixf(long var0, long var2);

   public static void glLoadTransposeMatrix(DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLoadTransposeMatrixd;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglLoadTransposeMatrixd(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglLoadTransposeMatrixd(long var0, long var2);

   public static void glMultTransposeMatrix(FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultTransposeMatrixf;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMultTransposeMatrixf(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMultTransposeMatrixf(long var0, long var2);

   public static void glMultTransposeMatrix(DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultTransposeMatrixd;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMultTransposeMatrixd(MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMultTransposeMatrixd(long var0, long var2);

   public static void glSampleCoverage(float value, boolean invert) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSampleCoverage;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSampleCoverage(value, invert, function_pointer);
   }

   static native void nglSampleCoverage(float var0, boolean var1, long var2);
}
