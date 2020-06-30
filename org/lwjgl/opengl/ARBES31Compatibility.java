package org.lwjgl.opengl;

import org.lwjgl.opengl.GL45;

public final class ARBES31Compatibility {
   public static void glMemoryBarrierByRegion(int barriers) {
      GL45.glMemoryBarrierByRegion(barriers);
   }
}
