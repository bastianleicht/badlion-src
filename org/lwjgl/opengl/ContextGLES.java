package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Context;
import org.lwjgl.opengl.DrawableGLES;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengles.EGL;
import org.lwjgl.opengles.EGLContext;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.PowerManagementEventException;

final class ContextGLES implements Context {
   private static final ThreadLocal current_context_local = new ThreadLocal();
   private final DrawableGLES drawable;
   private final EGLContext eglContext;
   private final org.lwjgl.opengles.ContextAttribs contextAttribs;
   private boolean destroyed;
   private boolean destroy_requested;
   private Thread thread;

   public EGLContext getEGLContext() {
      return this.eglContext;
   }

   org.lwjgl.opengles.ContextAttribs getContextAttribs() {
      return this.contextAttribs;
   }

   static ContextGLES getCurrentContext() {
      return (ContextGLES)current_context_local.get();
   }

   ContextGLES(DrawableGLES drawable, org.lwjgl.opengles.ContextAttribs attribs, ContextGLES shared_context) throws LWJGLException {
      if(drawable == null) {
         throw new IllegalArgumentException();
      } else {
         ContextGLES context_lock = shared_context != null?shared_context:this;
         synchronized(context_lock) {
            if(shared_context != null && shared_context.destroyed) {
               throw new IllegalArgumentException("Shared context is destroyed");
            } else {
               this.drawable = drawable;
               this.contextAttribs = attribs;
               this.eglContext = drawable.getEGLDisplay().createContext(drawable.getEGLConfig(), shared_context == null?null:shared_context.eglContext, attribs == null?(new org.lwjgl.opengles.ContextAttribs(2)).getAttribList():attribs.getAttribList());
            }
         }
      }
   }

   public void releaseCurrent() throws LWJGLException, PowerManagementEventException {
      EGL.eglReleaseCurrent(this.drawable.getEGLDisplay());
      org.lwjgl.opengles.GLContext.useContext((Object)null);
      current_context_local.set((Object)null);
      synchronized(this) {
         this.thread = null;
         this.checkDestroy();
      }
   }

   public static void swapBuffers() throws LWJGLException, PowerManagementEventException {
      ContextGLES current_context = getCurrentContext();
      if(current_context != null) {
         current_context.drawable.getEGLSurface().swapBuffers();
      }

   }

   private boolean canAccess() {
      return this.thread == null || Thread.currentThread() == this.thread;
   }

   private void checkAccess() {
      if(!this.canAccess()) {
         throw new IllegalStateException("From thread " + Thread.currentThread() + ": " + this.thread + " already has the context current");
      }
   }

   public synchronized void makeCurrent() throws LWJGLException, PowerManagementEventException {
      this.checkAccess();
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         this.thread = Thread.currentThread();
         current_context_local.set(this);
         this.eglContext.makeCurrent(this.drawable.getEGLSurface());
         org.lwjgl.opengles.GLContext.useContext(this);
      }
   }

   public synchronized boolean isCurrent() throws LWJGLException {
      if(this.destroyed) {
         throw new IllegalStateException("Context is destroyed");
      } else {
         return EGL.eglIsCurrentContext(this.eglContext);
      }
   }

   private void checkDestroy() {
      if(!this.destroyed && this.destroy_requested) {
         try {
            this.eglContext.destroy();
            this.destroyed = true;
            this.thread = null;
         } catch (LWJGLException var2) {
            LWJGLUtil.log("Exception occurred while destroying context: " + var2);
         }
      }

   }

   public static void setSwapInterval(int value) {
      ContextGLES current_context = getCurrentContext();
      if(current_context != null) {
         try {
            current_context.drawable.getEGLDisplay().setSwapInterval(value);
         } catch (LWJGLException var3) {
            LWJGLUtil.log("Failed to set swap interval. Reason: " + var3.getMessage());
         }
      }

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
            if(org.lwjgl.opengles.GLContext.getCapabilities() != null && org.lwjgl.opengles.GLContext.getCapabilities().OpenGLES20) {
               error = GLES20.glGetError();
            }

            try {
               this.releaseCurrent();
            } catch (PowerManagementEventException var4) {
               ;
            }
         }

         this.checkDestroy();
         if(was_current && error != 0) {
            throw new OpenGLException(error);
         }
      }
   }

   public void releaseDrawable() throws LWJGLException {
   }

   static {
      Sys.initialize();
   }
}
