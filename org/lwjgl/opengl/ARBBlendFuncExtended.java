package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;

public final class ARBBlendFuncExtended {
   public static final int GL_SRC1_COLOR = 35065;
   public static final int GL_SRC1_ALPHA = 34185;
   public static final int GL_ONE_MINUS_SRC1_COLOR = 35066;
   public static final int GL_ONE_MINUS_SRC1_ALPHA = 35067;
   public static final int GL_MAX_DUAL_SOURCE_DRAW_BUFFERS = 35068;

   public static void glBindFragDataLocationIndexed(int program, int colorNumber, int index, ByteBuffer name) {
      GL33.glBindFragDataLocationIndexed(program, colorNumber, index, name);
   }

   public static void glBindFragDataLocationIndexed(int program, int colorNumber, int index, CharSequence name) {
      GL33.glBindFragDataLocationIndexed(program, colorNumber, index, name);
   }

   public static int glGetFragDataIndex(int program, ByteBuffer name) {
      return GL33.glGetFragDataIndex(program, name);
   }

   public static int glGetFragDataIndex(int program, CharSequence name) {
      return GL33.glGetFragDataIndex(program, name);
   }
}
