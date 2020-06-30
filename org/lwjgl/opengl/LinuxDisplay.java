package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.AWTCanvasImplementation;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayImplementation;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.DrawableGLES;
import org.lwjgl.opengl.DrawableLWJGL;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GlobalLock;
import org.lwjgl.opengl.LinuxDisplayPeerInfo;
import org.lwjgl.opengl.LinuxEvent;
import org.lwjgl.opengl.LinuxKeyboard;
import org.lwjgl.opengl.LinuxMouse;
import org.lwjgl.opengl.LinuxPbufferPeerInfo;
import org.lwjgl.opengl.LinuxPeerInfo;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.XRandR;

final class LinuxDisplay implements DisplayImplementation {
   public static final int CurrentTime = 0;
   public static final int GrabSuccess = 0;
   public static final int AutoRepeatModeOff = 0;
   public static final int AutoRepeatModeOn = 1;
   public static final int AutoRepeatModeDefault = 2;
   public static final int None = 0;
   private static final int KeyPressMask = 1;
   private static final int KeyReleaseMask = 2;
   private static final int ButtonPressMask = 4;
   private static final int ButtonReleaseMask = 8;
   private static final int NotifyAncestor = 0;
   private static final int NotifyNonlinear = 3;
   private static final int NotifyPointer = 5;
   private static final int NotifyPointerRoot = 6;
   private static final int NotifyDetailNone = 7;
   private static final int SetModeInsert = 0;
   private static final int SaveSetRoot = 1;
   private static final int SaveSetUnmap = 1;
   private static final int X_SetInputFocus = 42;
   private static final int FULLSCREEN_LEGACY = 1;
   private static final int FULLSCREEN_NETWM = 2;
   private static final int WINDOWED = 3;
   private static int current_window_mode = 3;
   private static final int XRANDR = 10;
   private static final int XF86VIDMODE = 11;
   private static final int NONE = 12;
   private static long display;
   private static long current_window;
   private static long saved_error_handler;
   private static int display_connection_usage_count;
   private final LinuxEvent event_buffer = new LinuxEvent();
   private final LinuxEvent tmp_event_buffer = new LinuxEvent();
   private int current_displaymode_extension = 12;
   private long delete_atom;
   private PeerInfo peer_info;
   private ByteBuffer saved_gamma;
   private ByteBuffer current_gamma;
   private DisplayMode saved_mode;
   private DisplayMode current_mode;
   private boolean keyboard_grabbed;
   private boolean pointer_grabbed;
   private boolean input_released;
   private boolean grab;
   private boolean focused;
   private boolean minimized;
   private boolean dirty;
   private boolean close_requested;
   private long current_cursor;
   private long blank_cursor;
   private boolean mouseInside = true;
   private boolean resizable;
   private boolean resized;
   private int window_x;
   private int window_y;
   private int window_width;
   private int window_height;
   private Canvas parent;
   private long parent_window;
   private static boolean xembedded;
   private long parent_proxy_focus_window;
   private boolean parent_focused;
   private boolean parent_focus_changed;
   private long last_window_focus = 0L;
   private LinuxKeyboard keyboard;
   private LinuxMouse mouse;
   private String wm_class;
   private final FocusListener focus_listener = new FocusListener() {
      public void focusGained(FocusEvent e) {
         synchronized(GlobalLock.lock) {
            LinuxDisplay.this.parent_focused = true;
            LinuxDisplay.this.parent_focus_changed = true;
         }
      }

      public void focusLost(FocusEvent e) {
         synchronized(GlobalLock.lock) {
            LinuxDisplay.this.parent_focused = false;
            LinuxDisplay.this.parent_focus_changed = true;
         }
      }
   };

   private static ByteBuffer getCurrentGammaRamp() throws LWJGLException {
      lockAWT();

      ByteBuffer var0;
      try {
         incDisplay();

         try {
            if(!isXF86VidModeSupported()) {
               var0 = null;
               return var0;
            }

            var0 = nGetCurrentGammaRamp(getDisplay(), getDefaultScreen());
         } finally {
            decDisplay();
         }
      } finally {
         unlockAWT();
      }

      return var0;
   }

   private static native ByteBuffer nGetCurrentGammaRamp(long var0, int var2) throws LWJGLException;

   private static int getBestDisplayModeExtension() {
      int result;
      if(isXrandrSupported()) {
         LWJGLUtil.log("Using Xrandr for display mode switching");
         result = 10;
      } else if(isXF86VidModeSupported()) {
         LWJGLUtil.log("Using XF86VidMode for display mode switching");
         result = 11;
      } else {
         LWJGLUtil.log("No display mode extensions available");
         result = 12;
      }

      return result;
   }

   private static boolean isXrandrSupported() {
      if(Display.getPrivilegedBoolean("LWJGL_DISABLE_XRANDR")) {
         return false;
      } else {
         lockAWT();

         boolean var1;
         try {
            incDisplay();

            try {
               boolean e = nIsXrandrSupported(getDisplay());
               return e;
            } finally {
               decDisplay();
            }
         } catch (LWJGLException var10) {
            LWJGLUtil.log("Got exception while querying Xrandr support: " + var10);
            var1 = false;
         } finally {
            unlockAWT();
         }

         return var1;
      }
   }

   private static native boolean nIsXrandrSupported(long var0) throws LWJGLException;

   private static boolean isXF86VidModeSupported() {
      lockAWT();

      boolean var1;
      try {
         incDisplay();

         try {
            boolean e = nIsXF86VidModeSupported(getDisplay());
            return e;
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var10) {
         LWJGLUtil.log("Got exception while querying XF86VM support: " + var10);
         var1 = false;
      } finally {
         unlockAWT();
      }

      return var1;
   }

   private static native boolean nIsXF86VidModeSupported(long var0) throws LWJGLException;

   private static boolean isNetWMFullscreenSupported() throws LWJGLException {
      if(Display.getPrivilegedBoolean("LWJGL_DISABLE_NETWM")) {
         return false;
      } else {
         lockAWT();

         boolean var1;
         try {
            incDisplay();

            try {
               boolean e = nIsNetWMFullscreenSupported(getDisplay(), getDefaultScreen());
               return e;
            } finally {
               decDisplay();
            }
         } catch (LWJGLException var10) {
            LWJGLUtil.log("Got exception while querying NetWM support: " + var10);
            var1 = false;
         } finally {
            unlockAWT();
         }

         return var1;
      }
   }

   private static native boolean nIsNetWMFullscreenSupported(long var0, int var2) throws LWJGLException;

   static void lockAWT() {
      try {
         nLockAWT();
      } catch (LWJGLException var1) {
         LWJGLUtil.log("Caught exception while locking AWT: " + var1);
      }

   }

   private static native void nLockAWT() throws LWJGLException;

   static void unlockAWT() {
      try {
         nUnlockAWT();
      } catch (LWJGLException var1) {
         LWJGLUtil.log("Caught exception while unlocking AWT: " + var1);
      }

   }

   private static native void nUnlockAWT() throws LWJGLException;

   static void incDisplay() throws LWJGLException {
      if(display_connection_usage_count == 0) {
         try {
            GLContext.loadOpenGLLibrary();
            org.lwjgl.opengles.GLContext.loadOpenGLLibrary();
         } catch (Throwable var1) {
            ;
         }

         saved_error_handler = setErrorHandler();
         display = openDisplay();
      }

      ++display_connection_usage_count;
   }

   private static native int callErrorHandler(long var0, long var2, long var4);

   private static native long setErrorHandler();

   private static native long resetErrorHandler(long var0);

   private static native void synchronize(long var0, boolean var2);

   private static int globalErrorHandler(long display, long event_ptr, long error_display, long serial, long error_code, long request_code, long minor_code) throws LWJGLException {
      if(xembedded && request_code == 42L) {
         return 0;
      } else if(display == getDisplay()) {
         String error_msg = getErrorText(display, error_code);
         throw new LWJGLException("X Error - disp: 0x" + Long.toHexString(error_display) + " serial: " + serial + " error: " + error_msg + " request_code: " + request_code + " minor_code: " + minor_code);
      } else {
         return saved_error_handler != 0L?callErrorHandler(saved_error_handler, display, event_ptr):0;
      }
   }

   private static native String getErrorText(long var0, long var2);

   static void decDisplay() {
   }

   static native long openDisplay() throws LWJGLException;

   static native void closeDisplay(long var0);

   private int getWindowMode(boolean fullscreen) throws LWJGLException {
      if(fullscreen) {
         if(this.current_displaymode_extension == 10 && isNetWMFullscreenSupported()) {
            LWJGLUtil.log("Using NetWM for fullscreen window");
            return 2;
         } else {
            LWJGLUtil.log("Using legacy mode for fullscreen window");
            return 1;
         }
      } else {
         return 3;
      }
   }

   static long getDisplay() {
      if(display_connection_usage_count <= 0) {
         throw new InternalError("display_connection_usage_count = " + display_connection_usage_count);
      } else {
         return display;
      }
   }

   static int getDefaultScreen() {
      return nGetDefaultScreen(getDisplay());
   }

   static native int nGetDefaultScreen(long var0);

   static long getWindow() {
      return current_window;
   }

   private void ungrabKeyboard() {
      if(this.keyboard_grabbed) {
         nUngrabKeyboard(getDisplay());
         this.keyboard_grabbed = false;
      }

   }

   static native int nUngrabKeyboard(long var0);

   private void grabKeyboard() {
      if(!this.keyboard_grabbed) {
         int res = nGrabKeyboard(getDisplay(), getWindow());
         if(res == 0) {
            this.keyboard_grabbed = true;
         }
      }

   }

   static native int nGrabKeyboard(long var0, long var2);

   private void grabPointer() {
      if(!this.pointer_grabbed) {
         int result = nGrabPointer(getDisplay(), getWindow(), 0L);
         if(result == 0) {
            this.pointer_grabbed = true;
            if(isLegacyFullscreen()) {
               nSetViewPort(getDisplay(), getWindow(), getDefaultScreen());
            }
         }
      }

   }

   static native int nGrabPointer(long var0, long var2, long var4);

   private static native void nSetViewPort(long var0, long var2, int var4);

   private void ungrabPointer() {
      if(this.pointer_grabbed) {
         this.pointer_grabbed = false;
         nUngrabPointer(getDisplay());
      }

   }

   static native int nUngrabPointer(long var0);

   private static boolean isFullscreen() {
      return current_window_mode == 1 || current_window_mode == 2;
   }

   private boolean shouldGrab() {
      return !this.input_released && this.grab && this.mouse != null;
   }

   private void updatePointerGrab() {
      if(!isFullscreen() && !this.shouldGrab()) {
         this.ungrabPointer();
      } else {
         this.grabPointer();
      }

      this.updateCursor();
   }

   private void updateCursor() {
      long cursor;
      if(this.shouldGrab()) {
         cursor = this.blank_cursor;
      } else {
         cursor = this.current_cursor;
      }

      nDefineCursor(getDisplay(), getWindow(), cursor);
   }

   private static native void nDefineCursor(long var0, long var2, long var4);

   private static boolean isLegacyFullscreen() {
      return current_window_mode == 1;
   }

   private void updateKeyboardGrab() {
      if(isLegacyFullscreen()) {
         this.grabKeyboard();
      } else {
         this.ungrabKeyboard();
      }

   }

   public void createWindow(DrawableLWJGL drawable, DisplayMode mode, Canvas parent, int x, int y) throws LWJGLException {
      lockAWT();

      try {
         incDisplay();

         try {
            if(drawable instanceof DrawableGLES) {
               this.peer_info = new LinuxDisplayPeerInfo();
            }

            ByteBuffer handle = this.peer_info.lockAndGetHandle();

            try {
               current_window_mode = this.getWindowMode(Display.isFullscreen());
               if(current_window_mode != 3) {
                  LinuxDisplay.Compiz.setLegacyFullscreenSupport(true);
               }

               boolean undecorated = Display.getPrivilegedBoolean("org.lwjgl.opengl.Window.undecorated") || current_window_mode != 3 && Display.getPrivilegedBoolean("org.lwjgl.opengl.Window.undecorated_fs");
               this.parent = parent;
               this.parent_window = parent != null?getHandle(parent):getRootWindow(getDisplay(), getDefaultScreen());
               this.resizable = Display.isResizable();
               this.resized = false;
               this.window_x = x;
               this.window_y = y;
               this.window_width = mode.getWidth();
               this.window_height = mode.getHeight();
               if(mode.isFullscreenCapable() && this.current_displaymode_extension == 10) {
                  XRandR.Screen primaryScreen = XRandR.DisplayModetoScreen(Display.getDisplayMode());
                  x = primaryScreen.xPos;
                  y = primaryScreen.yPos;
               }

               current_window = nCreateWindow(getDisplay(), getDefaultScreen(), handle, mode, current_window_mode, x, y, undecorated, this.parent_window, this.resizable);
               this.wm_class = Display.getPrivilegedString("LWJGL_WM_CLASS");
               if(this.wm_class == null) {
                  this.wm_class = Display.getTitle();
               }

               this.setClassHint(Display.getTitle(), this.wm_class);
               mapRaised(getDisplay(), current_window);
               xembedded = parent != null && isAncestorXEmbedded(this.parent_window);
               this.blank_cursor = createBlankCursor();
               this.current_cursor = 0L;
               this.focused = false;
               this.input_released = false;
               this.pointer_grabbed = false;
               this.keyboard_grabbed = false;
               this.close_requested = false;
               this.grab = false;
               this.minimized = false;
               this.dirty = true;
               if(drawable instanceof DrawableGLES) {
                  ((DrawableGLES)drawable).initialize(current_window, getDisplay(), 4, (org.lwjgl.opengles.PixelFormat)drawable.getPixelFormat());
               }

               if(parent != null) {
                  parent.addFocusListener(this.focus_listener);
                  this.parent_focused = parent.isFocusOwner();
                  this.parent_focus_changed = true;
               }
            } finally {
               this.peer_info.unlock();
            }
         } catch (LWJGLException var18) {
            decDisplay();
            throw var18;
         }
      } finally {
         unlockAWT();
      }

   }

   private static native long nCreateWindow(long var0, int var2, ByteBuffer var3, DisplayMode var4, int var5, int var6, int var7, boolean var8, long var9, boolean var11) throws LWJGLException;

   private static native long getRootWindow(long var0, int var2);

   private static native boolean hasProperty(long var0, long var2, long var4);

   private static native long getParentWindow(long var0, long var2) throws LWJGLException;

   private static native int getChildCount(long var0, long var2) throws LWJGLException;

   private static native void mapRaised(long var0, long var2);

   private static native void reparentWindow(long var0, long var2, long var4, int var6, int var7);

   private static native long nGetInputFocus(long var0) throws LWJGLException;

   private static native void nSetInputFocus(long var0, long var2, long var4);

   private static native void nSetWindowSize(long var0, long var2, int var4, int var5, boolean var6);

   private static native int nGetX(long var0, long var2);

   private static native int nGetY(long var0, long var2);

   private static native int nGetWidth(long var0, long var2);

   private static native int nGetHeight(long var0, long var2);

   private static boolean isAncestorXEmbedded(long window) throws LWJGLException {
      long xembed_atom = internAtom("_XEMBED_INFO", true);
      if(xembed_atom != 0L) {
         for(long w = window; w != 0L; w = getParentWindow(getDisplay(), w)) {
            if(hasProperty(getDisplay(), w, xembed_atom)) {
               return true;
            }
         }
      }

      return false;
   }

   private static long getHandle(Canvas parent) throws LWJGLException {
      AWTCanvasImplementation awt_impl = AWTGLCanvas.createImplementation();
      LinuxPeerInfo parent_peer_info = (LinuxPeerInfo)awt_impl.createPeerInfo(parent, (PixelFormat)null, (ContextAttribs)null);
      ByteBuffer parent_peer_info_handle = parent_peer_info.lockAndGetHandle();

      long var4;
      try {
         var4 = parent_peer_info.getDrawable();
      } finally {
         parent_peer_info.unlock();
      }

      return var4;
   }

   private void updateInputGrab() {
      this.updatePointerGrab();
      this.updateKeyboardGrab();
   }

   public void destroyWindow() {
      lockAWT();

      try {
         if(this.parent != null) {
            this.parent.removeFocusListener(this.focus_listener);
         }

         try {
            this.setNativeCursor((Object)null);
         } catch (LWJGLException var5) {
            LWJGLUtil.log("Failed to reset cursor: " + var5.getMessage());
         }

         nDestroyCursor(getDisplay(), this.blank_cursor);
         this.blank_cursor = 0L;
         this.ungrabKeyboard();
         nDestroyWindow(getDisplay(), getWindow());
         decDisplay();
         if(current_window_mode != 3) {
            LinuxDisplay.Compiz.setLegacyFullscreenSupport(false);
         }
      } finally {
         unlockAWT();
      }

   }

   static native void nDestroyWindow(long var0, long var2);

   public void switchDisplayMode(DisplayMode mode) throws LWJGLException {
      lockAWT();

      try {
         this.switchDisplayModeOnTmpDisplay(mode);
         this.current_mode = mode;
      } finally {
         unlockAWT();
      }

   }

   private void switchDisplayModeOnTmpDisplay(DisplayMode mode) throws LWJGLException {
      if(this.current_displaymode_extension == 10) {
         XRandR.setConfiguration(false, new XRandR.Screen[]{XRandR.DisplayModetoScreen(mode)});
      } else {
         incDisplay();

         try {
            nSwitchDisplayMode(getDisplay(), getDefaultScreen(), this.current_displaymode_extension, mode);
         } finally {
            decDisplay();
         }
      }

   }

   private static native void nSwitchDisplayMode(long var0, int var2, int var3, DisplayMode var4) throws LWJGLException;

   private static long internAtom(String atom_name, boolean only_if_exists) throws LWJGLException {
      incDisplay();

      long var2;
      try {
         var2 = nInternAtom(getDisplay(), atom_name, only_if_exists);
      } finally {
         decDisplay();
      }

      return var2;
   }

   static native long nInternAtom(long var0, String var2, boolean var3);

   public void resetDisplayMode() {
      lockAWT();

      try {
         if(this.current_displaymode_extension == 10) {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  XRandR.restoreConfiguration();
                  return null;
               }
            });
         } else {
            this.switchDisplayMode(this.saved_mode);
         }

         if(isXF86VidModeSupported()) {
            this.doSetGamma(this.saved_gamma);
         }

         LinuxDisplay.Compiz.setLegacyFullscreenSupport(false);
      } catch (LWJGLException var5) {
         LWJGLUtil.log("Caught exception while resetting mode: " + var5);
      } finally {
         unlockAWT();
      }

   }

   public int getGammaRampLength() {
      if(!isXF86VidModeSupported()) {
         return 0;
      } else {
         lockAWT();

         byte var17;
         try {
            incDisplay();

            try {
               LWJGLException e;
               try {
                  e = nGetGammaRampLength(getDisplay(), getDefaultScreen());
                  return (int)e;
               } catch (LWJGLException var13) {
                  e = var13;
                  LWJGLUtil.log("Got exception while querying gamma length: " + var13);
                  var17 = 0;
               }
            } finally {
               decDisplay();
            }
         } catch (LWJGLException var15) {
            LWJGLUtil.log("Failed to get gamma ramp length: " + var15);
            var17 = 0;
            return var17;
         } finally {
            unlockAWT();
         }

         return var17;
      }
   }

   private static native int nGetGammaRampLength(long var0, int var2) throws LWJGLException;

   public void setGammaRamp(FloatBuffer gammaRamp) throws LWJGLException {
      if(!isXF86VidModeSupported()) {
         throw new LWJGLException("No gamma ramp support (Missing XF86VM extension)");
      } else {
         this.doSetGamma(convertToNativeRamp(gammaRamp));
      }
   }

   private void doSetGamma(ByteBuffer native_gamma) throws LWJGLException {
      lockAWT();

      try {
         setGammaRampOnTmpDisplay(native_gamma);
         this.current_gamma = native_gamma;
      } finally {
         unlockAWT();
      }

   }

   private static void setGammaRampOnTmpDisplay(ByteBuffer native_gamma) throws LWJGLException {
      incDisplay();

      try {
         nSetGammaRamp(getDisplay(), getDefaultScreen(), native_gamma);
      } finally {
         decDisplay();
      }

   }

   private static native void nSetGammaRamp(long var0, int var2, ByteBuffer var3) throws LWJGLException;

   private static ByteBuffer convertToNativeRamp(FloatBuffer ramp) throws LWJGLException {
      return nConvertToNativeRamp(ramp, ramp.position(), ramp.remaining());
   }

   private static native ByteBuffer nConvertToNativeRamp(FloatBuffer var0, int var1, int var2) throws LWJGLException;

   public String getAdapter() {
      return null;
   }

   public String getVersion() {
      return null;
   }

   public DisplayMode init() throws LWJGLException {
      lockAWT();

      DisplayMode var2;
      try {
         LinuxDisplay.Compiz.init();
         this.delete_atom = internAtom("WM_DELETE_WINDOW", false);
         this.current_displaymode_extension = getBestDisplayModeExtension();
         if(this.current_displaymode_extension == 12) {
            throw new LWJGLException("No display mode extension is available");
         }

         DisplayMode[] modes = this.getAvailableDisplayModes();
         if(modes == null || modes.length == 0) {
            throw new LWJGLException("No modes available");
         }

         switch(this.current_displaymode_extension) {
         case 10:
            this.saved_mode = (DisplayMode)AccessController.doPrivileged(new PrivilegedAction() {
               public DisplayMode run() {
                  XRandR.saveConfiguration();
                  return XRandR.ScreentoDisplayMode(XRandR.getConfiguration());
               }
            });
            break;
         case 11:
            this.saved_mode = modes[0];
            break;
         default:
            throw new LWJGLException("Unknown display mode extension: " + this.current_displaymode_extension);
         }

         this.current_mode = this.saved_mode;
         this.saved_gamma = getCurrentGammaRamp();
         this.current_gamma = this.saved_gamma;
         var2 = this.saved_mode;
      } finally {
         unlockAWT();
      }

      return var2;
   }

   private static DisplayMode getCurrentXRandrMode() throws LWJGLException {
      lockAWT();

      DisplayMode var0;
      try {
         incDisplay();

         try {
            var0 = nGetCurrentXRandrMode(getDisplay(), getDefaultScreen());
         } finally {
            decDisplay();
         }
      } finally {
         unlockAWT();
      }

      return var0;
   }

   private static native DisplayMode nGetCurrentXRandrMode(long var0, int var2) throws LWJGLException;

   public void setTitle(String title) {
      lockAWT();

      try {
         ByteBuffer titleText = MemoryUtil.encodeUTF8(title);
         nSetTitle(getDisplay(), getWindow(), MemoryUtil.getAddress(titleText), titleText.remaining() - 1);
      } finally {
         unlockAWT();
      }

   }

   private static native void nSetTitle(long var0, long var2, long var4, int var6);

   private void setClassHint(String wm_name, String wm_class) {
      lockAWT();

      try {
         ByteBuffer nameText = MemoryUtil.encodeUTF8(wm_name);
         ByteBuffer classText = MemoryUtil.encodeUTF8(wm_class);
         nSetClassHint(getDisplay(), getWindow(), MemoryUtil.getAddress(nameText), MemoryUtil.getAddress(classText));
      } finally {
         unlockAWT();
      }

   }

   private static native void nSetClassHint(long var0, long var2, long var4, long var6);

   public boolean isCloseRequested() {
      boolean result = this.close_requested;
      this.close_requested = false;
      return result;
   }

   public boolean isVisible() {
      return !this.minimized;
   }

   public boolean isActive() {
      return this.focused || isLegacyFullscreen();
   }

   public boolean isDirty() {
      boolean result = this.dirty;
      this.dirty = false;
      return result;
   }

   public PeerInfo createPeerInfo(PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
      this.peer_info = new LinuxDisplayPeerInfo(pixel_format);
      return this.peer_info;
   }

   private void relayEventToParent(LinuxEvent event_buffer, int event_mask) {
      this.tmp_event_buffer.copyFrom(event_buffer);
      this.tmp_event_buffer.setWindow(this.parent_window);
      this.tmp_event_buffer.sendEvent(getDisplay(), this.parent_window, true, (long)event_mask);
   }

   private void relayEventToParent(LinuxEvent event_buffer) {
      if(this.parent != null) {
         switch(event_buffer.getType()) {
         case 2:
            this.relayEventToParent(event_buffer, 1);
            break;
         case 3:
            this.relayEventToParent(event_buffer, 1);
            break;
         case 4:
            if(xembedded || !this.focused) {
               this.relayEventToParent(event_buffer, 1);
            }
            break;
         case 5:
            if(xembedded || !this.focused) {
               this.relayEventToParent(event_buffer, 1);
            }
         }

      }
   }

   private void processEvents() {
      while(LinuxEvent.getPending(getDisplay()) > 0) {
         this.event_buffer.nextEvent(getDisplay());
         long event_window = this.event_buffer.getWindow();
         this.relayEventToParent(this.event_buffer);
         if(event_window == getWindow() && !this.event_buffer.filterEvent(event_window) && (this.mouse == null || !this.mouse.filterEvent(this.grab, this.shouldWarpPointer(), this.event_buffer)) && (this.keyboard == null || !this.keyboard.filterEvent(this.event_buffer))) {
            switch(this.event_buffer.getType()) {
            case 7:
               this.mouseInside = true;
               break;
            case 8:
               this.mouseInside = false;
               break;
            case 9:
               this.setFocused(true, this.event_buffer.getFocusDetail());
               break;
            case 10:
               this.setFocused(false, this.event_buffer.getFocusDetail());
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            default:
               break;
            case 12:
               this.dirty = true;
               break;
            case 18:
               this.dirty = true;
               this.minimized = true;
               break;
            case 19:
               this.dirty = true;
               this.minimized = false;
               break;
            case 22:
               int x = nGetX(getDisplay(), getWindow());
               int y = nGetY(getDisplay(), getWindow());
               int width = nGetWidth(getDisplay(), getWindow());
               int height = nGetHeight(getDisplay(), getWindow());
               this.window_x = x;
               this.window_y = y;
               if(this.window_width != width || this.window_height != height) {
                  this.resized = true;
                  this.window_width = width;
                  this.window_height = height;
               }
               break;
            case 33:
               if(this.event_buffer.getClientFormat() == 32 && (long)this.event_buffer.getClientData(0) == this.delete_atom) {
                  this.close_requested = true;
               }
            }
         }
      }

   }

   public void update() {
      lockAWT();

      try {
         this.processEvents();
         this.checkInput();
      } finally {
         unlockAWT();
      }

   }

   public void reshape(int x, int y, int width, int height) {
      lockAWT();

      try {
         nReshape(getDisplay(), getWindow(), x, y, width, height);
      } finally {
         unlockAWT();
      }

   }

   private static native void nReshape(long var0, long var2, int var4, int var5, int var6, int var7);

   public DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
      lockAWT();

      DisplayMode[] var16;
      try {
         incDisplay();
         if(this.current_displaymode_extension != 10) {
            try {
               DisplayMode[] modes = nGetAvailableDisplayModes(getDisplay(), getDefaultScreen(), this.current_displaymode_extension);
               DisplayMode[] var15 = modes;
               return var15;
            } finally {
               decDisplay();
            }
         }

         DisplayMode[] nDisplayModes = nGetAvailableDisplayModes(getDisplay(), getDefaultScreen(), this.current_displaymode_extension);
         int bpp = 24;
         if(nDisplayModes.length > 0) {
            bpp = nDisplayModes[0].getBitsPerPixel();
         }

         XRandR.Screen[] resolutions = XRandR.getResolutions(XRandR.getScreenNames()[0]);
         DisplayMode[] modes = new DisplayMode[resolutions.length];

         for(int i = 0; i < modes.length; ++i) {
            modes[i] = new DisplayMode(resolutions[i].width, resolutions[i].height, bpp, resolutions[i].freq);
         }

         var16 = modes;
      } finally {
         unlockAWT();
      }

      return var16;
   }

   private static native DisplayMode[] nGetAvailableDisplayModes(long var0, int var2, int var3) throws LWJGLException;

   public boolean hasWheel() {
      return true;
   }

   public int getButtonCount() {
      return this.mouse.getButtonCount();
   }

   public void createMouse() throws LWJGLException {
      lockAWT();

      try {
         this.mouse = new LinuxMouse(getDisplay(), getWindow(), getWindow());
      } finally {
         unlockAWT();
      }

   }

   public void destroyMouse() {
      this.mouse = null;
      this.updateInputGrab();
   }

   public void pollMouse(IntBuffer coord_buffer, ByteBuffer buttons) {
      lockAWT();

      try {
         this.mouse.poll(this.grab, coord_buffer, buttons);
      } finally {
         unlockAWT();
      }

   }

   public void readMouse(ByteBuffer buffer) {
      lockAWT();

      try {
         this.mouse.read(buffer);
      } finally {
         unlockAWT();
      }

   }

   public void setCursorPosition(int x, int y) {
      lockAWT();

      try {
         this.mouse.setCursorPosition(x, y);
      } finally {
         unlockAWT();
      }

   }

   private void checkInput() {
      if(this.parent != null) {
         if(xembedded) {
            long current_focus_window = 0L;
            if(this.last_window_focus != current_focus_window || this.parent_focused != this.focused) {
               if(this.isParentWindowActive(current_focus_window)) {
                  if(this.parent_focused) {
                     nSetInputFocus(getDisplay(), current_window, 0L);
                     this.last_window_focus = current_window;
                     this.focused = true;
                  } else {
                     nSetInputFocus(getDisplay(), this.parent_proxy_focus_window, 0L);
                     this.last_window_focus = this.parent_proxy_focus_window;
                     this.focused = false;
                  }
               } else {
                  this.last_window_focus = current_focus_window;
                  this.focused = false;
               }
            }
         } else if(this.parent_focus_changed && this.parent_focused) {
            this.setInputFocusUnsafe(getWindow());
            this.parent_focus_changed = false;
         }

      }
   }

   private void setInputFocusUnsafe(long window) {
      try {
         nSetInputFocus(getDisplay(), window, 0L);
         nSync(getDisplay(), false);
      } catch (LWJGLException var4) {
         LWJGLUtil.log("Got exception while trying to focus: " + var4);
      }

   }

   private static native void nSync(long var0, boolean var2) throws LWJGLException;

   private boolean isParentWindowActive(long window) {
      try {
         if(window == current_window) {
            return true;
         } else if(getChildCount(getDisplay(), window) != 0) {
            return false;
         } else {
            long parent_window = getParentWindow(getDisplay(), window);
            if(parent_window == 0L) {
               return false;
            } else {
               long w = current_window;

               while(w != 0L) {
                  w = getParentWindow(getDisplay(), w);
                  if(w == parent_window) {
                     this.parent_proxy_focus_window = window;
                     return true;
                  }
               }

               return false;
            }
         }
      } catch (LWJGLException var7) {
         LWJGLUtil.log("Failed to detect if parent window is active: " + var7.getMessage());
         return true;
      }
   }

   private void setFocused(boolean got_focus, int focus_detail) {
      if(this.focused != got_focus && focus_detail != 7 && focus_detail != 5 && focus_detail != 6 && !xembedded) {
         this.focused = got_focus;
         if(this.focused) {
            this.acquireInput();
         } else {
            this.releaseInput();
         }

      }
   }

   private void releaseInput() {
      if(!isLegacyFullscreen() && !this.input_released) {
         if(this.keyboard != null) {
            this.keyboard.releaseAll();
         }

         this.input_released = true;
         this.updateInputGrab();
         if(current_window_mode == 2) {
            nIconifyWindow(getDisplay(), getWindow(), getDefaultScreen());

            try {
               if(this.current_displaymode_extension == 10) {
                  AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        XRandR.restoreConfiguration();
                        return null;
                     }
                  });
               } else {
                  this.switchDisplayModeOnTmpDisplay(this.saved_mode);
               }

               setGammaRampOnTmpDisplay(this.saved_gamma);
            } catch (LWJGLException var2) {
               LWJGLUtil.log("Failed to restore saved mode: " + var2.getMessage());
            }
         }

      }
   }

   private static native void nIconifyWindow(long var0, long var2, int var4);

   private void acquireInput() {
      if(!isLegacyFullscreen() && this.input_released) {
         this.input_released = false;
         this.updateInputGrab();
         if(current_window_mode == 2) {
            try {
               this.switchDisplayModeOnTmpDisplay(this.current_mode);
               setGammaRampOnTmpDisplay(this.current_gamma);
            } catch (LWJGLException var2) {
               LWJGLUtil.log("Failed to restore mode: " + var2.getMessage());
            }
         }

      }
   }

   public void grabMouse(boolean new_grab) {
      lockAWT();

      try {
         if(new_grab != this.grab) {
            this.grab = new_grab;
            this.updateInputGrab();
            this.mouse.changeGrabbed(this.grab, this.shouldWarpPointer());
         }
      } finally {
         unlockAWT();
      }

   }

   private boolean shouldWarpPointer() {
      return this.pointer_grabbed && this.shouldGrab();
   }

   public int getNativeCursorCapabilities() {
      lockAWT();

      int e;
      try {
         incDisplay();

         try {
            e = nGetNativeCursorCapabilities(getDisplay());
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var11) {
         throw new RuntimeException(var11);
      } finally {
         unlockAWT();
      }

      return e;
   }

   private static native int nGetNativeCursorCapabilities(long var0) throws LWJGLException;

   public void setNativeCursor(Object handle) throws LWJGLException {
      this.current_cursor = getCursorHandle(handle);
      lockAWT();

      try {
         this.updateCursor();
      } finally {
         unlockAWT();
      }

   }

   public int getMinCursorSize() {
      lockAWT();

      byte var2;
      try {
         incDisplay();

         try {
            int e = nGetMinCursorSize(getDisplay(), getWindow());
            return e;
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var11) {
         LWJGLUtil.log("Exception occurred in getMinCursorSize: " + var11);
         var2 = 0;
      } finally {
         unlockAWT();
      }

      return var2;
   }

   private static native int nGetMinCursorSize(long var0, long var2);

   public int getMaxCursorSize() {
      lockAWT();

      byte var2;
      try {
         incDisplay();

         try {
            int e = nGetMaxCursorSize(getDisplay(), getWindow());
            return e;
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var11) {
         LWJGLUtil.log("Exception occurred in getMaxCursorSize: " + var11);
         var2 = 0;
      } finally {
         unlockAWT();
      }

      return var2;
   }

   private static native int nGetMaxCursorSize(long var0, long var2);

   public void createKeyboard() throws LWJGLException {
      lockAWT();

      try {
         this.keyboard = new LinuxKeyboard(getDisplay(), getWindow());
      } finally {
         unlockAWT();
      }

   }

   public void destroyKeyboard() {
      lockAWT();

      try {
         this.keyboard.destroy(getDisplay());
         this.keyboard = null;
      } finally {
         unlockAWT();
      }

   }

   public void pollKeyboard(ByteBuffer keyDownBuffer) {
      lockAWT();

      try {
         this.keyboard.poll(keyDownBuffer);
      } finally {
         unlockAWT();
      }

   }

   public void readKeyboard(ByteBuffer buffer) {
      lockAWT();

      try {
         this.keyboard.read(buffer);
      } finally {
         unlockAWT();
      }

   }

   private static native long nCreateCursor(long var0, int var2, int var3, int var4, int var5, int var6, IntBuffer var7, int var8, IntBuffer var9, int var10) throws LWJGLException;

   private static long createBlankCursor() {
      return nCreateBlankCursor(getDisplay(), getWindow());
   }

   static native long nCreateBlankCursor(long var0, long var2);

   public Object createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
      lockAWT();

      Long var10;
      try {
         incDisplay();

         try {
            long cursor = nCreateCursor(getDisplay(), width, height, xHotspot, yHotspot, numImages, images, images.position(), delays, delays != null?delays.position():-1);
            var10 = Long.valueOf(cursor);
         } catch (LWJGLException var14) {
            decDisplay();
            throw var14;
         }
      } finally {
         unlockAWT();
      }

      return var10;
   }

   private static long getCursorHandle(Object cursor_handle) {
      return cursor_handle != null?((Long)cursor_handle).longValue():0L;
   }

   public void destroyCursor(Object cursorHandle) {
      lockAWT();

      try {
         nDestroyCursor(getDisplay(), getCursorHandle(cursorHandle));
         decDisplay();
      } finally {
         unlockAWT();
      }

   }

   static native void nDestroyCursor(long var0, long var2);

   public int getPbufferCapabilities() {
      lockAWT();

      byte var2;
      try {
         incDisplay();

         try {
            int e = nGetPbufferCapabilities(getDisplay(), getDefaultScreen());
            return e;
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var11) {
         LWJGLUtil.log("Exception occurred in getPbufferCapabilities: " + var11);
         var2 = 0;
      } finally {
         unlockAWT();
      }

      return var2;
   }

   private static native int nGetPbufferCapabilities(long var0, int var2);

   public boolean isBufferLost(PeerInfo handle) {
      return false;
   }

   public PeerInfo createPbuffer(int width, int height, PixelFormat pixel_format, ContextAttribs attribs, IntBuffer pixelFormatCaps, IntBuffer pBufferAttribs) throws LWJGLException {
      return new LinuxPbufferPeerInfo(width, height, pixel_format);
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

   private static ByteBuffer convertIcons(ByteBuffer[] icons) {
      int bufferSize = 0;

      for(ByteBuffer icon : icons) {
         int size = icon.limit() / 4;
         int dimension = (int)Math.sqrt((double)size);
         if(dimension > 0) {
            bufferSize = bufferSize + 8;
            bufferSize = bufferSize + dimension * dimension * 4;
         }
      }

      if(bufferSize == 0) {
         return null;
      } else {
         ByteBuffer icon_argb = BufferUtils.createByteBuffer(bufferSize);
         icon_argb.order(ByteOrder.BIG_ENDIAN);

         for(ByteBuffer icon : icons) {
            int size = icon.limit() / 4;
            int dimension = (int)Math.sqrt((double)size);
            icon_argb.putInt(dimension);
            icon_argb.putInt(dimension);

            for(int y = 0; y < dimension; ++y) {
               for(int x = 0; x < dimension; ++x) {
                  byte r = icon.get(x * 4 + y * dimension * 4);
                  byte g = icon.get(x * 4 + y * dimension * 4 + 1);
                  byte b = icon.get(x * 4 + y * dimension * 4 + 2);
                  byte a = icon.get(x * 4 + y * dimension * 4 + 3);
                  icon_argb.put(a);
                  icon_argb.put(r);
                  icon_argb.put(g);
                  icon_argb.put(b);
               }
            }
         }

         return icon_argb;
      }
   }

   public int setIcon(ByteBuffer[] icons) {
      lockAWT();

      int var15;
      try {
         incDisplay();

         try {
            ByteBuffer icons_data = convertIcons(icons);
            if(icons_data != null) {
               nSetWindowIcon(getDisplay(), getWindow(), icons_data, icons_data.capacity());
               var15 = icons.length;
               return var15;
            }

            var15 = 0;
         } finally {
            decDisplay();
         }
      } catch (LWJGLException var13) {
         LWJGLUtil.log("Failed to set display icon: " + var13);
         var15 = 0;
         return var15;
      } finally {
         unlockAWT();
      }

      return var15;
   }

   private static native void nSetWindowIcon(long var0, long var2, ByteBuffer var4, int var5);

   public int getX() {
      return this.window_x;
   }

   public int getY() {
      return this.window_y;
   }

   public int getWidth() {
      return this.window_width;
   }

   public int getHeight() {
      return this.window_height;
   }

   public boolean isInsideWindow() {
      return this.mouseInside;
   }

   public void setResizable(boolean resizable) {
      if(this.resizable != resizable) {
         this.resizable = resizable;
         nSetWindowSize(getDisplay(), getWindow(), this.window_width, this.window_height, resizable);
      }
   }

   public boolean wasResized() {
      if(this.resized) {
         this.resized = false;
         return true;
      } else {
         return false;
      }
   }

   public float getPixelScaleFactor() {
      return 1.0F;
   }

   private static final class Compiz {
      private static boolean applyFix;
      private static LinuxDisplay.Compiz.Provider provider;

      static void init() {
         if(!Display.getPrivilegedBoolean("org.lwjgl.opengl.Window.nocompiz_lfs")) {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     if(!LinuxDisplay.Compiz.isProcessActive("compiz")) {
                        Object e = null;
                        return null;
                     } else {
                        LinuxDisplay.Compiz.provider = null;
                        String providerName = null;
                        if(LinuxDisplay.Compiz.isProcessActive("dbus-daemon")) {
                           providerName = "Dbus";
                           LinuxDisplay.Compiz.provider = new LinuxDisplay.Compiz.Provider() {
                              private static final String KEY = "/org/freedesktop/compiz/workarounds/allscreens/legacy_fullscreen";

                              public boolean hasLegacyFullscreenSupport() throws LWJGLException {
                                 List output = LinuxDisplay.Compiz.run(new String[]{"dbus-send", "--print-reply", "--type=method_call", "--dest=org.freedesktop.compiz", "/org/freedesktop/compiz/workarounds/allscreens/legacy_fullscreen", "org.freedesktop.compiz.get"});
                                 if(output != null && output.size() >= 2) {
                                    String line = (String)output.get(0);
                                    if(!line.startsWith("method return")) {
                                       throw new LWJGLException("Invalid Dbus reply.");
                                    } else {
                                       line = ((String)output.get(1)).trim();
                                       if(line.startsWith("boolean") && line.length() >= 12) {
                                          return "true".equalsIgnoreCase(line.substring("boolean".length() + 1));
                                       } else {
                                          throw new LWJGLException("Invalid Dbus reply.");
                                       }
                                    }
                                 } else {
                                    throw new LWJGLException("Invalid Dbus reply.");
                                 }
                              }

                              public void setLegacyFullscreenSupport(boolean state) throws LWJGLException {
                                 if(LinuxDisplay.Compiz.run(new String[]{"dbus-send", "--type=method_call", "--dest=org.freedesktop.compiz", "/org/freedesktop/compiz/workarounds/allscreens/legacy_fullscreen", "org.freedesktop.compiz.set", "boolean:" + Boolean.toString(state)}) == null) {
                                    throw new LWJGLException("Failed to apply Compiz LFS workaround.");
                                 }
                              }
                           };
                        } else {
                           try {
                              Runtime.getRuntime().exec("gconftool");
                              providerName = "gconftool";
                              LinuxDisplay.Compiz.provider = new LinuxDisplay.Compiz.Provider() {
                                 private static final String KEY = "/apps/compiz/plugins/workarounds/allscreens/options/legacy_fullscreen";

                                 public boolean hasLegacyFullscreenSupport() throws LWJGLException {
                                    List output = LinuxDisplay.Compiz.run(new String[]{"gconftool", "-g", "/apps/compiz/plugins/workarounds/allscreens/options/legacy_fullscreen"});
                                    if(output != null && output.size() != 0) {
                                       return Boolean.parseBoolean(((String)output.get(0)).trim());
                                    } else {
                                       throw new LWJGLException("Invalid gconftool reply.");
                                    }
                                 }

                                 public void setLegacyFullscreenSupport(boolean state) throws LWJGLException {
                                    if(LinuxDisplay.Compiz.run(new String[]{"gconftool", "-s", "/apps/compiz/plugins/workarounds/allscreens/options/legacy_fullscreen", "-s", Boolean.toString(state), "-t", "bool"}) == null) {
                                       throw new LWJGLException("Failed to apply Compiz LFS workaround.");
                                    } else {
                                       if(state) {
                                          try {
                                             Thread.sleep(200L);
                                          } catch (InterruptedException var3) {
                                             var3.printStackTrace();
                                          }
                                       }

                                    }
                                 }
                              };
                           } catch (IOException var7) {
                              ;
                           }
                        }

                        if(LinuxDisplay.Compiz.provider != null && !LinuxDisplay.Compiz.provider.hasLegacyFullscreenSupport()) {
                           LinuxDisplay.Compiz.applyFix = true;
                           LWJGLUtil.log("Using " + providerName + " to apply Compiz LFS workaround.");
                        }

                        return null;
                     }
                  } catch (LWJGLException var8) {
                     return null;
                  } finally {
                     ;
                  }
               }
            });
         }
      }

      static void setLegacyFullscreenSupport(final boolean enabled) {
         if(applyFix) {
            AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     LinuxDisplay.Compiz.provider.setLegacyFullscreenSupport(enabled);
                  } catch (LWJGLException var2) {
                     LWJGLUtil.log("Failed to change Compiz Legacy Fullscreen Support. Reason: " + var2.getMessage());
                  }

                  return null;
               }
            });
         }
      }

      private static List run(String... command) throws LWJGLException {
         List<String> output = new ArrayList();

         try {
            Process p = Runtime.getRuntime().exec(command);

            try {
               int exitValue = p.waitFor();
               if(exitValue != 0) {
                  return null;
               }
            } catch (InterruptedException var5) {
               throw new LWJGLException("Process interrupted.", var5);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while((line = br.readLine()) != null) {
               output.add(line);
            }

            br.close();
            return output;
         } catch (IOException var6) {
            throw new LWJGLException("Process failed.", var6);
         }
      }

      private static boolean isProcessActive(String processName) throws LWJGLException {
         List<String> output = run(new String[]{"ps", "-C", processName});
         if(output == null) {
            return false;
         } else {
            for(String line : output) {
               if(line.contains(processName)) {
                  return true;
               }
            }

            return false;
         }
      }

      private interface Provider {
         boolean hasLegacyFullscreenSupport() throws LWJGLException;

         void setLegacyFullscreenSupport(boolean var1) throws LWJGLException;
      }
   }
}
