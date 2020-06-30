package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL42;

public final class ARBBaseInstance {
   public static void glDrawArraysInstancedBaseInstance(int mode, int first, int count, int primcount, int baseinstance) {
      GL42.glDrawArraysInstancedBaseInstance(mode, first, count, primcount, baseinstance);
   }

   public static void glDrawElementsInstancedBaseInstance(int mode, ByteBuffer indices, int primcount, int baseinstance) {
      GL42.glDrawElementsInstancedBaseInstance(mode, indices, primcount, baseinstance);
   }

   public static void glDrawElementsInstancedBaseInstance(int mode, IntBuffer indices, int primcount, int baseinstance) {
      GL42.glDrawElementsInstancedBaseInstance(mode, indices, primcount, baseinstance);
   }

   public static void glDrawElementsInstancedBaseInstance(int mode, ShortBuffer indices, int primcount, int baseinstance) {
      GL42.glDrawElementsInstancedBaseInstance(mode, indices, primcount, baseinstance);
   }

   public static void glDrawElementsInstancedBaseInstance(int mode, int indices_count, int type, long indices_buffer_offset, int primcount, int baseinstance) {
      GL42.glDrawElementsInstancedBaseInstance(mode, indices_count, type, indices_buffer_offset, primcount, baseinstance);
   }

   public static void glDrawElementsInstancedBaseVertexBaseInstance(int mode, ByteBuffer indices, int primcount, int basevertex, int baseinstance) {
      GL42.glDrawElementsInstancedBaseVertexBaseInstance(mode, indices, primcount, basevertex, baseinstance);
   }

   public static void glDrawElementsInstancedBaseVertexBaseInstance(int mode, IntBuffer indices, int primcount, int basevertex, int baseinstance) {
      GL42.glDrawElementsInstancedBaseVertexBaseInstance(mode, indices, primcount, basevertex, baseinstance);
   }

   public static void glDrawElementsInstancedBaseVertexBaseInstance(int mode, ShortBuffer indices, int primcount, int basevertex, int baseinstance) {
      GL42.glDrawElementsInstancedBaseVertexBaseInstance(mode, indices, primcount, basevertex, baseinstance);
   }

   public static void glDrawElementsInstancedBaseVertexBaseInstance(int mode, int indices_count, int type, long indices_buffer_offset, int primcount, int basevertex, int baseinstance) {
      GL42.glDrawElementsInstancedBaseVertexBaseInstance(mode, indices_count, type, indices_buffer_offset, primcount, basevertex, baseinstance);
   }
}
