package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.PeerInfo;

interface ContextImplementation {
   ByteBuffer create(PeerInfo var1, IntBuffer var2, ByteBuffer var3) throws LWJGLException;

   void swapBuffers() throws LWJGLException;

   void releaseDrawable(ByteBuffer var1) throws LWJGLException;

   void releaseCurrentContext() throws LWJGLException;

   void update(ByteBuffer var1);

   void makeCurrent(PeerInfo var1, ByteBuffer var2) throws LWJGLException;

   boolean isCurrent(ByteBuffer var1) throws LWJGLException;

   void setSwapInterval(int var1);

   void destroy(PeerInfo var1, ByteBuffer var2) throws LWJGLException;
}
