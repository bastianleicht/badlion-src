package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.MacOSXPeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class MacOSXPbufferPeerInfo extends MacOSXPeerInfo {
   MacOSXPbufferPeerInfo(int width, int height, PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
      super(pixel_format, attribs, false, false, true, false);
      nCreate(this.getHandle(), width, height);
   }

   private static native void nCreate(ByteBuffer var0, int var1, int var2) throws LWJGLException;

   public void destroy() {
      nDestroy(this.getHandle());
   }

   private static native void nDestroy(ByteBuffer var0);

   protected void doLockAndInitHandle() throws LWJGLException {
   }

   protected void doUnlock() throws LWJGLException {
   }
}
