package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;

abstract class WindowsPeerInfo extends PeerInfo {
   protected WindowsPeerInfo() {
      super(createHandle());
   }

   private static native ByteBuffer createHandle();

   protected static int choosePixelFormat(long hdc, int origin_x, int origin_y, PixelFormat pixel_format, IntBuffer pixel_format_caps, boolean use_hdc_bpp, boolean support_window, boolean support_pbuffer, boolean double_buffered) throws LWJGLException {
      return nChoosePixelFormat(hdc, origin_x, origin_y, pixel_format, pixel_format_caps, use_hdc_bpp, support_window, support_pbuffer, double_buffered);
   }

   private static native int nChoosePixelFormat(long var0, int var2, int var3, PixelFormat var4, IntBuffer var5, boolean var6, boolean var7, boolean var8, boolean var9) throws LWJGLException;

   protected static native void setPixelFormat(long var0, int var2) throws LWJGLException;

   public final long getHdc() {
      return nGetHdc(this.getHandle());
   }

   private static native long nGetHdc(ByteBuffer var0);

   public final long getHwnd() {
      return nGetHwnd(this.getHandle());
   }

   private static native long nGetHwnd(ByteBuffer var0);
}
