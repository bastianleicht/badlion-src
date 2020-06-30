package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextGL;
import org.lwjgl.opengl.ContextImplementation;
import org.lwjgl.opengl.LinuxDisplay;
import org.lwjgl.opengl.PeerInfo;

final class LinuxContextImplementation implements ContextImplementation {
   public ByteBuffer create(PeerInfo peer_info, IntBuffer attribs, ByteBuffer shared_context_handle) throws LWJGLException {
      LinuxDisplay.lockAWT();

      ByteBuffer var5;
      try {
         ByteBuffer peer_handle = peer_info.lockAndGetHandle();

         try {
            var5 = nCreate(peer_handle, attribs, shared_context_handle);
         } finally {
            peer_info.unlock();
         }
      } finally {
         LinuxDisplay.unlockAWT();
      }

      return var5;
   }

   private static native ByteBuffer nCreate(ByteBuffer var0, IntBuffer var1, ByteBuffer var2) throws LWJGLException;

   native long getGLXContext(ByteBuffer var1);

   native long getDisplay(ByteBuffer var1);

   public void releaseDrawable(ByteBuffer context_handle) throws LWJGLException {
   }

   public void swapBuffers() throws LWJGLException {
      ContextGL current_context = ContextGL.getCurrentContext();
      if(current_context == null) {
         throw new IllegalStateException("No context is current");
      } else {
         synchronized(current_context) {
            PeerInfo current_peer_info = current_context.getPeerInfo();
            LinuxDisplay.lockAWT();

            try {
               ByteBuffer peer_handle = current_peer_info.lockAndGetHandle();

               try {
                  nSwapBuffers(peer_handle);
               } finally {
                  current_peer_info.unlock();
               }
            } finally {
               LinuxDisplay.unlockAWT();
            }

         }
      }
   }

   private static native void nSwapBuffers(ByteBuffer var0) throws LWJGLException;

   public void releaseCurrentContext() throws LWJGLException {
      ContextGL current_context = ContextGL.getCurrentContext();
      if(current_context == null) {
         throw new IllegalStateException("No context is current");
      } else {
         synchronized(current_context) {
            PeerInfo current_peer_info = current_context.getPeerInfo();
            LinuxDisplay.lockAWT();

            try {
               ByteBuffer peer_handle = current_peer_info.lockAndGetHandle();

               try {
                  nReleaseCurrentContext(peer_handle);
               } finally {
                  current_peer_info.unlock();
               }
            } finally {
               LinuxDisplay.unlockAWT();
            }

         }
      }
   }

   private static native void nReleaseCurrentContext(ByteBuffer var0) throws LWJGLException;

   public void update(ByteBuffer context_handle) {
   }

   public void makeCurrent(PeerInfo peer_info, ByteBuffer handle) throws LWJGLException {
      LinuxDisplay.lockAWT();

      try {
         ByteBuffer peer_handle = peer_info.lockAndGetHandle();

         try {
            nMakeCurrent(peer_handle, handle);
         } finally {
            peer_info.unlock();
         }
      } finally {
         LinuxDisplay.unlockAWT();
      }

   }

   private static native void nMakeCurrent(ByteBuffer var0, ByteBuffer var1) throws LWJGLException;

   public boolean isCurrent(ByteBuffer handle) throws LWJGLException {
      LinuxDisplay.lockAWT();

      boolean var3;
      try {
         boolean result = nIsCurrent(handle);
         var3 = result;
      } finally {
         LinuxDisplay.unlockAWT();
      }

      return var3;
   }

   private static native boolean nIsCurrent(ByteBuffer var0) throws LWJGLException;

   public void setSwapInterval(int value) {
      ContextGL current_context = ContextGL.getCurrentContext();
      PeerInfo peer_info = current_context.getPeerInfo();
      if(current_context == null) {
         throw new IllegalStateException("No context is current");
      } else {
         synchronized(current_context) {
            LinuxDisplay.lockAWT();

            try {
               ByteBuffer peer_handle = peer_info.lockAndGetHandle();

               try {
                  nSetSwapInterval(peer_handle, current_context.getHandle(), value);
               } finally {
                  peer_info.unlock();
               }
            } catch (LWJGLException var18) {
               var18.printStackTrace();
            } finally {
               LinuxDisplay.unlockAWT();
            }

         }
      }
   }

   private static native void nSetSwapInterval(ByteBuffer var0, ByteBuffer var1, int var2);

   public void destroy(PeerInfo peer_info, ByteBuffer handle) throws LWJGLException {
      LinuxDisplay.lockAWT();

      try {
         ByteBuffer peer_handle = peer_info.lockAndGetHandle();

         try {
            nDestroy(peer_handle, handle);
         } finally {
            peer_info.unlock();
         }
      } finally {
         LinuxDisplay.unlockAWT();
      }

   }

   private static native void nDestroy(ByteBuffer var0, ByteBuffer var1) throws LWJGLException;
}
