package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;

abstract class PeerInfo {
   private final ByteBuffer handle;
   private Thread locking_thread;
   private int lock_count;

   protected PeerInfo(ByteBuffer handle) {
      this.handle = handle;
   }

   private void lockAndInitHandle() throws LWJGLException {
      this.doLockAndInitHandle();
   }

   public final synchronized void unlock() throws LWJGLException {
      if(this.lock_count <= 0) {
         throw new IllegalStateException("PeerInfo not locked!");
      } else if(Thread.currentThread() != this.locking_thread) {
         throw new IllegalStateException("PeerInfo already locked by " + this.locking_thread);
      } else {
         --this.lock_count;
         if(this.lock_count == 0) {
            this.doUnlock();
            this.locking_thread = null;
            this.notify();
         }

      }
   }

   protected abstract void doLockAndInitHandle() throws LWJGLException;

   protected abstract void doUnlock() throws LWJGLException;

   public final synchronized ByteBuffer lockAndGetHandle() throws LWJGLException {
      Thread this_thread = Thread.currentThread();

      while(this.locking_thread != null && this.locking_thread != this_thread) {
         try {
            this.wait();
         } catch (InterruptedException var3) {
            LWJGLUtil.log("Interrupted while waiting for PeerInfo lock: " + var3);
         }
      }

      if(this.lock_count == 0) {
         this.locking_thread = this_thread;
         this.doLockAndInitHandle();
      }

      ++this.lock_count;
      return this.getHandle();
   }

   protected final ByteBuffer getHandle() {
      return this.handle;
   }

   public void destroy() {
   }
}
