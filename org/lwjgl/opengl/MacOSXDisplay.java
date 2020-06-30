package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.Robot;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.AWTUtil;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayImplementation;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.DrawableGL;
import org.lwjgl.opengl.DrawableLWJGL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.KeyboardEventQueue;
import org.lwjgl.opengl.MacOSXCanvasPeerInfo;
import org.lwjgl.opengl.MacOSXDisplayPeerInfo;
import org.lwjgl.opengl.MacOSXMouseEventQueue;
import org.lwjgl.opengl.MacOSXNativeKeyboard;
import org.lwjgl.opengl.MacOSXNativeMouse;
import org.lwjgl.opengl.MacOSXPbufferPeerInfo;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class MacOSXDisplay implements DisplayImplementation {
   private static final int PBUFFER_HANDLE_SIZE = 24;
   private static final int GAMMA_LENGTH = 256;
   private Canvas canvas;
   private Robot robot;
   private MacOSXMouseEventQueue mouse_queue;
   private KeyboardEventQueue keyboard_queue;
   private DisplayMode requested_mode;
   private MacOSXNativeMouse mouse;
   private MacOSXNativeKeyboard keyboard;
   private ByteBuffer window;
   private ByteBuffer context;
   private boolean skipViewportValue = false;
   private static final IntBuffer current_viewport = BufferUtils.createIntBuffer(16);
   private boolean mouseInsideWindow;
   private boolean close_requested;
   private boolean native_mode = true;
   private boolean updateNativeCursor = false;
   private long currentNativeCursor = 0L;
   private boolean enableHighDPI = false;
   private float scaleFactor = 1.0F;

   private native ByteBuffer nCreateWindow(int var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, boolean var10, ByteBuffer var11, ByteBuffer var12) throws LWJGLException;

   private native Object nGetCurrentDisplayMode();

   private native void nGetDisplayModes(Object var1);

   private native boolean nIsMiniaturized(ByteBuffer var1);

   private native boolean nIsFocused(ByteBuffer var1);

   private native void nSetResizable(ByteBuffer var1, boolean var2);

   private native void nResizeWindow(ByteBuffer var1, int var2, int var3, int var4, int var5);

   private native boolean nWasResized(ByteBuffer var1);

   private native int nGetX(ByteBuffer var1);

   private native int nGetY(ByteBuffer var1);

   private native int nGetWidth(ByteBuffer var1);

   private native int nGetHeight(ByteBuffer var1);

   private native boolean nIsNativeMode(ByteBuffer var1);

   private static boolean isUndecorated() {
      return Display.getPrivilegedBoolean("org.lwjgl.opengl.Window.undecorated");
   }

   public void createWindow(DrawableLWJGL drawable, DisplayMode mode, Canvas parent, int x, int y) throws LWJGLException {
      boolean fullscreen = Display.isFullscreen();
      boolean resizable = Display.isResizable();
      boolean parented = parent != null && !fullscreen;
      boolean enableFullscreenModeAPI = LWJGLUtil.isMacOSXEqualsOrBetterThan(10, 7) && parent == null && !Display.getPrivilegedBoolean("org.lwjgl.opengl.Display.disableOSXFullscreenModeAPI");
      this.enableHighDPI = LWJGLUtil.isMacOSXEqualsOrBetterThan(10, 7) && parent == null && (Display.getPrivilegedBoolean("org.lwjgl.opengl.Display.enableHighDPI") || fullscreen);
      if(parented) {
         this.canvas = parent;
      } else {
         this.canvas = null;
      }

      this.close_requested = false;
      DrawableGL gl_drawable = (DrawableGL)Display.getDrawable();
      PeerInfo peer_info = gl_drawable.peer_info;
      ByteBuffer peer_handle = peer_info.lockAndGetHandle();
      ByteBuffer window_handle = parented?((MacOSXCanvasPeerInfo)peer_info).window_handle:this.window;

      try {
         this.window = this.nCreateWindow(x, y, mode.getWidth(), mode.getHeight(), fullscreen, isUndecorated(), resizable, parented, enableFullscreenModeAPI, this.enableHighDPI, peer_handle, window_handle);
         if(fullscreen) {
            this.skipViewportValue = true;
            current_viewport.put(2, mode.getWidth());
            current_viewport.put(3, mode.getHeight());
         }

         this.native_mode = this.nIsNativeMode(peer_handle);
         if(!this.native_mode) {
            this.robot = AWTUtil.createRobot(this.canvas);
         }
      } catch (LWJGLException var18) {
         this.destroyWindow();
         throw var18;
      } finally {
         peer_info.unlock();
      }

   }

   public void doHandleQuit() {
      synchronized(this) {
         this.close_requested = true;
      }
   }

   public void mouseInsideWindow(boolean inside) {
      synchronized(this) {
         this.mouseInsideWindow = inside;
      }

      this.updateNativeCursor = true;
   }

   public void setScaleFactor(float scale) {
      synchronized(this) {
         this.scaleFactor = scale;
      }
   }

   public native void nDestroyCALayer(ByteBuffer var1);

   public native void nDestroyWindow(ByteBuffer var1);

   public void destroyWindow() {
      if(!this.native_mode) {
         DrawableGL gl_drawable = (DrawableGL)Display.getDrawable();
         PeerInfo peer_info = gl_drawable.peer_info;
         if(peer_info != null) {
            ByteBuffer peer_handle = peer_info.getHandle();
            this.nDestroyCALayer(peer_handle);
         }

         this.robot = null;
      }

      this.nDestroyWindow(this.window);
   }

   public int getGammaRampLength() {
      return 256;
   }

   public native void setGammaRamp(FloatBuffer var1) throws LWJGLException;

   public String getAdapter() {
      return null;
   }

   public String getVersion() {
      return null;
   }

   private static boolean equals(DisplayMode mode1, DisplayMode mode2) {
      return mode1.getWidth() == mode2.getWidth() && mode1.getHeight() == mode2.getHeight() && mode1.getBitsPerPixel() == mode2.getBitsPerPixel() && mode1.getFrequency() == mode2.getFrequency();
   }

   public void switchDisplayMode(DisplayMode mode) throws LWJGLException {
      DisplayMode[] modes = this.getAvailableDisplayModes();

      for(DisplayMode available_mode : modes) {
         if(equals(available_mode, mode)) {
            this.requested_mode = available_mode;
            return;
         }
      }

      throw new LWJGLException(mode + " is not supported");
   }

   public void resetDisplayMode() {
      this.requested_mode = null;
      this.restoreGamma();
   }

   private native void restoreGamma();

   public Object createDisplayMode(int width, int height, int bitsPerPixel, int refreshRate) {
      return new DisplayMode(width, height, bitsPerPixel, refreshRate);
   }

   public DisplayMode init() throws LWJGLException {
      return (DisplayMode)this.nGetCurrentDisplayMode();
   }

   public void addDisplayMode(Object modesList, int width, int height, int bitsPerPixel, int refreshRate) {
      List<DisplayMode> modes = (List)modesList;
      DisplayMode displayMode = new DisplayMode(width, height, bitsPerPixel, refreshRate);
      modes.add(displayMode);
   }

   public DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
      List<DisplayMode> modes = new ArrayList();
      this.nGetDisplayModes(modes);
      modes.add(Display.getDesktopDisplayMode());
      return (DisplayMode[])modes.toArray(new DisplayMode[modes.size()]);
   }

   private native void nSetTitle(ByteBuffer var1, ByteBuffer var2);

   public void setTitle(String title) {
      ByteBuffer buffer = MemoryUtil.encodeUTF8(title);
      this.nSetTitle(this.window, buffer);
   }

   public boolean isCloseRequested() {
      synchronized(this) {
         boolean result = this.close_requested;
         this.close_requested = false;
         return result;
      }
   }

   public boolean isVisible() {
      return true;
   }

   public boolean isActive() {
      return this.native_mode?this.nIsFocused(this.window):Display.getParent().hasFocus();
   }

   public Canvas getCanvas() {
      return this.canvas;
   }

   public boolean isDirty() {
      return false;
   }

   public PeerInfo createPeerInfo(PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
      try {
         return new MacOSXDisplayPeerInfo(pixel_format, attribs, true);
      } catch (LWJGLException var4) {
         return new MacOSXDisplayPeerInfo(pixel_format, attribs, false);
      }
   }

   public void update() {
      boolean should_update = true;
      DrawableGL drawable = (DrawableGL)Display.getDrawable();
      if(should_update) {
         if(this.skipViewportValue) {
            this.skipViewportValue = false;
         } else {
            GL11.glGetInteger(2978, current_viewport);
         }

         drawable.context.update();
         GL11.glViewport(current_viewport.get(0), current_viewport.get(1), current_viewport.get(2), current_viewport.get(3));
      }

      if(this.native_mode && this.updateNativeCursor) {
         this.updateNativeCursor = false;

         try {
            this.setNativeCursor(Long.valueOf(this.currentNativeCursor));
         } catch (LWJGLException var4) {
            var4.printStackTrace();
         }
      }

   }

   public void reshape(int x, int y, int width, int height) {
   }

   public boolean hasWheel() {
      return AWTUtil.hasWheel();
   }

   public int getButtonCount() {
      return AWTUtil.getButtonCount();
   }

   public void createMouse() throws LWJGLException {
      if(this.native_mode) {
         this.mouse = new MacOSXNativeMouse(this, this.window);
         this.mouse.register();
      } else {
         this.mouse_queue = new MacOSXMouseEventQueue(this.canvas);
         this.mouse_queue.register();
      }

   }

   public void destroyMouse() {
      if(this.native_mode) {
         try {
            MacOSXNativeMouse.setCursor(0L);
         } catch (LWJGLException var2) {
            ;
         }

         this.grabMouse(false);
         if(this.mouse != null) {
            this.mouse.unregister();
         }

         this.mouse = null;
      } else {
         if(this.mouse_queue != null) {
            MacOSXMouseEventQueue.nGrabMouse(false);
            this.mouse_queue.unregister();
         }

         this.mouse_queue = null;
      }

   }

   public void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons_buffer) {
      if(this.native_mode) {
         this.mouse.poll(coord_buffer, buttons_buffer);
      } else {
         this.mouse_queue.poll(coord_buffer, buttons_buffer);
      }

   }

   public void readMouse(ByteBuffer buffer) {
      if(this.native_mode) {
         this.mouse.copyEvents(buffer);
      } else {
         this.mouse_queue.copyEvents(buffer);
      }

   }

   public void grabMouse(boolean grab) {
      if(this.native_mode) {
         this.mouse.setGrabbed(grab);
      } else {
         this.mouse_queue.setGrabbed(grab);
      }

   }

   public int getNativeCursorCapabilities() {
      return this.native_mode?7:AWTUtil.getNativeCursorCapabilities();
   }

   public void setCursorPosition(int x, int y) {
      if(this.native_mode && this.mouse != null) {
         this.mouse.setCursorPosition(x, y);
      }

   }

   public void setNativeCursor(Object handle) throws LWJGLException {
      if(this.native_mode) {
         this.currentNativeCursor = getCursorHandle(handle);
         if(Display.isCreated()) {
            if(this.mouseInsideWindow) {
               MacOSXNativeMouse.setCursor(this.currentNativeCursor);
            } else {
               MacOSXNativeMouse.setCursor(0L);
            }
         }
      }

   }

   public int getMinCursorSize() {
      return 1;
   }

   public int getMaxCursorSize() {
      DisplayMode dm = Display.getDesktopDisplayMode();
      return Math.min(dm.getWidth(), dm.getHeight()) / 2;
   }

   public void createKeyboard() throws LWJGLException {
      if(this.native_mode) {
         this.keyboard = new MacOSXNativeKeyboard(this.window);
         this.keyboard.register();
      } else {
         this.keyboard_queue = new KeyboardEventQueue(this.canvas);
         this.keyboard_queue.register();
      }

   }

   public void destroyKeyboard() {
      if(this.native_mode) {
         if(this.keyboard != null) {
            this.keyboard.unregister();
         }

         this.keyboard = null;
      } else {
         if(this.keyboard_queue != null) {
            this.keyboard_queue.unregister();
         }

         this.keyboard_queue = null;
      }

   }

   public void pollKeyboard(ByteBuffer keyDownBuffer) {
      if(this.native_mode) {
         this.keyboard.poll(keyDownBuffer);
      } else {
         this.keyboard_queue.poll(keyDownBuffer);
      }

   }

   public void readKeyboard(ByteBuffer buffer) {
      if(this.native_mode) {
         this.keyboard.copyEvents(buffer);
      } else {
         this.keyboard_queue.copyEvents(buffer);
      }

   }

   public Object createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
      if(this.native_mode) {
         long cursor = MacOSXNativeMouse.createCursor(width, height, xHotspot, yHotspot, numImages, images, delays);
         return Long.valueOf(cursor);
      } else {
         return AWTUtil.createCursor(width, height, xHotspot, yHotspot, numImages, images, delays);
      }
   }

   public void destroyCursor(Object cursor_handle) {
      long handle = getCursorHandle(cursor_handle);
      if(this.currentNativeCursor == handle) {
         this.currentNativeCursor = 0L;
      }

      MacOSXNativeMouse.destroyCursor(handle);
   }

   private static long getCursorHandle(Object cursor_handle) {
      return cursor_handle != null?((Long)cursor_handle).longValue():0L;
   }

   public int getPbufferCapabilities() {
      return 1;
   }

   public boolean isBufferLost(PeerInfo handle) {
      return false;
   }

   public PeerInfo createPbuffer(int width, int height, PixelFormat pixel_format, ContextAttribs attribs, IntBuffer pixelFormatCaps, IntBuffer pBufferAttribs) throws LWJGLException {
      return new MacOSXPbufferPeerInfo(width, height, pixel_format, attribs);
   }

   public void setPbufferAttrib(PeerInfo handle, int attrib, int value) {
      throw new UnsupportedOperationException();
   }

   public void bindTexImageToPbuffer(PeerInfo handle, int buffer) {
      throw new UnsupportedOperationException();
   }

   public void releaseTexImageFromPbuffer(PeerInfo handle, int buffer) {
      throw new UnsupportedOperationException();
   }

   public int setIcon(ByteBuffer[] icons) {
      return 0;
   }

   public int getX() {
      return this.nGetX(this.window);
   }

   public int getY() {
      return this.nGetY(this.window);
   }

   public int getWidth() {
      return this.nGetWidth(this.window);
   }

   public int getHeight() {
      return this.nGetHeight(this.window);
   }

   public boolean isInsideWindow() {
      return this.mouseInsideWindow;
   }

   public void setResizable(boolean resizable) {
      this.nSetResizable(this.window, resizable);
   }

   public boolean wasResized() {
      return this.nWasResized(this.window);
   }

   public float getPixelScaleFactor() {
      return this.enableHighDPI && !Display.isFullscreen()?this.scaleFactor:1.0F;
   }
}
