package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.LinuxDisplay;
import org.lwjgl.opengl.LinuxPeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class LinuxDisplayPeerInfo extends LinuxPeerInfo {
   final boolean egl;

   LinuxDisplayPeerInfo() throws LWJGLException {
      this.egl = true;
      org.lwjgl.opengles.GLContext.loadOpenGLLibrary();
   }

   LinuxDisplayPeerInfo(PixelFormat pixel_format) throws LWJGLException {
      this.egl = false;
      LinuxDisplay.lockAWT();

      try {
         GLContext.loadOpenGLLibrary();

         try {
            LinuxDisplay.incDisplay();

            try {
               initDefaultPeerInfo(LinuxDisplay.getDisplay(), LinuxDisplay.getDefaultScreen(), this.getHandle(), pixel_format);
            } catch (LWJGLException var7) {
               LinuxDisplay.decDisplay();
               throw var7;
            }
         } catch (LWJGLException var8) {
            GLContext.unloadOpenGLLibrary();
            throw var8;
         }
      } finally {
         LinuxDisplay.unlockAWT();
      }

   }

   private static native void initDefaultPeerInfo(long var0, int var2, ByteBuffer var3, PixelFormat var4) throws LWJGLException;

   protected void doLockAndInitHandle() throws LWJGLException {
      LinuxDisplay.lockAWT();

      try {
         initDrawable(LinuxDisplay.getWindow(), this.getHandle());
      } finally {
         LinuxDisplay.unlockAWT();
      }

   }

   private static native void initDrawable(long var0, ByteBuffer var2);

   protected void doUnlock() throws LWJGLException {
   }

   public void destroy() {
      super.destroy();
      if(this.egl) {
         org.lwjgl.opengles.GLContext.unloadOpenGLLibrary();
      } else {
         LinuxDisplay.lockAWT();
         LinuxDisplay.decDisplay();
         GLContext.unloadOpenGLLibrary();
         LinuxDisplay.unlockAWT();
      }

   }
}
