package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL40;

public final class ARBDrawIndirect {
   public static final int GL_DRAW_INDIRECT_BUFFER = 36671;
   public static final int GL_DRAW_INDIRECT_BUFFER_BINDING = 36675;

   public static void glDrawArraysIndirect(int mode, ByteBuffer indirect) {
      GL40.glDrawArraysIndirect(mode, indirect);
   }

   public static void glDrawArraysIndirect(int mode, long indirect_buffer_offset) {
      GL40.glDrawArraysIndirect(mode, indirect_buffer_offset);
   }

   public static void glDrawArraysIndirect(int mode, IntBuffer indirect) {
      GL40.glDrawArraysIndirect(mode, indirect);
   }

   public static void glDrawElementsIndirect(int mode, int type, ByteBuffer indirect) {
      GL40.glDrawElementsIndirect(mode, type, indirect);
   }

   public static void glDrawElementsIndirect(int mode, int type, long indirect_buffer_offset) {
      GL40.glDrawElementsIndirect(mode, type, indirect_buffer_offset);
   }

   public static void glDrawElementsIndirect(int mode, int type, IntBuffer indirect) {
      GL40.glDrawElementsIndirect(mode, type, indirect);
   }
}
