package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Context;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.PixelFormatLWJGL;

interface DrawableLWJGL extends Drawable {
   void setPixelFormat(PixelFormatLWJGL var1) throws LWJGLException;

   void setPixelFormat(PixelFormatLWJGL var1, ContextAttribs var2) throws LWJGLException;

   PixelFormatLWJGL getPixelFormat();

   Context getContext();

   Context createSharedContext() throws LWJGLException;

   void checkGLError();

   void setSwapInterval(int var1);

   void swapBuffers() throws LWJGLException;

   void initContext(float var1, float var2, float var3);
}
