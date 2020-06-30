package org.lwjgl.opengl;

import java.awt.Canvas;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.DrawableLWJGL;
import org.lwjgl.opengl.InputImplementation;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;

interface DisplayImplementation extends InputImplementation {
   void createWindow(DrawableLWJGL var1, DisplayMode var2, Canvas var3, int var4, int var5) throws LWJGLException;

   void destroyWindow();

   void switchDisplayMode(DisplayMode var1) throws LWJGLException;

   void resetDisplayMode();

   int getGammaRampLength();

   void setGammaRamp(FloatBuffer var1) throws LWJGLException;

   String getAdapter();

   String getVersion();

   DisplayMode init() throws LWJGLException;

   void setTitle(String var1);

   boolean isCloseRequested();

   boolean isVisible();

   boolean isActive();

   boolean isDirty();

   PeerInfo createPeerInfo(PixelFormat var1, ContextAttribs var2) throws LWJGLException;

   void update();

   void reshape(int var1, int var2, int var3, int var4);

   DisplayMode[] getAvailableDisplayModes() throws LWJGLException;

   int getPbufferCapabilities();

   boolean isBufferLost(PeerInfo var1);

   PeerInfo createPbuffer(int var1, int var2, PixelFormat var3, ContextAttribs var4, IntBuffer var5, IntBuffer var6) throws LWJGLException;

   void setPbufferAttrib(PeerInfo var1, int var2, int var3);

   void bindTexImageToPbuffer(PeerInfo var1, int var2);

   void releaseTexImageFromPbuffer(PeerInfo var1, int var2);

   int setIcon(ByteBuffer[] var1);

   void setResizable(boolean var1);

   boolean wasResized();

   int getWidth();

   int getHeight();

   int getX();

   int getY();

   float getPixelScaleFactor();
}
