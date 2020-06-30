package org.lwjgl.opengl;

import org.lwjgl.opengl.GL43;

public final class ARBTextureView {
   public static final int GL_TEXTURE_VIEW_MIN_LEVEL = 33499;
   public static final int GL_TEXTURE_VIEW_NUM_LEVELS = 33500;
   public static final int GL_TEXTURE_VIEW_MIN_LAYER = 33501;
   public static final int GL_TEXTURE_VIEW_NUM_LAYERS = 33502;
   public static final int GL_TEXTURE_IMMUTABLE_LEVELS = 33503;

   public static void glTextureView(int texture, int target, int origtexture, int internalformat, int minlevel, int numlevels, int minlayer, int numlayers) {
      GL43.glTextureView(texture, target, origtexture, internalformat, minlevel, numlevels, minlayer, numlayers);
   }
}
