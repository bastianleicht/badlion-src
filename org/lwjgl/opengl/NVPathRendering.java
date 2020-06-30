package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class NVPathRendering {
   public static final int GL_CLOSE_PATH_NV = 0;
   public static final int GL_MOVE_TO_NV = 2;
   public static final int GL_RELATIVE_MOVE_TO_NV = 3;
   public static final int GL_LINE_TO_NV = 4;
   public static final int GL_RELATIVE_LINE_TO_NV = 5;
   public static final int GL_HORIZONTAL_LINE_TO_NV = 6;
   public static final int GL_RELATIVE_HORIZONTAL_LINE_TO_NV = 7;
   public static final int GL_VERTICAL_LINE_TO_NV = 8;
   public static final int GL_RELATIVE_VERTICAL_LINE_TO_NV = 9;
   public static final int GL_QUADRATIC_CURVE_TO_NV = 10;
   public static final int GL_RELATIVE_QUADRATIC_CURVE_TO_NV = 11;
   public static final int GL_CUBIC_CURVE_TO_NV = 12;
   public static final int GL_RELATIVE_CUBIC_CURVE_TO_NV = 13;
   public static final int GL_SMOOTH_QUADRATIC_CURVE_TO_NV = 14;
   public static final int GL_RELATIVE_SMOOTH_QUADRATIC_CURVE_TO_NV = 15;
   public static final int GL_SMOOTH_CUBIC_CURVE_TO_NV = 16;
   public static final int GL_RELATIVE_SMOOTH_CUBIC_CURVE_TO_NV = 17;
   public static final int GL_SMALL_CCW_ARC_TO_NV = 18;
   public static final int GL_RELATIVE_SMALL_CCW_ARC_TO_NV = 19;
   public static final int GL_SMALL_CW_ARC_TO_NV = 20;
   public static final int GL_RELATIVE_SMALL_CW_ARC_TO_NV = 21;
   public static final int GL_LARGE_CCW_ARC_TO_NV = 22;
   public static final int GL_RELATIVE_LARGE_CCW_ARC_TO_NV = 23;
   public static final int GL_LARGE_CW_ARC_TO_NV = 24;
   public static final int GL_RELATIVE_LARGE_CW_ARC_TO_NV = 25;
   public static final int GL_CIRCULAR_CCW_ARC_TO_NV = 248;
   public static final int GL_CIRCULAR_CW_ARC_TO_NV = 250;
   public static final int GL_CIRCULAR_TANGENT_ARC_TO_NV = 252;
   public static final int GL_ARC_TO_NV = 254;
   public static final int GL_RELATIVE_ARC_TO_NV = 255;
   public static final int GL_PATH_FORMAT_SVG_NV = 36976;
   public static final int GL_PATH_FORMAT_PS_NV = 36977;
   public static final int GL_STANDARD_FONT_NAME_NV = 36978;
   public static final int GL_SYSTEM_FONT_NAME_NV = 36979;
   public static final int GL_FILE_NAME_NV = 36980;
   public static final int GL_SKIP_MISSING_GLYPH_NV = 37033;
   public static final int GL_USE_MISSING_GLYPH_NV = 37034;
   public static final int GL_PATH_STROKE_WIDTH_NV = 36981;
   public static final int GL_PATH_INITIAL_END_CAP_NV = 36983;
   public static final int GL_PATH_TERMINAL_END_CAP_NV = 36984;
   public static final int GL_PATH_JOIN_STYLE_NV = 36985;
   public static final int GL_PATH_MITER_LIMIT_NV = 36986;
   public static final int GL_PATH_INITIAL_DASH_CAP_NV = 36988;
   public static final int GL_PATH_TERMINAL_DASH_CAP_NV = 36989;
   public static final int GL_PATH_DASH_OFFSET_NV = 36990;
   public static final int GL_PATH_CLIENT_LENGTH_NV = 36991;
   public static final int GL_PATH_DASH_OFFSET_RESET_NV = 37044;
   public static final int GL_PATH_FILL_MODE_NV = 36992;
   public static final int GL_PATH_FILL_MASK_NV = 36993;
   public static final int GL_PATH_FILL_COVER_MODE_NV = 36994;
   public static final int GL_PATH_STROKE_COVER_MODE_NV = 36995;
   public static final int GL_PATH_STROKE_MASK_NV = 36996;
   public static final int GL_PATH_END_CAPS_NV = 36982;
   public static final int GL_PATH_DASH_CAPS_NV = 36987;
   public static final int GL_COUNT_UP_NV = 37000;
   public static final int GL_COUNT_DOWN_NV = 37001;
   public static final int GL_PRIMARY_COLOR = 34167;
   public static final int GL_PRIMARY_COLOR_NV = 34092;
   public static final int GL_SECONDARY_COLOR_NV = 34093;
   public static final int GL_PATH_OBJECT_BOUNDING_BOX_NV = 37002;
   public static final int GL_CONVEX_HULL_NV = 37003;
   public static final int GL_BOUNDING_BOX_NV = 37005;
   public static final int GL_TRANSLATE_X_NV = 37006;
   public static final int GL_TRANSLATE_Y_NV = 37007;
   public static final int GL_TRANSLATE_2D_NV = 37008;
   public static final int GL_TRANSLATE_3D_NV = 37009;
   public static final int GL_AFFINE_2D_NV = 37010;
   public static final int GL_AFFINE_3D_NV = 37012;
   public static final int GL_TRANSPOSE_AFFINE_2D_NV = 37014;
   public static final int GL_TRANSPOSE_AFFINE_3D_NV = 37016;
   public static final int GL_UTF8_NV = 37018;
   public static final int GL_UTF16_NV = 37019;
   public static final int GL_BOUNDING_BOX_OF_BOUNDING_BOXES_NV = 37020;
   public static final int GL_PATH_COMMAND_COUNT_NV = 37021;
   public static final int GL_PATH_COORD_COUNT_NV = 37022;
   public static final int GL_PATH_DASH_ARRAY_COUNT_NV = 37023;
   public static final int GL_PATH_COMPUTED_LENGTH_NV = 37024;
   public static final int GL_PATH_FILL_BOUNDING_BOX_NV = 37025;
   public static final int GL_PATH_STROKE_BOUNDING_BOX_NV = 37026;
   public static final int GL_SQUARE_NV = 37027;
   public static final int GL_ROUND_NV = 37028;
   public static final int GL_TRIANGULAR_NV = 37029;
   public static final int GL_BEVEL_NV = 37030;
   public static final int GL_MITER_REVERT_NV = 37031;
   public static final int GL_MITER_TRUNCATE_NV = 37032;
   public static final int GL_MOVE_TO_RESETS_NV = 37045;
   public static final int GL_MOVE_TO_CONTINUES_NV = 37046;
   public static final int GL_BOLD_BIT_NV = 1;
   public static final int GL_ITALIC_BIT_NV = 2;
   public static final int GL_PATH_ERROR_POSITION_NV = 37035;
   public static final int GL_PATH_FOG_GEN_MODE_NV = 37036;
   public static final int GL_PATH_STENCIL_FUNC_NV = 37047;
   public static final int GL_PATH_STENCIL_REF_NV = 37048;
   public static final int GL_PATH_STENCIL_VALUE_MASK_NV = 37049;
   public static final int GL_PATH_STENCIL_DEPTH_OFFSET_FACTOR_NV = 37053;
   public static final int GL_PATH_STENCIL_DEPTH_OFFSET_UNITS_NV = 37054;
   public static final int GL_PATH_COVER_DEPTH_FUNC_NV = 37055;
   public static final int GL_GLYPH_WIDTH_BIT_NV = 1;
   public static final int GL_GLYPH_HEIGHT_BIT_NV = 2;
   public static final int GL_GLYPH_HORIZONTAL_BEARING_X_BIT_NV = 4;
   public static final int GL_GLYPH_HORIZONTAL_BEARING_Y_BIT_NV = 8;
   public static final int GL_GLYPH_HORIZONTAL_BEARING_ADVANCE_BIT_NV = 16;
   public static final int GL_GLYPH_VERTICAL_BEARING_X_BIT_NV = 32;
   public static final int GL_GLYPH_VERTICAL_BEARING_Y_BIT_NV = 64;
   public static final int GL_GLYPH_VERTICAL_BEARING_ADVANCE_BIT_NV = 128;
   public static final int GL_GLYPH_HAS_KERNING_NV = 256;
   public static final int GL_FONT_X_MIN_BOUNDS_NV = 65536;
   public static final int GL_FONT_Y_MIN_BOUNDS_NV = 131072;
   public static final int GL_FONT_X_MAX_BOUNDS_NV = 262144;
   public static final int GL_FONT_Y_MAX_BOUNDS_NV = 524288;
   public static final int GL_FONT_UNITS_PER_EM_NV = 1048576;
   public static final int GL_FONT_ASCENDER_NV = 2097152;
   public static final int GL_FONT_DESCENDER_NV = 4194304;
   public static final int GL_FONT_HEIGHT_NV = 8388608;
   public static final int GL_FONT_MAX_ADVANCE_WIDTH_NV = 16777216;
   public static final int GL_FONT_MAX_ADVANCE_HEIGHT_NV = 33554432;
   public static final int GL_FONT_UNDERLINE_POSITION_NV = 67108864;
   public static final int GL_FONT_UNDERLINE_THICKNESS_NV = 134217728;
   public static final int GL_FONT_HAS_KERNING_NV = 268435456;
   public static final int GL_ACCUM_ADJACENT_PAIRS_NV = 37037;
   public static final int GL_ADJACENT_PAIRS_NV = 37038;
   public static final int GL_FIRST_TO_REST_NV = 37039;
   public static final int GL_PATH_GEN_MODE_NV = 37040;
   public static final int GL_PATH_GEN_COEFF_NV = 37041;
   public static final int GL_PATH_GEN_COLOR_FORMAT_NV = 37042;
   public static final int GL_PATH_GEN_COMPONENTS_NV = 37043;

   public static void glPathCommandsNV(int path, ByteBuffer commands, int coordType, ByteBuffer coords) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathCommandsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(commands);
      BufferChecks.checkDirect(coords);
      nglPathCommandsNV(path, commands.remaining(), MemoryUtil.getAddress(commands), coords.remaining(), coordType, MemoryUtil.getAddress(coords), function_pointer);
   }

   static native void nglPathCommandsNV(int var0, int var1, long var2, int var4, int var5, long var6, long var8);

   public static void glPathCoordsNV(int path, int coordType, ByteBuffer coords) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathCoordsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(coords);
      nglPathCoordsNV(path, coords.remaining(), coordType, MemoryUtil.getAddress(coords), function_pointer);
   }

   static native void nglPathCoordsNV(int var0, int var1, int var2, long var3, long var5);

   public static void glPathSubCommandsNV(int path, int commandStart, int commandsToDelete, ByteBuffer commands, int coordType, ByteBuffer coords) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathSubCommandsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(commands);
      BufferChecks.checkDirect(coords);
      nglPathSubCommandsNV(path, commandStart, commandsToDelete, commands.remaining(), MemoryUtil.getAddress(commands), coords.remaining(), coordType, MemoryUtil.getAddress(coords), function_pointer);
   }

   static native void nglPathSubCommandsNV(int var0, int var1, int var2, int var3, long var4, int var6, int var7, long var8, long var10);

   public static void glPathSubCoordsNV(int path, int coordStart, int coordType, ByteBuffer coords) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathSubCoordsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(coords);
      nglPathSubCoordsNV(path, coordStart, coords.remaining(), coordType, MemoryUtil.getAddress(coords), function_pointer);
   }

   static native void nglPathSubCoordsNV(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glPathStringNV(int path, int format, ByteBuffer pathString) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathStringNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pathString);
      nglPathStringNV(path, format, pathString.remaining(), MemoryUtil.getAddress(pathString), function_pointer);
   }

   static native void nglPathStringNV(int var0, int var1, int var2, long var3, long var5);

   public static void glPathGlyphsNV(int firstPathName, int fontTarget, ByteBuffer fontName, int fontStyle, int type, ByteBuffer charcodes, int handleMissingGlyphs, int pathParameterTemplate, float emScale) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathGlyphsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(fontName);
      BufferChecks.checkNullTerminated(fontName);
      BufferChecks.checkDirect(charcodes);
      nglPathGlyphsNV(firstPathName, fontTarget, MemoryUtil.getAddress(fontName), fontStyle, charcodes.remaining() / GLChecks.calculateBytesPerCharCode(type), type, MemoryUtil.getAddress(charcodes), handleMissingGlyphs, pathParameterTemplate, emScale, function_pointer);
   }

   static native void nglPathGlyphsNV(int var0, int var1, long var2, int var4, int var5, int var6, long var7, int var9, int var10, float var11, long var12);

   public static void glPathGlyphRangeNV(int firstPathName, int fontTarget, ByteBuffer fontName, int fontStyle, int firstGlyph, int numGlyphs, int handleMissingGlyphs, int pathParameterTemplate, float emScale) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathGlyphRangeNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(fontName);
      BufferChecks.checkNullTerminated(fontName);
      nglPathGlyphRangeNV(firstPathName, fontTarget, MemoryUtil.getAddress(fontName), fontStyle, firstGlyph, numGlyphs, handleMissingGlyphs, pathParameterTemplate, emScale, function_pointer);
   }

   static native void nglPathGlyphRangeNV(int var0, int var1, long var2, int var4, int var5, int var6, int var7, int var8, float var9, long var10);

   public static void glWeightPathsNV(int resultPath, IntBuffer paths, FloatBuffer weights) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWeightPathsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      BufferChecks.checkBuffer(weights, paths.remaining());
      nglWeightPathsNV(resultPath, paths.remaining(), MemoryUtil.getAddress(paths), MemoryUtil.getAddress(weights), function_pointer);
   }

   static native void nglWeightPathsNV(int var0, int var1, long var2, long var4, long var6);

   public static void glCopyPathNV(int resultPath, int srcPath) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyPathNV(resultPath, srcPath, function_pointer);
   }

   static native void nglCopyPathNV(int var0, int var1, long var2);

   public static void glInterpolatePathsNV(int resultPath, int pathA, int pathB, float weight) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glInterpolatePathsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglInterpolatePathsNV(resultPath, pathA, pathB, weight, function_pointer);
   }

   static native void nglInterpolatePathsNV(int var0, int var1, int var2, float var3, long var4);

   public static void glTransformPathNV(int resultPath, int srcPath, int transformType, FloatBuffer transformValues) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTransformPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(transformValues != null) {
         BufferChecks.checkBuffer(transformValues, GLChecks.calculateTransformPathValues(transformType));
      }

      nglTransformPathNV(resultPath, srcPath, transformType, MemoryUtil.getAddressSafe(transformValues), function_pointer);
   }

   static native void nglTransformPathNV(int var0, int var1, int var2, long var3, long var5);

   public static void glPathParameterNV(int path, int pname, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)value, 4);
      nglPathParameterivNV(path, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglPathParameterivNV(int var0, int var1, long var2, long var4);

   public static void glPathParameteriNV(int path, int pname, int value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathParameteriNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathParameteriNV(path, pname, value, function_pointer);
   }

   static native void nglPathParameteriNV(int var0, int var1, int var2, long var3);

   public static void glPathParameterNV(int path, int pname, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)value, 4);
      nglPathParameterfvNV(path, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglPathParameterfvNV(int var0, int var1, long var2, long var4);

   public static void glPathParameterfNV(int path, int pname, float value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathParameterfNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathParameterfNV(path, pname, value, function_pointer);
   }

   static native void nglPathParameterfNV(int var0, int var1, float var2, long var3);

   public static void glPathDashArrayNV(int path, FloatBuffer dashArray) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathDashArrayNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(dashArray);
      nglPathDashArrayNV(path, dashArray.remaining(), MemoryUtil.getAddress(dashArray), function_pointer);
   }

   static native void nglPathDashArrayNV(int var0, int var1, long var2, long var4);

   public static int glGenPathsNV(int range) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenPathsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGenPathsNV(range, function_pointer);
      return __result;
   }

   static native int nglGenPathsNV(int var0, long var1);

   public static void glDeletePathsNV(int path, int range) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeletePathsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeletePathsNV(path, range, function_pointer);
   }

   static native void nglDeletePathsNV(int var0, int var1, long var2);

   public static boolean glIsPathNV(int path) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsPathNV(path, function_pointer);
      return __result;
   }

   static native boolean nglIsPathNV(int var0, long var1);

   public static void glPathStencilFuncNV(int func, int ref, int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathStencilFuncNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathStencilFuncNV(func, ref, mask, function_pointer);
   }

   static native void nglPathStencilFuncNV(int var0, int var1, int var2, long var3);

   public static void glPathStencilDepthOffsetNV(float factor, int units) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathStencilDepthOffsetNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathStencilDepthOffsetNV(factor, units, function_pointer);
   }

   static native void nglPathStencilDepthOffsetNV(float var0, int var1, long var2);

   public static void glStencilFillPathNV(int path, int fillMode, int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilFillPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilFillPathNV(path, fillMode, mask, function_pointer);
   }

   static native void nglStencilFillPathNV(int var0, int var1, int var2, long var3);

   public static void glStencilStrokePathNV(int path, int reference, int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilStrokePathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglStencilStrokePathNV(path, reference, mask, function_pointer);
   }

   static native void nglStencilStrokePathNV(int var0, int var1, int var2, long var3);

   public static void glStencilFillPathInstancedNV(int pathNameType, ByteBuffer paths, int pathBase, int fillMode, int mask, int transformType, FloatBuffer transformValues) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilFillPathInstancedNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      if(transformValues != null) {
         BufferChecks.checkBuffer(transformValues, GLChecks.calculateTransformPathValues(transformType));
      }

      nglStencilFillPathInstancedNV(paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType), pathNameType, MemoryUtil.getAddress(paths), pathBase, fillMode, mask, transformType, MemoryUtil.getAddressSafe(transformValues), function_pointer);
   }

   static native void nglStencilFillPathInstancedNV(int var0, int var1, long var2, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glStencilStrokePathInstancedNV(int pathNameType, ByteBuffer paths, int pathBase, int reference, int mask, int transformType, FloatBuffer transformValues) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glStencilStrokePathInstancedNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      if(transformValues != null) {
         BufferChecks.checkBuffer(transformValues, GLChecks.calculateTransformPathValues(transformType));
      }

      nglStencilStrokePathInstancedNV(paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType), pathNameType, MemoryUtil.getAddress(paths), pathBase, reference, mask, transformType, MemoryUtil.getAddressSafe(transformValues), function_pointer);
   }

   static native void nglStencilStrokePathInstancedNV(int var0, int var1, long var2, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glPathCoverDepthFuncNV(int zfunc) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathCoverDepthFuncNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathCoverDepthFuncNV(zfunc, function_pointer);
   }

   static native void nglPathCoverDepthFuncNV(int var0, long var1);

   public static void glPathColorGenNV(int color, int genMode, int colorFormat, FloatBuffer coeffs) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathColorGenNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(coeffs != null) {
         BufferChecks.checkBuffer(coeffs, GLChecks.calculatePathColorGenCoeffsCount(genMode, colorFormat));
      }

      nglPathColorGenNV(color, genMode, colorFormat, MemoryUtil.getAddressSafe(coeffs), function_pointer);
   }

   static native void nglPathColorGenNV(int var0, int var1, int var2, long var3, long var5);

   public static void glPathTexGenNV(int texCoordSet, int genMode, FloatBuffer coeffs) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathTexGenNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(coeffs != null) {
         BufferChecks.checkDirect(coeffs);
      }

      nglPathTexGenNV(texCoordSet, genMode, GLChecks.calculatePathTextGenCoeffsPerComponent(coeffs, genMode), MemoryUtil.getAddressSafe(coeffs), function_pointer);
   }

   static native void nglPathTexGenNV(int var0, int var1, int var2, long var3, long var5);

   public static void glPathFogGenNV(int genMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPathFogGenNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPathFogGenNV(genMode, function_pointer);
   }

   static native void nglPathFogGenNV(int var0, long var1);

   public static void glCoverFillPathNV(int path, int coverMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCoverFillPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCoverFillPathNV(path, coverMode, function_pointer);
   }

   static native void nglCoverFillPathNV(int var0, int var1, long var2);

   public static void glCoverStrokePathNV(int name, int coverMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCoverStrokePathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCoverStrokePathNV(name, coverMode, function_pointer);
   }

   static native void nglCoverStrokePathNV(int var0, int var1, long var2);

   public static void glCoverFillPathInstancedNV(int pathNameType, ByteBuffer paths, int pathBase, int coverMode, int transformType, FloatBuffer transformValues) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCoverFillPathInstancedNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      if(transformValues != null) {
         BufferChecks.checkBuffer(transformValues, GLChecks.calculateTransformPathValues(transformType));
      }

      nglCoverFillPathInstancedNV(paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType), pathNameType, MemoryUtil.getAddress(paths), pathBase, coverMode, transformType, MemoryUtil.getAddressSafe(transformValues), function_pointer);
   }

   static native void nglCoverFillPathInstancedNV(int var0, int var1, long var2, int var4, int var5, int var6, long var7, long var9);

   public static void glCoverStrokePathInstancedNV(int pathNameType, ByteBuffer paths, int pathBase, int coverMode, int transformType, FloatBuffer transformValues) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCoverStrokePathInstancedNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      if(transformValues != null) {
         BufferChecks.checkBuffer(transformValues, GLChecks.calculateTransformPathValues(transformType));
      }

      nglCoverStrokePathInstancedNV(paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType), pathNameType, MemoryUtil.getAddress(paths), pathBase, coverMode, transformType, MemoryUtil.getAddressSafe(transformValues), function_pointer);
   }

   static native void nglCoverStrokePathInstancedNV(int var0, int var1, long var2, int var4, int var5, int var6, long var7, long var9);

   public static void glGetPathParameterNV(int name, int param, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)value, 4);
      nglGetPathParameterivNV(name, param, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathParameterivNV(int var0, int var1, long var2, long var4);

   public static int glGetPathParameteriNV(int name, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathParameterivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer value = APIUtil.getBufferInt(caps);
      nglGetPathParameterivNV(name, param, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static void glGetPathParameterfvNV(int name, int param, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)value, 4);
      nglGetPathParameterfvNV(name, param, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathParameterfvNV(int var0, int var1, long var2, long var4);

   public static float glGetPathParameterfNV(int name, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathParameterfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer value = APIUtil.getBufferFloat(caps);
      nglGetPathParameterfvNV(name, param, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static void glGetPathCommandsNV(int name, ByteBuffer commands) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathCommandsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(commands);
      nglGetPathCommandsNV(name, MemoryUtil.getAddress(commands), function_pointer);
   }

   static native void nglGetPathCommandsNV(int var0, long var1, long var3);

   public static void glGetPathCoordsNV(int name, FloatBuffer coords) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathCoordsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(coords);
      nglGetPathCoordsNV(name, MemoryUtil.getAddress(coords), function_pointer);
   }

   static native void nglGetPathCoordsNV(int var0, long var1, long var3);

   public static void glGetPathDashArrayNV(int name, FloatBuffer dashArray) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathDashArrayNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(dashArray);
      nglGetPathDashArrayNV(name, MemoryUtil.getAddress(dashArray), function_pointer);
   }

   static native void nglGetPathDashArrayNV(int var0, long var1, long var3);

   public static void glGetPathMetricsNV(int metricQueryMask, int pathNameType, ByteBuffer paths, int pathBase, int stride, FloatBuffer metrics) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathMetricsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(paths);
      BufferChecks.checkBuffer(metrics, GLChecks.calculateMetricsSize(metricQueryMask, stride));
      nglGetPathMetricsNV(metricQueryMask, paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType), pathNameType, MemoryUtil.getAddress(paths), pathBase, stride, MemoryUtil.getAddress(metrics), function_pointer);
   }

   static native void nglGetPathMetricsNV(int var0, int var1, int var2, long var3, int var5, int var6, long var7, long var9);

   public static void glGetPathMetricRangeNV(int metricQueryMask, int fistPathName, int numPaths, int stride, FloatBuffer metrics) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathMetricRangeNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer(metrics, GLChecks.calculateMetricsSize(metricQueryMask, stride));
      nglGetPathMetricRangeNV(metricQueryMask, fistPathName, numPaths, stride, MemoryUtil.getAddress(metrics), function_pointer);
   }

   static native void nglGetPathMetricRangeNV(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glGetPathSpacingNV(int pathListMode, int pathNameType, ByteBuffer paths, int pathBase, float advanceScale, float kerningScale, int transformType, FloatBuffer returnedSpacing) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathSpacingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      int numPaths = paths.remaining() / GLChecks.calculateBytesPerPathName(pathNameType);
      BufferChecks.checkDirect(paths);
      BufferChecks.checkBuffer(returnedSpacing, numPaths - 1);
      nglGetPathSpacingNV(pathListMode, numPaths, pathNameType, MemoryUtil.getAddress(paths), pathBase, advanceScale, kerningScale, transformType, MemoryUtil.getAddress(returnedSpacing), function_pointer);
   }

   static native void nglGetPathSpacingNV(int var0, int var1, int var2, long var3, int var5, float var6, float var7, int var8, long var9, long var11);

   public static void glGetPathColorGenNV(int color, int pname, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathColorGenivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)value, 16);
      nglGetPathColorGenivNV(color, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathColorGenivNV(int var0, int var1, long var2, long var4);

   public static int glGetPathColorGeniNV(int color, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathColorGenivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer value = APIUtil.getBufferInt(caps);
      nglGetPathColorGenivNV(color, pname, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static void glGetPathColorGenNV(int color, int pname, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathColorGenfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)value, 16);
      nglGetPathColorGenfvNV(color, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathColorGenfvNV(int var0, int var1, long var2, long var4);

   public static float glGetPathColorGenfNV(int color, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathColorGenfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer value = APIUtil.getBufferFloat(caps);
      nglGetPathColorGenfvNV(color, pname, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static void glGetPathTexGenNV(int texCoordSet, int pname, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathTexGenivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)value, 16);
      nglGetPathTexGenivNV(texCoordSet, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathTexGenivNV(int var0, int var1, long var2, long var4);

   public static int glGetPathTexGeniNV(int texCoordSet, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathTexGenivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer value = APIUtil.getBufferInt(caps);
      nglGetPathTexGenivNV(texCoordSet, pname, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static void glGetPathTexGenNV(int texCoordSet, int pname, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathTexGenfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)value, 16);
      nglGetPathTexGenfvNV(texCoordSet, pname, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglGetPathTexGenfvNV(int var0, int var1, long var2, long var4);

   public static float glGetPathTexGenfNV(int texCoordSet, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathTexGenfvNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer value = APIUtil.getBufferFloat(caps);
      nglGetPathTexGenfvNV(texCoordSet, pname, MemoryUtil.getAddress(value), function_pointer);
      return value.get(0);
   }

   public static boolean glIsPointInFillPathNV(int path, int mask, float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsPointInFillPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsPointInFillPathNV(path, mask, x, y, function_pointer);
      return __result;
   }

   static native boolean nglIsPointInFillPathNV(int var0, int var1, float var2, float var3, long var4);

   public static boolean glIsPointInStrokePathNV(int path, float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsPointInStrokePathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsPointInStrokePathNV(path, x, y, function_pointer);
      return __result;
   }

   static native boolean nglIsPointInStrokePathNV(int var0, float var1, float var2, long var3);

   public static float glGetPathLengthNV(int path, int startSegment, int numSegments) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPathLengthNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      float __result = nglGetPathLengthNV(path, startSegment, numSegments, function_pointer);
      return __result;
   }

   static native float nglGetPathLengthNV(int var0, int var1, int var2, long var3);

   public static boolean glPointAlongPathNV(int path, int startSegment, int numSegments, float distance, FloatBuffer x, FloatBuffer y, FloatBuffer tangentX, FloatBuffer tangentY) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointAlongPathNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(x != null) {
         BufferChecks.checkBuffer((FloatBuffer)x, 1);
      }

      if(y != null) {
         BufferChecks.checkBuffer((FloatBuffer)y, 1);
      }

      if(tangentX != null) {
         BufferChecks.checkBuffer((FloatBuffer)tangentX, 1);
      }

      if(tangentY != null) {
         BufferChecks.checkBuffer((FloatBuffer)tangentY, 1);
      }

      boolean __result = nglPointAlongPathNV(path, startSegment, numSegments, distance, MemoryUtil.getAddressSafe(x), MemoryUtil.getAddressSafe(y), MemoryUtil.getAddressSafe(tangentX), MemoryUtil.getAddressSafe(tangentY), function_pointer);
      return __result;
   }

   static native boolean nglPointAlongPathNV(int var0, int var1, int var2, float var3, long var4, long var6, long var8, long var10, long var12);
}
