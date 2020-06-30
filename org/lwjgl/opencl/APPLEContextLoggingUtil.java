package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CallbackUtil;

public final class APPLEContextLoggingUtil {
   public static final CLContextCallback SYSTEM_LOG_CALLBACK;
   public static final CLContextCallback STD_OUT_CALLBACK;
   public static final CLContextCallback STD_ERR_CALLBACK;

   static {
      if(CLCapabilities.CL_APPLE_ContextLoggingFunctions) {
         SYSTEM_LOG_CALLBACK = new CLContextCallback(CallbackUtil.getLogMessageToSystemLogAPPLE()) {
            protected void handleMessage(String errinfo, ByteBuffer private_info) {
               throw new UnsupportedOperationException();
            }
         };
         STD_OUT_CALLBACK = new CLContextCallback(CallbackUtil.getLogMessageToStdoutAPPLE()) {
            protected void handleMessage(String errinfo, ByteBuffer private_info) {
               throw new UnsupportedOperationException();
            }
         };
         STD_ERR_CALLBACK = new CLContextCallback(CallbackUtil.getLogMessageToStderrAPPLE()) {
            protected void handleMessage(String errinfo, ByteBuffer private_info) {
               throw new UnsupportedOperationException();
            }
         };
      } else {
         SYSTEM_LOG_CALLBACK = null;
         STD_OUT_CALLBACK = null;
         STD_ERR_CALLBACK = null;
      }

   }
}
