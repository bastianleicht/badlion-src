package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL40;

public final class ARBTransformFeedback2 {
   public static final int GL_TRANSFORM_FEEDBACK = 36386;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_PAUSED = 36387;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_ACTIVE = 36388;
   public static final int GL_TRANSFORM_FEEDBACK_BINDING = 36389;

   public static void glBindTransformFeedback(int target, int id) {
      GL40.glBindTransformFeedback(target, id);
   }

   public static void glDeleteTransformFeedbacks(IntBuffer ids) {
      GL40.glDeleteTransformFeedbacks(ids);
   }

   public static void glDeleteTransformFeedbacks(int id) {
      GL40.glDeleteTransformFeedbacks(id);
   }

   public static void glGenTransformFeedbacks(IntBuffer ids) {
      GL40.glGenTransformFeedbacks(ids);
   }

   public static int glGenTransformFeedbacks() {
      return GL40.glGenTransformFeedbacks();
   }

   public static boolean glIsTransformFeedback(int id) {
      return GL40.glIsTransformFeedback(id);
   }

   public static void glPauseTransformFeedback() {
      GL40.glPauseTransformFeedback();
   }

   public static void glResumeTransformFeedback() {
      GL40.glResumeTransformFeedback();
   }

   public static void glDrawTransformFeedback(int mode, int id) {
      GL40.glDrawTransformFeedback(mode, id);
   }
}
