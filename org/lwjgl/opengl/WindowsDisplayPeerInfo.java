package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.WindowsPeerInfo;

final class WindowsDisplayPeerInfo extends WindowsPeerInfo {
   final boolean egl;

   WindowsDisplayPeerInfo(boolean egl) throws LWJGLException {
      this.egl = egl;
      if(egl) {
         org.lwjgl.opengles.GLContext.loadOpenGLLibrary();
      } else {
         GLContext.loadOpenGLLibrary();
      }

   }

   void initDC(long hwnd, long hdc) throws LWJGLException {
      nInitDC(this.getHandle(), hwnd, hdc);
   }

   private static native void nInitDC(ByteBuffer var0, long var1, long var3);

   protected void doLockAndInitHandle() throws LWJGLException {
   }

   protected void doUnlock() throws LWJGLException {
   }

   public void destroy() {
      super.destroy();
      if(this.egl) {
         org.lwjgl.opengles.GLContext.unloadOpenGLLibrary();
      } else {
         GLContext.unloadOpenGLLibrary();
      }

   }
}
