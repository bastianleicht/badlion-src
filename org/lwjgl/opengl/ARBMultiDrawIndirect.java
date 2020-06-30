package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL43;

public final class ARBMultiDrawIndirect {
   public static void glMultiDrawArraysIndirect(int mode, ByteBuffer indirect, int primcount, int stride) {
      GL43.glMultiDrawArraysIndirect(mode, indirect, primcount, stride);
   }

   public static void glMultiDrawArraysIndirect(int mode, long indirect_buffer_offset, int primcount, int stride) {
      GL43.glMultiDrawArraysIndirect(mode, indirect_buffer_offset, primcount, stride);
   }

   public static void glMultiDrawArraysIndirect(int mode, IntBuffer indirect, int primcount, int stride) {
      GL43.glMultiDrawArraysIndirect(mode, indirect, primcount, stride);
   }

   public static void glMultiDrawElementsIndirect(int mode, int type, ByteBuffer indirect, int primcount, int stride) {
      GL43.glMultiDrawElementsIndirect(mode, type, indirect, primcount, stride);
   }

   public static void glMultiDrawElementsIndirect(int mode, int type, long indirect_buffer_offset, int primcount, int stride) {
      GL43.glMultiDrawElementsIndirect(mode, type, indirect_buffer_offset, primcount, stride);
   }

   public static void glMultiDrawElementsIndirect(int mode, int type, IntBuffer indirect, int primcount, int stride) {
      GL43.glMultiDrawElementsIndirect(mode, type, indirect, primcount, stride);
   }
}
