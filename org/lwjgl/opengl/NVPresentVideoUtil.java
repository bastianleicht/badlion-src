package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.ContextGL;
import org.lwjgl.opengl.GLContext;

public final class NVPresentVideoUtil {
   private static void checkExtension() {
      if(LWJGLUtil.CHECKS && !GLContext.getCapabilities().GL_NV_present_video) {
         throw new IllegalStateException("NV_present_video is not supported");
      }
   }

   private static ByteBuffer getPeerInfo() {
      return ContextGL.getCurrentContext().getPeerInfo().getHandle();
   }

   public static int glEnumerateVideoDevicesNV(LongBuffer devices) {
      checkExtension();
      if(devices != null) {
         BufferChecks.checkBuffer((LongBuffer)devices, 1);
      }

      return nglEnumerateVideoDevicesNV(getPeerInfo(), devices, devices == null?0:devices.position());
   }

   private static native int nglEnumerateVideoDevicesNV(ByteBuffer var0, LongBuffer var1, int var2);

   public static boolean glBindVideoDeviceNV(int video_slot, long video_device, IntBuffer attrib_list) {
      checkExtension();
      if(attrib_list != null) {
         BufferChecks.checkNullTerminated(attrib_list);
      }

      return nglBindVideoDeviceNV(getPeerInfo(), video_slot, video_device, attrib_list, attrib_list == null?0:attrib_list.position());
   }

   private static native boolean nglBindVideoDeviceNV(ByteBuffer var0, int var1, long var2, IntBuffer var4, int var5);

   public static boolean glQueryContextNV(int attrib, IntBuffer value) {
      checkExtension();
      BufferChecks.checkBuffer((IntBuffer)value, 1);
      ContextGL ctx = ContextGL.getCurrentContext();
      return nglQueryContextNV(ctx.getPeerInfo().getHandle(), ctx.getHandle(), attrib, value, value.position());
   }

   private static native boolean nglQueryContextNV(ByteBuffer var0, ByteBuffer var1, int var2, IntBuffer var3, int var4);
}
