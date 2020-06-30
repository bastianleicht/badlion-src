package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.Sys;
import org.lwjgl.opengl.CallbackUtil;
import org.lwjgl.opengl.Context;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.ContextImplementation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.LinuxContextImplementation;
import org.lwjgl.opengl.MacOSXContextImplementation;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.WindowsContextImplementation;

final class ContextGL implements Context {
   private static final ContextImplementation implementation = createImplementation();
   private static final ThreadLocal current_context_local = new ThreadLocal();
   private final ByteBuffer handle;
   private final PeerInfo peer_info;
   private final ContextAttribs contextAttribs;
   private final boolean forwardCompatible;
   private boolean destroyed;
   private boolean destroy_requested;
   private Thread thread;

   private static ContextImplementation createImplementation() {
      switch(LWJGLUtil.getPlatform()) {
      case 1:
         return new LinuxContextImplementation();
      case 2:
         return new MacOSXContextImplementation();
      case 3:
         return new WindowsContextImplementation();
      default:
         throw new IllegalStateException("Unsupported platform");
      }
   }

   PeerInfo getPeerInfo() {
      return this.peer_info;
   }

   ContextAttribs getContextAttribs() {
      return this.contextAttribs;
   }

   static ContextGL getCurrentContext() {
      return (ContextGL)current_context_local.get();
   }

   ContextGL(PeerInfo peer_info, ContextAttribs attribs, ContextGL shared_context) throws LWJGLException {
      ContextGL context_lock = shared_context != null?shared_context:this;
      synchronized(context_lock) {
         if(shared_context != null && shared_context.destroyed) {
            throw new IllegalArgumentException("Shared context is destroyed");
         } else {
            GLContext.loadOpenGLLibrary();

            try {
               this.peer_info = peer_info;
               this.contextAttribs = attribs;
               IntBuffer attribList;
               if(attribs != null) {
                  attribList = attribs.getAttribList();
                  this.forwardCompatible = attribs.isForwardCompatible();
               } else {
                  attribList = null;
                  this.forwardCompatible = false;
               }

               this.handle = implementation.create(peer_info, attribList, shared_context != null?shared_context.handle:null);
            } catch (LWJGLException var8) {
               GLContext.unloadOpenGLLibrary();
               throw var8;
            }

         }
      }
   }

   public void releaseCurrent() throws LWJGLException {
      ContextGL current_context = getCurrentContext();
      if(current_context != null) {
         implementation.releaseCurrentContext();
         GLContext.useContext((Object)null);
         current_context_local.set((Object)null);
         synchronized(current_context) {
            current_context.thread = null;
            current_context.checkDestroy();
         }
      }

   }

   public synchronized void releaseDrawable() throws LWJGLException {
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         implementation.releaseDrawable(this.getHandle());
      }
   }

   public synchronized void update() {
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         implementation.update(this.getHandle());
      }
   }

   public static void swapBuffers() throws LWJGLException {
      implementation.swapBuffers();
   }

   private boolean canAccess() {
      return this.thread == null || Thread.currentThread() == this.thread;
   }

   private void checkAccess() {
      if(!this.canAccess()) {
         throw new IllegalStateException("From thread " + Thread.currentThread() + ": " + this.thread + " already has the context current");
      }
   }

   public synchronized void makeCurrent() throws LWJGLException {
      this.checkAccess();
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         this.thread = Thread.currentThread();
         current_context_local.set(this);
         implementation.makeCurrent(this.peer_info, this.handle);
         GLContext.useContext(this, this.forwardCompatible);
      }
   }

   ByteBuffer getHandle() {
      return this.handle;
   }

   public synchronized boolean isCurrent() throws LWJGLException {
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         return implementation.isCurrent(this.handle);
      }
   }

   private void checkDestroy() {
      if(!this.destroyed && this.destroy_requested) {
         try {
            this.releaseDrawable();
            implementation.destroy(this.peer_info, this.handle);
            CallbackUtil.unregisterCallbacks(this);
            this.destroyed = true;
            this.thread = null;
            GLContext.unloadOpenGLLibrary();
         } catch (LWJGLException var2) {
            LWJGLUtil.log("Exception occurred while destroying context: " + var2);
         }
      }

   }

   public static void setSwapInterval(int value) {
      implementation.setSwapInterval(value);
   }

   public synchronized void forceDestroy() throws LWJGLException {
      this.checkAccess();
      this.destroy();
   }

   public synchronized void destroy() throws LWJGLException {
      if(!this.destroyed) {
         this.destroy_requested = true;
         boolean was_current = this.isCurrent();
         int error = 0;
         if(was_current) {
            try {
               error = GL11.glGetError();
            } catch (Exception var4) {
               ;
            }

            this.releaseCurrent();
         }

         this.checkDestroy();
         if(was_current && error != 0) {
            throw new OpenGLException(error);
         }
      }
   }

   public synchronized void setCLSharingProperties(PointerBuffer properties) throws LWJGLException {
      ByteBuffer peer_handle = this.peer_info.lockAndGetHandle();

      try {
         switch(LWJGLUtil.getPlatform()) {
         case 1:
            LinuxContextImplementation implLinux = (LinuxContextImplementation)implementation;
            properties.put(8200L).put(implLinux.getGLXContext(this.handle));
            properties.put(8202L).put(implLinux.getDisplay(peer_handle));
            break;
         case 2:
            if(LWJGLUtil.isMacOSXEqualsOrBetterThan(10, 6)) {
               MacOSXContextImplementation implMacOSX = (MacOSXContextImplementation)implementation;
               long CGLShareGroup = implMacOSX.getCGLShareGroup(this.handle);
               properties.put(268435456L).put(CGLShareGroup);
               break;
            }
         default:
            throw new UnsupportedOperationException("CL/GL context sharing is not supported on this platform.");
         case 3:
            WindowsContextImplementation implWindows = (WindowsContextImplementation)implementation;
            properties.put(8200L).put(implWindows.getHGLRC(this.handle));
            properties.put(8203L).put(implWindows.getHDC(peer_handle));
         }
      } finally {
         this.peer_info.unlock();
      }

   }

   static {
      Sys.initialize();
   }
}
