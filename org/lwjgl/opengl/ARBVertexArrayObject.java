package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL30;

public final class ARBVertexArrayObject {
   public static final int GL_VERTEX_ARRAY_BINDING = 34229;

   public static void glBindVertexArray(int array) {
      GL30.glBindVertexArray(array);
   }

   public static void glDeleteVertexArrays(IntBuffer arrays) {
      GL30.glDeleteVertexArrays(arrays);
   }

   public static void glDeleteVertexArrays(int array) {
      GL30.glDeleteVertexArrays(array);
   }

   public static void glGenVertexArrays(IntBuffer arrays) {
      GL30.glGenVertexArrays(arrays);
   }

   public static int glGenVertexArrays() {
      return GL30.glGenVertexArrays();
   }

   public static boolean glIsVertexArray(int array) {
      return GL30.glIsVertexArray(array);
   }
}
