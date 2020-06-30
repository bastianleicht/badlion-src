package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL33;

public final class ARBSamplerObjects {
   public static final int GL_SAMPLER_BINDING = 35097;

   public static void glGenSamplers(IntBuffer samplers) {
      GL33.glGenSamplers(samplers);
   }

   public static int glGenSamplers() {
      return GL33.glGenSamplers();
   }

   public static void glDeleteSamplers(IntBuffer samplers) {
      GL33.glDeleteSamplers(samplers);
   }

   public static void glDeleteSamplers(int sampler) {
      GL33.glDeleteSamplers(sampler);
   }

   public static boolean glIsSampler(int sampler) {
      return GL33.glIsSampler(sampler);
   }

   public static void glBindSampler(int unit, int sampler) {
      GL33.glBindSampler(unit, sampler);
   }

   public static void glSamplerParameteri(int sampler, int pname, int param) {
      GL33.glSamplerParameteri(sampler, pname, param);
   }

   public static void glSamplerParameterf(int sampler, int pname, float param) {
      GL33.glSamplerParameterf(sampler, pname, param);
   }

   public static void glSamplerParameter(int sampler, int pname, IntBuffer params) {
      GL33.glSamplerParameter(sampler, pname, params);
   }

   public static void glSamplerParameter(int sampler, int pname, FloatBuffer params) {
      GL33.glSamplerParameter(sampler, pname, params);
   }

   public static void glSamplerParameterI(int sampler, int pname, IntBuffer params) {
      GL33.glSamplerParameterI(sampler, pname, params);
   }

   public static void glSamplerParameterIu(int sampler, int pname, IntBuffer params) {
      GL33.glSamplerParameterIu(sampler, pname, params);
   }

   public static void glGetSamplerParameter(int sampler, int pname, IntBuffer params) {
      GL33.glGetSamplerParameter(sampler, pname, params);
   }

   public static int glGetSamplerParameteri(int sampler, int pname) {
      return GL33.glGetSamplerParameteri(sampler, pname);
   }

   public static void glGetSamplerParameter(int sampler, int pname, FloatBuffer params) {
      GL33.glGetSamplerParameter(sampler, pname, params);
   }

   public static float glGetSamplerParameterf(int sampler, int pname) {
      return GL33.glGetSamplerParameterf(sampler, pname);
   }

   public static void glGetSamplerParameterI(int sampler, int pname, IntBuffer params) {
      GL33.glGetSamplerParameterI(sampler, pname, params);
   }

   public static int glGetSamplerParameterIi(int sampler, int pname) {
      return GL33.glGetSamplerParameterIi(sampler, pname);
   }

   public static void glGetSamplerParameterIu(int sampler, int pname, IntBuffer params) {
      GL33.glGetSamplerParameterIu(sampler, pname, params);
   }

   public static int glGetSamplerParameterIui(int sampler, int pname) {
      return GL33.glGetSamplerParameterIui(sampler, pname);
   }
}
