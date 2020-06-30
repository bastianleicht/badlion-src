package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.EXTDrawBuffers2;
import org.lwjgl.opengl.GL41;

public final class ARBViewportArray {
   public static final int GL_MAX_VIEWPORTS = 33371;
   public static final int GL_VIEWPORT_SUBPIXEL_BITS = 33372;
   public static final int GL_VIEWPORT_BOUNDS_RANGE = 33373;
   public static final int GL_LAYER_PROVOKING_VERTEX = 33374;
   public static final int GL_VIEWPORT_INDEX_PROVOKING_VERTEX = 33375;
   public static final int GL_FIRST_VERTEX_CONVENTION = 36429;
   public static final int GL_LAST_VERTEX_CONVENTION = 36430;
   public static final int GL_PROVOKING_VERTEX = 36431;
   public static final int GL_UNDEFINED_VERTEX = 33376;

   public static void glViewportArray(int first, FloatBuffer v) {
      GL41.glViewportArray(first, v);
   }

   public static void glViewportIndexedf(int index, float x, float y, float w, float h) {
      GL41.glViewportIndexedf(index, x, y, w, h);
   }

   public static void glViewportIndexed(int index, FloatBuffer v) {
      GL41.glViewportIndexed(index, v);
   }

   public static void glScissorArray(int first, IntBuffer v) {
      GL41.glScissorArray(first, v);
   }

   public static void glScissorIndexed(int index, int left, int bottom, int width, int height) {
      GL41.glScissorIndexed(index, left, bottom, width, height);
   }

   public static void glScissorIndexed(int index, IntBuffer v) {
      GL41.glScissorIndexed(index, v);
   }

   public static void glDepthRangeArray(int first, DoubleBuffer v) {
      GL41.glDepthRangeArray(first, v);
   }

   public static void glDepthRangeIndexed(int index, double n, double f) {
      GL41.glDepthRangeIndexed(index, n, f);
   }

   public static void glGetFloat(int target, int index, FloatBuffer data) {
      GL41.glGetFloat(target, index, data);
   }

   public static float glGetFloat(int target, int index) {
      return GL41.glGetFloat(target, index);
   }

   public static void glGetDouble(int target, int index, DoubleBuffer data) {
      GL41.glGetDouble(target, index, data);
   }

   public static double glGetDouble(int target, int index) {
      return GL41.glGetDouble(target, index);
   }

   public static void glGetIntegerIndexedEXT(int target, int index, IntBuffer v) {
      EXTDrawBuffers2.glGetIntegerIndexedEXT(target, index, v);
   }

   public static int glGetIntegerIndexedEXT(int target, int index) {
      return EXTDrawBuffers2.glGetIntegerIndexedEXT(target, index);
   }

   public static void glEnableIndexedEXT(int target, int index) {
      EXTDrawBuffers2.glEnableIndexedEXT(target, index);
   }

   public static void glDisableIndexedEXT(int target, int index) {
      EXTDrawBuffers2.glDisableIndexedEXT(target, index);
   }

   public static boolean glIsEnabledIndexedEXT(int target, int index) {
      return EXTDrawBuffers2.glIsEnabledIndexedEXT(target, index);
   }
}
