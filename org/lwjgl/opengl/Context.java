package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

interface Context {
   boolean isCurrent() throws LWJGLException;

   void makeCurrent() throws LWJGLException;

   void releaseCurrent() throws LWJGLException;

   void releaseDrawable() throws LWJGLException;
}
