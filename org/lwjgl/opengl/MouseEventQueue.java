package org.lwjgl.opengl;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.AWTUtil;
import org.lwjgl.opengl.EventQueue;

class MouseEventQueue extends EventQueue implements MouseListener, MouseMotionListener, MouseWheelListener {
   private static final int WHEEL_SCALE = 120;
   public static final int NUM_BUTTONS = 3;
   private final Component component;
   private boolean grabbed;
   private int accum_dx;
   private int accum_dy;
   private int accum_dz;
   private int last_x;
   private int last_y;
   private boolean saved_control_state;
   private final ByteBuffer event = ByteBuffer.allocate(22);
   private final byte[] buttons = new byte[3];

   MouseEventQueue(Component component) {
      super(22);
      this.component = component;
   }

   public synchronized void register() {
      this.resetCursorToCenter();
      if(this.component != null) {
         this.component.addMouseListener(this);
         this.component.addMouseMotionListener(this);
         this.component.addMouseWheelListener(this);
      }

   }

   public synchronized void unregister() {
      if(this.component != null) {
         this.component.removeMouseListener(this);
         this.component.removeMouseMotionListener(this);
         this.component.removeMouseWheelListener(this);
      }

   }

   protected Component getComponent() {
      return this.component;
   }

   public synchronized void setGrabbed(boolean grabbed) {
      this.grabbed = grabbed;
      this.resetCursorToCenter();
   }

   public synchronized boolean isGrabbed() {
      return this.grabbed;
   }

   protected int transformY(int y) {
      return this.component != null?this.component.getHeight() - 1 - y:y;
   }

   protected void resetCursorToCenter() {
      this.clearEvents();
      this.accum_dx = this.accum_dy = 0;
      if(this.component != null) {
         Point cursor_location = AWTUtil.getCursorPosition(this.component);
         if(cursor_location != null) {
            this.last_x = cursor_location.x;
            this.last_y = cursor_location.y;
         }
      }

   }

   private void putMouseEvent(byte button, byte state, int dz, long nanos) {
      if(this.grabbed) {
         this.putMouseEventWithCoords(button, state, 0, 0, dz, nanos);
      } else {
         this.putMouseEventWithCoords(button, state, this.last_x, this.last_y, dz, nanos);
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
         coord_buffer.put(0, this.accum_dx);
         coord_buffer.put(1, this.accum_dy);
      } else {
         coord_buffer.put(0, this.last_x);
         coord_buffer.put(1, this.last_y);
      }

      coord_buffer.put(2, this.accum_dz);
      this.accum_dx = this.accum_dy = this.accum_dz = 0;
      int old_position = buttons_buffer.position();
      buttons_buffer.put(this.buttons, 0, this.buttons.length);
      buttons_buffer.position(old_position);
   }

   private void setCursorPos(int x, int y, long nanos) {
      y = this.transformY(y);
      if(!this.grabbed) {
         int dx = x - this.last_x;
         int dy = y - this.last_y;
         this.addDelta(dx, dy);
         this.last_x = x;
         this.last_y = y;
         this.putMouseEventWithCoords((byte)-1, (byte)0, x, y, 0, nanos);
      }
   }

   protected void addDelta(int dx, int dy) {
      this.accum_dx += dx;
      this.accum_dy += dy;
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   private void handleButton(MouseEvent e) {
      byte state;
      switch(e.getID()) {
      case 501:
         state = 1;
         break;
      case 502:
         state = 0;
         break;
      default:
         throw new IllegalArgumentException("Not a valid event ID: " + e.getID());
      }

      byte button;
      switch(e.getButton()) {
      case 0:
         return;
      case 1:
         if(state == 1) {
            this.saved_control_state = e.isControlDown();
         }

         if(this.saved_control_state) {
            if(this.buttons[1] == state) {
               return;
            }

            button = 1;
         } else {
            button = 0;
         }
         break;
      case 2:
         button = 2;
         break;
      case 3:
         if(this.buttons[1] == state) {
            return;
         }

         button = 1;
         break;
      default:
         throw new IllegalArgumentException("Not a valid button: " + e.getButton());
      }

      this.setButton(button, state, e.getWhen() * 1000000L);
   }

   public synchronized void mousePressed(MouseEvent e) {
      this.handleButton(e);
   }

   private void setButton(byte button, byte state, long nanos) {
      this.buttons[button] = state;
      this.putMouseEvent(button, state, 0, nanos);
   }

   public synchronized void mouseReleased(MouseEvent e) {
      this.handleButton(e);
   }

   private void handleMotion(MouseEvent e) {
      if(this.grabbed) {
         this.updateDeltas(e.getWhen() * 1000000L);
      } else {
         this.setCursorPos(e.getX(), e.getY(), e.getWhen() * 1000000L);
      }

   }

   public synchronized void mouseDragged(MouseEvent e) {
      this.handleMotion(e);
   }

   public synchronized void mouseMoved(MouseEvent e) {
      this.handleMotion(e);
   }

   private void handleWheel(int amount, long nanos) {
      this.accum_dz += amount;
      this.putMouseEvent((byte)-1, (byte)0, amount, nanos);
   }

   protected void updateDeltas(long nanos) {
   }

   public synchronized void mouseWheelMoved(MouseWheelEvent e) {
      int wheel_amount = -e.getWheelRotation() * 120;
      this.handleWheel(wheel_amount, e.getWhen() * 1000000L);
   }
}
