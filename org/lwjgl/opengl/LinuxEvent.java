package org.lwjgl.opengl;

import java.nio.ByteBuffer;

final class LinuxEvent {
   public static final int FocusIn = 9;
   public static final int FocusOut = 10;
   public static final int KeyPress = 2;
   public static final int KeyRelease = 3;
   public static final int ButtonPress = 4;
   public static final int ButtonRelease = 5;
   public static final int MotionNotify = 6;
   public static final int EnterNotify = 7;
   public static final int LeaveNotify = 8;
   public static final int UnmapNotify = 18;
   public static final int MapNotify = 19;
   public static final int Expose = 12;
   public static final int ConfigureNotify = 22;
   public static final int ClientMessage = 33;
   private final ByteBuffer event_buffer = createEventBuffer();

   private static native ByteBuffer createEventBuffer();

   public void copyFrom(LinuxEvent event) {
      int pos = this.event_buffer.position();
      int event_pos = event.event_buffer.position();
      this.event_buffer.put(event.event_buffer);
      this.event_buffer.position(pos);
      event.event_buffer.position(event_pos);
   }

   public static native int getPending(long var0);

   public void sendEvent(long display, long window, boolean propagate, long event_mask) {
      nSendEvent(this.event_buffer, display, window, propagate, event_mask);
   }

   private static native void nSendEvent(ByteBuffer var0, long var1, long var3, boolean var5, long var6);

   public boolean filterEvent(long window) {
      return nFilterEvent(this.event_buffer, window);
   }

   private static native boolean nFilterEvent(ByteBuffer var0, long var1);

   public void nextEvent(long display) {
      nNextEvent(display, this.event_buffer);
   }

   private static native void nNextEvent(long var0, ByteBuffer var2);

   public int getType() {
      return nGetType(this.event_buffer);
   }

   private static native int nGetType(ByteBuffer var0);

   public long getWindow() {
      return nGetWindow(this.event_buffer);
   }

   private static native long nGetWindow(ByteBuffer var0);

   public void setWindow(long window) {
      nSetWindow(this.event_buffer, window);
   }

   private static native void nSetWindow(ByteBuffer var0, long var1);

   public int getFocusMode() {
      return nGetFocusMode(this.event_buffer);
   }

   private static native int nGetFocusMode(ByteBuffer var0);

   public int getFocusDetail() {
      return nGetFocusDetail(this.event_buffer);
   }

   private static native int nGetFocusDetail(ByteBuffer var0);

   public long getClientMessageType() {
      return nGetClientMessageType(this.event_buffer);
   }

   private static native long nGetClientMessageType(ByteBuffer var0);

   public int getClientData(int index) {
      return nGetClientData(this.event_buffer, index);
   }

   private static native int nGetClientData(ByteBuffer var0, int var1);

   public int getClientFormat() {
      return nGetClientFormat(this.event_buffer);
   }

   private static native int nGetClientFormat(ByteBuffer var0);

   public long getButtonTime() {
      return nGetButtonTime(this.event_buffer);
   }

   private static native long nGetButtonTime(ByteBuffer var0);

   public int getButtonState() {
      return nGetButtonState(this.event_buffer);
   }

   private static native int nGetButtonState(ByteBuffer var0);

   public int getButtonType() {
      return nGetButtonType(this.event_buffer);
   }

   private static native int nGetButtonType(ByteBuffer var0);

   public int getButtonButton() {
      return nGetButtonButton(this.event_buffer);
   }

   private static native int nGetButtonButton(ByteBuffer var0);

   public long getButtonRoot() {
      return nGetButtonRoot(this.event_buffer);
   }

   private static native long nGetButtonRoot(ByteBuffer var0);

   public int getButtonXRoot() {
      return nGetButtonXRoot(this.event_buffer);
   }

   private static native int nGetButtonXRoot(ByteBuffer var0);

   public int getButtonYRoot() {
      return nGetButtonYRoot(this.event_buffer);
   }

   private static native int nGetButtonYRoot(ByteBuffer var0);

   public int getButtonX() {
      return nGetButtonX(this.event_buffer);
   }

   private static native int nGetButtonX(ByteBuffer var0);

   public int getButtonY() {
      return nGetButtonY(this.event_buffer);
   }

   private static native int nGetButtonY(ByteBuffer var0);

   public long getKeyAddress() {
      return nGetKeyAddress(this.event_buffer);
   }

   private static native long nGetKeyAddress(ByteBuffer var0);

   public long getKeyTime() {
      return (long)nGetKeyTime(this.event_buffer);
   }

   private static native int nGetKeyTime(ByteBuffer var0);

   public int getKeyType() {
      return nGetKeyType(this.event_buffer);
   }

   private static native int nGetKeyType(ByteBuffer var0);

   public int getKeyKeyCode() {
      return nGetKeyKeyCode(this.event_buffer);
   }

   private static native int nGetKeyKeyCode(ByteBuffer var0);

   public int getKeyState() {
      return nGetKeyState(this.event_buffer);
   }

   private static native int nGetKeyState(ByteBuffer var0);
}
