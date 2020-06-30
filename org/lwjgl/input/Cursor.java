package org.lwjgl.input;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.OpenGLPackageAccess;

public class Cursor {
   public static final int CURSOR_ONE_BIT_TRANSPARENCY = 1;
   public static final int CURSOR_8_BIT_ALPHA = 2;
   public static final int CURSOR_ANIMATION = 4;
   private final Cursor.CursorElement[] cursors;
   private int index;
   private boolean destroyed;

   public Cursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
      synchronized(OpenGLPackageAccess.global_lock) {
         if((getCapabilities() & 1) == 0) {
            throw new LWJGLException("Native cursors not supported");
         } else {
            BufferChecks.checkBufferSize(images, width * height * numImages);
            if(delays != null) {
               BufferChecks.checkBufferSize(delays, numImages);
            }

            if(!Mouse.isCreated()) {
               throw new IllegalStateException("Mouse must be created before creating cursor objects");
            } else if(width * height * numImages > images.remaining()) {
               throw new IllegalArgumentException("width*height*numImages > images.remaining()");
            } else if(xHotspot < width && xHotspot >= 0) {
               if(yHotspot < height && yHotspot >= 0) {
                  Sys.initialize();
                  yHotspot = height - 1 - yHotspot;
                  this.cursors = createCursors(width, height, xHotspot, yHotspot, numImages, images, delays);
               } else {
                  throw new IllegalArgumentException("yHotspot > height || yHotspot < 0");
               }
            } else {
               throw new IllegalArgumentException("xHotspot > width || xHotspot < 0");
            }
         }
      }
   }

   public static int getMinCursorSize() {
      synchronized(OpenGLPackageAccess.global_lock) {
         if(!Mouse.isCreated()) {
            throw new IllegalStateException("Mouse must be created.");
         } else {
            return Mouse.getImplementation().getMinCursorSize();
         }
      }
   }

   public static int getMaxCursorSize() {
      synchronized(OpenGLPackageAccess.global_lock) {
         if(!Mouse.isCreated()) {
            throw new IllegalStateException("Mouse must be created.");
         } else {
            return Mouse.getImplementation().getMaxCursorSize();
         }
      }
   }

   public static int getCapabilities() {
      synchronized(OpenGLPackageAccess.global_lock) {
         return Mouse.getImplementation() != null?Mouse.getImplementation().getNativeCursorCapabilities():OpenGLPackageAccess.createImplementation().getNativeCursorCapabilities();
      }
   }

   private static Cursor.CursorElement[] createCursors(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
      IntBuffer images_copy = BufferUtils.createIntBuffer(images.remaining());
      flipImages(width, height, numImages, images, images_copy);
      Cursor.CursorElement[] cursors;
      switch(LWJGLUtil.getPlatform()) {
      case 1:
         Object handle = Mouse.getImplementation().createCursor(width, height, xHotspot, yHotspot, numImages, images_copy, delays);
         Cursor.CursorElement cursor_element = new Cursor.CursorElement(handle, -1L, -1L);
         cursors = new Cursor.CursorElement[]{cursor_element};
         return cursors;
      case 2:
         convertARGBtoABGR(images_copy);
         cursors = new Cursor.CursorElement[numImages];

         for(int i = 0; i < numImages; ++i) {
            Object handle = Mouse.getImplementation().createCursor(width, height, xHotspot, yHotspot, 1, images_copy, (IntBuffer)null);
            long delay = delays != null?(long)delays.get(i):0L;
            long timeout = System.currentTimeMillis();
            cursors[i] = new Cursor.CursorElement(handle, delay, timeout);
            images_copy.position(width * height * (i + 1));
         }

         return cursors;
      case 3:
         cursors = new Cursor.CursorElement[numImages];

         for(int i = 0; i < numImages; ++i) {
            int size = width * height;

            for(int j = 0; j < size; ++j) {
               int index = j + i * size;
               int alpha = images_copy.get(index) >> 24 & 255;
               if(alpha != 255) {
                  images_copy.put(index, 0);
               }
            }

            Object handle = Mouse.getImplementation().createCursor(width, height, xHotspot, yHotspot, 1, images_copy, (IntBuffer)null);
            long delay = delays != null?(long)delays.get(i):0L;
            long timeout = System.currentTimeMillis();
            cursors[i] = new Cursor.CursorElement(handle, delay, timeout);
            images_copy.position(width * height * (i + 1));
         }

         return cursors;
      default:
         throw new RuntimeException("Unknown OS");
      }
   }

   private static void convertARGBtoABGR(IntBuffer imageBuffer) {
      for(int i = 0; i < imageBuffer.limit(); ++i) {
         int argbColor = imageBuffer.get(i);
         byte alpha = (byte)(argbColor >>> 24);
         byte blue = (byte)(argbColor >>> 16);
         byte green = (byte)(argbColor >>> 8);
         byte red = (byte)argbColor;
         int abgrColor = ((alpha & 255) << 24) + ((red & 255) << 16) + ((green & 255) << 8) + (blue & 255);
         imageBuffer.put(i, abgrColor);
      }

   }

   private static void flipImages(int width, int height, int numImages, IntBuffer images, IntBuffer images_copy) {
      for(int i = 0; i < numImages; ++i) {
         int start_index = i * width * height;
         flipImage(width, height, start_index, images, images_copy);
      }

   }

   private static void flipImage(int width, int height, int start_index, IntBuffer images, IntBuffer images_copy) {
      for(int y = 0; y < height >> 1; ++y) {
         int index_y_1 = y * width + start_index;
         int index_y_2 = (height - y - 1) * width + start_index;

         for(int x = 0; x < width; ++x) {
            int index1 = index_y_1 + x;
            int index2 = index_y_2 + x;
            int temp_pixel = images.get(index1 + images.position());
            images_copy.put(index1, images.get(index2 + images.position()));
            images_copy.put(index2, temp_pixel);
         }
      }

   }

   Object getHandle() {
      this.checkValid();
      return this.cursors[this.index].cursorHandle;
   }

   private void checkValid() {
      if(this.destroyed) {
         throw new IllegalStateException("The cursor is destroyed");
      }
   }

   public void destroy() {
      synchronized(OpenGLPackageAccess.global_lock) {
         if(!this.destroyed) {
            if(Mouse.getNativeCursor() == this) {
               try {
                  Mouse.setNativeCursor((Cursor)null);
               } catch (LWJGLException var7) {
                  ;
               }
            }

            for(Cursor.CursorElement cursor : this.cursors) {
               Mouse.getImplementation().destroyCursor(cursor.cursorHandle);
            }

            this.destroyed = true;
         }
      }
   }

   protected void setTimeout() {
      this.checkValid();
      this.cursors[this.index].timeout = System.currentTimeMillis() + this.cursors[this.index].delay;
   }

   protected boolean hasTimedOut() {
      this.checkValid();
      return this.cursors.length > 1 && this.cursors[this.index].timeout < System.currentTimeMillis();
   }

   protected void nextCursor() {
      this.checkValid();
      this.index = ++this.index % this.cursors.length;
   }

   private static class CursorElement {
      final Object cursorHandle;
      final long delay;
      long timeout;

      CursorElement(Object cursorHandle, long delay, long timeout) {
         this.cursorHandle = cursorHandle;
         this.delay = delay;
         this.timeout = timeout;
      }
   }
}
