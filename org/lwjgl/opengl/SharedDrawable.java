package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextGL;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.DrawableGL;
import org.lwjgl.opengl.DrawableLWJGL;

public final class SharedDrawable extends DrawableGL {
   public SharedDrawable(Drawable drawable) throws LWJGLException {
      this.context = (ContextGL)((DrawableLWJGL)drawable).createSharedContext();
   }

   public ContextGL createSharedContext() {
      throw new UnsupportedOperationException();
   }
}
