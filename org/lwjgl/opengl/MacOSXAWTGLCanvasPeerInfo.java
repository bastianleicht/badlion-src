package org.lwjgl.opengl;

import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.MacOSXCanvasPeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class MacOSXAWTGLCanvasPeerInfo extends MacOSXCanvasPeerInfo {
   private final Canvas component;

   MacOSXAWTGLCanvasPeerInfo(Canvas component, PixelFormat pixel_format, ContextAttribs attribs, boolean support_pbuffer) throws LWJGLException {
      super(pixel_format, attribs, support_pbuffer);
      this.component = component;
   }

   protected void doLockAndInitHandle() throws LWJGLException {
      this.initHandle(this.component);
   }
}
