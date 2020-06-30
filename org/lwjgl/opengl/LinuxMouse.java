package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.EventQueue;
import org.lwjgl.opengl.LinuxDisplay;
import org.lwjgl.opengl.LinuxEvent;

final class LinuxMouse {
   private static final int POINTER_WARP_BORDER = 10;
   private static final int WHEEL_SCALE = 120;
   private int button_count;
   private static final int Button1 = 1;
   private static final int Button2 = 2;
   private static final int Button3 = 3;
   private static final int Button4 = 4;
   private static final int Button5 = 5;
   private static final int Button6 = 6;
   private static final int Button7 = 7;
   private static final int Button8 = 8;
   private static final int Button9 = 9;
   private static final int ButtonPress = 4;
   private static final int ButtonRelease = 5;
   private final long display;
   private final long window;
   private final long input_window;
   private final long warp_atom;
   private final IntBuffer query_pointer_buffer = BufferUtils.createIntBuffer(4);
   private final ByteBuffer event_buffer = ByteBuffer.allocate(22);
   private int last_x;
   private int last_y;
   private int accum_dx;
   private int accum_dy;
   private int accum_dz;
   private byte[] buttons;
   private EventQueue event_queue;
   private long last_event_nanos;

   LinuxMouse(long display, long window, long input_window) throws LWJGLException {
      this.display = display;
      this.window = window;
      this.input_window = input_window;
      this.warp_atom = LinuxDisplay.nInternAtom(display, "_LWJGL", false);
      this.button_count = nGetButtonCount(display);
      this.buttons = new byte[this.button_count];
      this.reset(false, false);
   }

   private void reset(boolean grab, boolean warp_pointer) {
      this.event_queue = new EventQueue(this.event_buffer.capacity());
      this.accum_dx = this.accum_dy = 0;
      long root_window = nQueryPointer(this.display, this.window, this.query_pointer_buffer);
      int root_x = this.query_pointer_buffer.get(0);
      int root_y = this.query_pointer_buffer.get(1);
      int win_x = this.query_pointer_buffer.get(2);
      int win_y = this.query_pointer_buffer.get(3);
      this.last_x = win_x;
      this.last_y = this.transformY(win_y);
      this.doHandlePointerMotion(grab, warp_pointer, root_window, root_x, root_y, win_x, win_y, this.last_event_nanos);
   }

   public void read(ByteBuffer buffer) {
      this.event_queue.copyEvents(buffer);
   }

   public void poll(boolean grab, IntBuffer coord_buffer, ByteBuffer buttons_buffer) {
      if(grab) {
         coord_buffer.put(0, this.accum_dx);
         coord_buffer.put(1, this.accum_dy);
      } else {
         coord_buffer.put(0, this.last_x);
         coord_buffer.put(1, this.last_y);
      }

      coord_buffer.put(2, this.accum_dz);
      this.accum_dx = this.accum_dy = this.accum_dz = 0;

      for(int i = 0; i < this.buttons.length; ++i) {
         buttons_buffer.put(i, this.buttons[i]);
      }

   }

   private void putMouseEventWithCoords(byte button, byte state, int coord1, int coord2, int dz, long nanos) {
      this.event_buffer.clear();
      this.event_buffer.put(button).put(state).putInt(coord1).putInt(coord2).putInt(dz).putLong(nanos);
      this.event_buffer.flip();
      this.event_queue.putEvent(this.event_buffer);
      this.last_event_nanos = nanos;
   }

   private void setCursorPos(boolean grab, int x, int y, long nanos) {
      y = this.transformY(y);
      int dx = x - this.last_x;
      int dy = y - this.last_y;
      if(dx != 0 || dy != 0) {
         this.accum_dx += dx;
         this.accum_dy += dy;
         this.last_x = x;
         this.last_y = y;
         if(grab) {
            this.putMouseEventWithCoords((byte)-1, (byte)0, dx, dy, 0, nanos);
         } else {
            this.putMouseEventWithCoords((byte)-1, (byte)0, x, y, 0, nanos);
         }
      }

   }

   private void doWarpPointer(int center_x, int center_y) {
      nSendWarpEvent(this.display, this.input_window, this.warp_atom, center_x, center_y);
      nWarpCursor(this.display, this.window, center_x, center_y);
   }

   private static native void nSendWarpEvent(long var0, long var2, long var4, int var6, int var7);

   private void doHandlePointerMotion(boolean grab, boolean warp_pointer, long root_window, int root_x, int root_y, int win_x, int win_y, long nanos) {
      this.setCursorPos(grab, win_x, win_y, nanos);
      if(warp_pointer) {
         int root_window_height = nGetWindowHeight(this.display, root_window);
         int root_window_width = nGetWindowWidth(this.display, root_window);
         int window_height = nGetWindowHeight(this.display, this.window);
         int window_width = nGetWindowWidth(this.display, this.window);
         int win_left = root_x - win_x;
         int win_top = root_y - win_y;
         int win_right = win_left + window_width;
         int win_bottom = win_top + window_height;
         int border_left = Math.max(0, win_left);
         int border_top = Math.max(0, win_top);
         int border_right = Math.min(root_window_width, win_right);
         int border_bottom = Math.min(root_window_height, win_bottom);
         boolean outside_limits = root_x < border_left + 10 || root_y < border_top + 10 || root_x > border_right - 10 || root_y > border_bottom - 10;
         if(outside_limits) {
            int center_x = (border_right - border_left) / 2;
            int center_y = (border_bottom - border_top) / 2;
            this.doWarpPointer(center_x, center_y);
         }

      }
   }

   public void changeGrabbed(boolean grab, boolean warp_pointer) {
      this.reset(grab, warp_pointer);
   }

   public int getButtonCount() {
      return this.buttons.length;
   }

   private int transformY(int y) {
      return nGetWindowHeight(this.display, this.window) - 1 - y;
   }

   private static native int nGetWindowHeight(long var0, long var2);

   private static native int nGetWindowWidth(long var0, long var2);

   private static native int nGetButtonCount(long var0);

   private static native long nQueryPointer(long var0, long var2, IntBuffer var4);

   public void setCursorPosition(int x, int y) {
      nWarpCursor(this.display, this.window, x, this.transformY(y));
   }

   private static native void nWarpCursor(long var0, long var2, int var4, int var5);

   private void handlePointerMotion(boolean grab, boolean warp_pointer, long millis, long root_window, int x_root, int y_root, int x, int y) {
      this.doHandlePointerMotion(grab, warp_pointer, root_window, x_root, y_root, x, y, millis * 1000000L);
   }

   private void handleButton(boolean grab, int button, byte state, long nanos) {
      byte button_num;
      switch(button) {
      case 1:
         button_num = 0;
         break;
      case 2:
         button_num = 2;
         break;
      case 3:
         button_num = 1;
         break;
      case 4:
      case 5:
      default:
         if(button <= 9 || button > this.button_count) {
            return;
         }

         button_num = (byte)(button - 1);
         break;
      case 6:
         button_num = 5;
         break;
      case 7:
         button_num = 6;
         break;
      case 8:
         button_num = 3;
         break;
      case 9:
         button_num = 4;
      }

      this.buttons[button_num] = state;
      this.putMouseEvent(grab, button_num, state, 0, nanos);
   }

   private void putMouseEvent(boolean grab, byte button, byte state, int dz, long nanos) {
      if(grab) {
         this.putMouseEventWithCoords(button, state, 0, 0, dz, nanos);
      } else {
         this.putMouseEventWithCoords(button, state, this.last_x, this.last_y, dz, nanos);
      }

   }

   private void handleButtonPress(boolean grab, byte button, long nanos) {
      int delta = 0;
      switch(button) {
      case 4:
         delta = 120;
         this.putMouseEvent(grab, (byte)-1, (byte)0, delta, nanos);
         this.accum_dz += delta;
         break;
      case 5:
         delta = -120;
         this.putMouseEvent(grab, (byte)-1, (byte)0, delta, nanos);
         this.accum_dz += delta;
         break;
      default:
         this.handleButton(grab, button, (byte)1, nanos);
      }

   }

   private void handleButtonEvent(boolean grab, long millis, int type, byte button) {
      long nanos = millis * 1000000L;
      switch(type) {
      case 4:
         this.handleButtonPress(grab, button, nanos);
         break;
      case 5:
         this.handleButton(grab, button, (byte)0, nanos);
      }

   }

   private void resetCursor(int x, int y) {
      this.last_x = x;
      this.last_y = this.transformY(y);
   }

   private void handleWarpEvent(int x, int y) {
      this.resetCursor(x, y);
   }

   public boolean filterEvent(boolean grab, boolean warp_pointer, LinuxEvent event) {
      switch(event.getType()) {
      case 4:
      case 5:
         this.handleButtonEvent(grab, event.getButtonTime(), event.getButtonType(), (byte)event.getButtonButton());
         return true;
      case 6:
         this.handlePointerMotion(grab, warp_pointer, event.getButtonTime(), event.getButtonRoot(), event.getButtonXRoot(), event.getButtonYRoot(), event.getButtonX(), event.getButtonY());
         return true;
      case 33:
         if(event.getClientMessageType() == this.warp_atom) {
            this.handleWarpEvent(event.getClientData(0), event.getClientData(1));
            return true;
         }
      default:
         return false;
      }
   }
}
