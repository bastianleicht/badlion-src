package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.EventQueue;
import org.lwjgl.opengl.MacOSXDisplay;

final class MacOSXNativeMouse extends EventQueue {
   private static final int WHEEL_SCALE = 120;
   private static final int NUM_BUTTONS = 3;
   private ByteBuffer window_handle;
   private MacOSXDisplay display;
   private boolean grabbed;
   private float accum_dx;
   private float accum_dy;
   private int accum_dz;
   private float last_x;
   private float last_y;
   private boolean saved_control_state;
   private final ByteBuffer event = ByteBuffer.allocate(22);
   private IntBuffer delta_buffer = BufferUtils.createIntBuffer(2);
   private int skip_event;
   private final byte[] buttons = new byte[3];

   MacOSXNativeMouse(MacOSXDisplay display, ByteBuffer window_handle) {
      super(22);
      this.display = display;
      this.window_handle = window_handle;
   }

   private native void nSetCursorPosition(ByteBuffer var1, int var2, int var3);

   public static native void nGrabMouse(boolean var0);

   private native void nRegisterMouseListener(ByteBuffer var1);

   private native void nUnregisterMouseListener(ByteBuffer var1);

   private static native long nCreateCursor(int var0, int var1, int var2, int var3, int var4, IntBuffer var5, int var6, IntBuffer var7, int var8) throws LWJGLException;

   private static native void nDestroyCursor(long var0);

   private static native void nSetCursor(long var0) throws LWJGLException;

   public synchronized void register() {
      this.nRegisterMouseListener(this.window_handle);
   }

   public static long createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
      try {
         return nCreateCursor(width, height, xHotspot, yHotspot, numImages, images, images.position(), delays, delays != null?delays.position():-1);
      } catch (LWJGLException var8) {
         throw var8;
      }
   }

   public static void destroyCursor(long cursor_handle) {
      nDestroyCursor(cursor_handle);
   }

   public static void setCursor(long cursor_handle) throws LWJGLException {
      try {
         nSetCursor(cursor_handle);
      } catch (LWJGLException var3) {
         throw var3;
      }
   }

   public synchronized void setCursorPosition(int x, int y) {
      this.nSetCursorPosition(this.window_handle, x, y);
   }

   public synchronized void unregister() {
      this.nUnregisterMouseListener(this.window_handle);
   }

   public synchronized void setGrabbed(boolean grabbed) {
      this.grabbed = grabbed;
      nGrabMouse(grabbed);
      this.skip_event = 1;
      this.accum_dx = this.accum_dy = 0.0F;
   }

   public synchronized boolean isGrabbed() {
      return this.grabbed;
   }

   protected void resetCursorToCenter() {
      this.clearEvents();
      this.accum_dx = this.accum_dy = 0.0F;
      if(this.display != null) {
         this.last_x = (float)(this.display.getWidth() / 2);
         this.last_y = (float)(this.display.getHeight() / 2);
      }

   }

   private void putMouseEvent(byte button, byte state, int dz, long nanos) {
      if(this.grabbed) {
         this.putMouseEventWithCoords(button, state, 0, 0, dz, nanos);
      } else {
         this.putMouseEventWithCoords(button, state, (int)this.last_x, (int)this.last_y, dz, nanos);
      }

   }

   protected void putMouseEventWithCoords(byte button, byte state, int coord1, int coord2, int dz, long nanos) {
      this.event.clear();
      this.event.put(button).put(state).putInt(coord1).putInt(coord2).putInt(dz).putLong(nanos);
      this.event.flip();
      this.putEvent(this.event);
   }

   public synchronized void poll(IntBuffer coord_buffer, ByteBuffer buttons_buffer) {
      if(this.grabbed) {
         coord_buffer.put(0, (int)this.accum_dx);
         coord_buffer.put(1, (int)this.accum_dy);
      } else {
         coord_buffer.put(0, (int)this.last_x);
         coord_buffer.put(1, (int)this.last_y);
      }

      coord_buffer.put(2, this.accum_dz);
      this.accum_dx = this.accum_dy = (float)(this.accum_dz = 0);
      int old_position = buttons_buffer.position();
      buttons_buffer.put(this.buttons, 0, this.buttons.length);
      buttons_buffer.position(old_position);
   }

   private void setCursorPos(float x, float y, long nanos) {
      if(!this.grabbed) {
         float dx = x - this.last_x;
         float dy = y - this.last_y;
         this.addDelta(dx, dy);
         this.last_x = x;
         this.last_y = y;
         this.putMouseEventWithCoords((byte)-1, (byte)0, (int)x, (int)y, 0, nanos);
      }
   }

   protected void addDelta(float dx, float dy) {
      this.accum_dx += dx;
      this.accum_dy += -dy;
   }

   public synchronized void setButton(int button, int state, long nanos) {
      this.buttons[button] = (byte)state;
      this.putMouseEvent((byte)button, (byte)state, 0, nanos);
   }

   public synchronized void mouseMoved(float x, float y, float dx, float dy, float dz, long nanos) {
      if(this.skip_event > 0) {
         --this.skip_event;
         if(this.skip_event == 0) {
            this.last_x = x;
            this.last_y = y;
         }

      } else {
         if(dz != 0.0F) {
            if(dy == 0.0F) {
               dy = dx;
            }

            int wheel_amount = (int)(dy * 120.0F);
            this.accum_dz += wheel_amount;
            this.putMouseEvent((byte)-1, (byte)0, wheel_amount, nanos);
         } else if(this.grabbed) {
            if(dx != 0.0F || dy != 0.0F) {
               this.putMouseEventWithCoords((byte)-1, (byte)0, (int)dx, (int)(-dy), 0, nanos);
               this.addDelta(dx, dy);
            }
         } else {
            this.setCursorPos(x, y, nanos);
         }

      }
   }
}
