package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.AWTCanvasImplementation;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.WindowsAWTGLCanvasPeerInfo;

final class WindowsCanvasImplementation implements AWTCanvasImplementation {
   public PeerInfo createPeerInfo(Canvas component, PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
      return new WindowsAWTGLCanvasPeerInfo(component, pixel_format);
   }

   public GraphicsConfiguration findConfiguration(GraphicsDevice device, PixelFormat pixel_format) throws LWJGLException {
      return null;
   }

   static {
      Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            try {
               System.loadLibrary("jawt");
            } catch (UnsatisfiedLinkError var2) {
               LWJGLUtil.log("Failed to load jawt: " + var2.getMessage());
            }

            return null;
         }
      });
   }
}
