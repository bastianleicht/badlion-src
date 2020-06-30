package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.opengl.GL33;

public final class ARBTimerQuery {
   public static final int GL_TIME_ELAPSED = 35007;
   public static final int GL_TIMESTAMP = 36392;

   public static void glQueryCounter(int id, int target) {
      GL33.glQueryCounter(id, target);
   }

   public static void glGetQueryObject(int id, int pname, LongBuffer params) {
      GL33.glGetQueryObject(id, pname, params);
   }

   public static long glGetQueryObjecti64(int id, int pname) {
      return GL33.glGetQueryObjecti64(id, pname);
   }

   public static void glGetQueryObjectu(int id, int pname, LongBuffer params) {
      GL33.glGetQueryObjectu(id, pname, params);
   }

   public static long glGetQueryObjectui64(int id, int pname) {
      return GL33.glGetQueryObjectui64(id, pname);
   }
}
