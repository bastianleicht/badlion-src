package org.lwjgl.opengl;

import org.lwjgl.PointerWrapperAbstract;

public final class KHRDebugCallback extends PointerWrapperAbstract {
   private static final int GL_DEBUG_SEVERITY_HIGH = 37190;
   private static final int GL_DEBUG_SEVERITY_MEDIUM = 37191;
   private static final int GL_DEBUG_SEVERITY_LOW = 37192;
   private static final int GL_DEBUG_SEVERITY_NOTIFICATION = 33387;
   private static final int GL_DEBUG_SOURCE_API = 33350;
   private static final int GL_DEBUG_SOURCE_WINDOW_SYSTEM = 33351;
   private static final int GL_DEBUG_SOURCE_SHADER_COMPILER = 33352;
   private static final int GL_DEBUG_SOURCE_THIRD_PARTY = 33353;
   private static final int GL_DEBUG_SOURCE_APPLICATION = 33354;
   private static final int GL_DEBUG_SOURCE_OTHER = 33355;
   private static final int GL_DEBUG_TYPE_ERROR = 33356;
   private static final int GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR = 33357;
   private static final int GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR = 33358;
   private static final int GL_DEBUG_TYPE_PORTABILITY = 33359;
   private static final int GL_DEBUG_TYPE_PERFORMANCE = 33360;
   private static final int GL_DEBUG_TYPE_OTHER = 33361;
   private static final int GL_DEBUG_TYPE_MARKER = 33384;
   private static final long CALLBACK_POINTER;
   private final KHRDebugCallback.Handler handler;

   public KHRDebugCallback() {
      this(new KHRDebugCallback.Handler() {
         public void handleMessage(int source, int type, int id, int severity, String message) {
            System.err.println("[LWJGL] KHR_debug message");
            System.err.println("\tID: " + id);
            String description;
            switch(source) {
            case 33350:
               description = "API";
               break;
            case 33351:
               description = "WINDOW SYSTEM";
               break;
            case 33352:
               description = "SHADER COMPILER";
               break;
            case 33353:
               description = "THIRD PARTY";
               break;
            case 33354:
               description = "APPLICATION";
               break;
            case 33355:
               description = "OTHER";
               break;
            default:
               description = this.printUnknownToken(source);
            }

            System.err.println("\tSource: " + description);
            switch(type) {
            case 33356:
               description = "ERROR";
               break;
            case 33357:
               description = "DEPRECATED BEHAVIOR";
               break;
            case 33358:
               description = "UNDEFINED BEHAVIOR";
               break;
            case 33359:
               description = "PORTABILITY";
               break;
            case 33360:
               description = "PERFORMANCE";
               break;
            case 33361:
               description = "OTHER";
               break;
            case 33384:
               description = "MARKER";
               break;
            default:
               description = this.printUnknownToken(type);
            }

            System.err.println("\tType: " + description);
            switch(severity) {
            case 33387:
               description = "NOTIFICATION";
               break;
            case 37190:
               description = "HIGH";
               break;
            case 37191:
               description = "MEDIUM";
               break;
            case 37192:
               description = "LOW";
               break;
            default:
               description = this.printUnknownToken(severity);
            }

            System.err.println("\tSeverity: " + description);
            System.err.println("\tMessage: " + message);
         }

         private String printUnknownToken(int token) {
            return "Unknown (0x" + Integer.toHexString(token).toUpperCase() + ")";
         }
      });
   }

   public KHRDebugCallback(KHRDebugCallback.Handler handler) {
      super(CALLBACK_POINTER);
      this.handler = handler;
   }

   KHRDebugCallback.Handler getHandler() {
      return this.handler;
   }

   static {
      long pointer = 0L;

      try {
         pointer = ((Long)Class.forName("org.lwjgl.opengl.CallbackUtil").getDeclaredMethod("getDebugCallbackKHR", new Class[0]).invoke((Object)null, new Object[0])).longValue();
      } catch (Exception var3) {
         ;
      }

      CALLBACK_POINTER = pointer;
   }

   public interface Handler {
      void handleMessage(int var1, int var2, int var3, int var4, String var5);
   }
}
