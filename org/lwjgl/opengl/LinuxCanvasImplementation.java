package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.AWTCanvasImplementation;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.LinuxAWTGLCanvasPeerInfo;
import org.lwjgl.opengl.LinuxDisplay;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class LinuxCanvasImplementation implements AWTCanvasImplementation {
   static int getScreenFromDevice(final GraphicsDevice device) throws LWJGLException {
      try {
         Method getScreen_method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Method run() throws Exception {
               return device.getClass().getMethod("getScreen", new Class[0]);
            }
         });
         Integer screen = (Integer)getScreen_method.invoke(device, new Object[0]);
         return screen.intValue();
      } catch (Exception var3) {
         throw new LWJGLException(var3);
      }
   }

   private static int getVisualIDFromConfiguration(final GraphicsConfiguration configuration) throws LWJGLException {
      try {
         Method getVisual_method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Method run() throws Exception {
               return configuration.getClass().getMethod("getVisual", new Class[0]);
            }
         });
         Integer visual = (Integer)getVisual_method.invoke(configuration, new Object[0]);
         return visual.intValue();
      } catch (Exception var3) {
         throw new LWJGLException(var3);
      }
   }

   public PeerInfo createPeerInfo(Canvas component, PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
      return new LinuxAWTGLCanvasPeerInfo(component);
   }

   public GraphicsConfiguration findConfiguration(GraphicsDevice device, PixelFormat pixel_format) throws LWJGLException {
      try {
         int screen = getScreenFromDevice(device);
         int visual_id_matching_format = findVisualIDFromFormat(screen, pixel_format);
         GraphicsConfiguration[] configurations = device.getConfigurations();

         for(GraphicsConfiguration configuration : configurations) {
            int visual_id = getVisualIDFromConfiguration(configuration);
            if(visual_id == visual_id_matching_format) {
               return configuration;
            }
         }
      } catch (LWJGLException var11) {
         LWJGLUtil.log("Got exception while trying to determine configuration: " + var11);
      }

      return null;
   }

   private static int findVisualIDFromFormat(int screen, PixelFormat pixel_format) throws LWJGLException {
      int var2;
      try {
         LinuxDisplay.lockAWT();

         try {
            GLContext.loadOpenGLLibrary();

            try {
               LinuxDisplay.incDisplay();
               var2 = nFindVisualIDFromFormat(LinuxDisplay.getDisplay(), screen, pixel_format);
            } finally {
               LinuxDisplay.decDisplay();
            }
         } finally {
            GLContext.unloadOpenGLLibrary();
         }
      } finally {
         LinuxDisplay.unlockAWT();
      }

      return var2;
   }

   private static native int nFindVisualIDFromFormat(long var0, int var2, PixelFormat var3) throws LWJGLException;
}
