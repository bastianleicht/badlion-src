package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.Graphics;

final class MacOSXGLCanvas extends Canvas {
   private static final long serialVersionUID = 6916664741667434870L;
   private boolean canvas_painted;
   private boolean dirty;

   public void update(Graphics g) {
      this.paint(g);
   }

   public void paint(Graphics g) {
      synchronized(this) {
         this.dirty = true;
         this.canvas_painted = true;
      }
   }

   public boolean syncCanvasPainted() {
      synchronized(this) {
         boolean result = this.canvas_painted;
         this.canvas_painted = false;
         return result;
      }
   }

   public boolean syncIsDirty() {
      synchronized(this) {
         boolean result = this.dirty;
         this.dirty = false;
         return result;
      }
   }
}
