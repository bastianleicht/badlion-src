package org.lwjgl.opengl;

import java.awt.Canvas;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;

final class AWTSurfaceLock {
   private static final int WAIT_DELAY_MILLIS = 100;
   private final ByteBuffer lock_buffer = createHandle();
   private boolean firstLockSucceeded;

   private static native ByteBuffer createHandle();

   public ByteBuffer lockAndGetHandle(Canvas component) throws LWJGLException {
      while(!this.privilegedLockAndInitHandle(component)) {
         LWJGLUtil.log("Could not get drawing surface info, retrying...");

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var3) {
            LWJGLUtil.log("Interrupted while retrying: " + var3);
         }
      }

      return this.lock_buffer;
   }

   private boolean privilegedLockAndInitHandle(final Canvas component) throws LWJGLException {
      if(this.firstLockSucceeded) {
         return lockAndInitHandle(this.lock_buffer, component);
      } else {
         try {
            this.firstLockSucceeded = ((Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Boolean run() throws LWJGLException {
                  return Boolean.valueOf(AWTSurfaceLock.lockAndInitHandle(AWTSurfaceLock.this.lock_buffer, component));
               }
            })).booleanValue();
            return this.firstLockSucceeded;
         } catch (PrivilegedActionException var3) {
            throw (LWJGLException)var3.getException();
         }
      }
   }

   private static native boolean lockAndInitHandle(ByteBuffer var0, Canvas var1) throws LWJGLException;

   void unlock() throws LWJGLException {
      nUnlock(this.lock_buffer);
   }

   private static native void nUnlock(ByteBuffer var0) throws LWJGLException;
}
