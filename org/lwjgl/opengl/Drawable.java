package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;

public interface Drawable {
   boolean isCurrent() throws LWJGLException;

   void makeCurrent() throws LWJGLException;

   void releaseContext() throws LWJGLException;

   void destroy();

   void setCLSharingProperties(PointerBuffer var1) throws LWJGLException;
}
