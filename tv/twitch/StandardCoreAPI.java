package tv.twitch;

import tv.twitch.CoreAPI;
import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;

public class StandardCoreAPI extends CoreAPI {
   public StandardCoreAPI() {
      try {
         System.loadLibrary("twitchsdk");
      } catch (UnsatisfiedLinkError var2) {
         System.out.println("If on Windows, make sure to provide all of the necessary dll\'s as specified in the twitchsdk README. Also, make sure to set the PATH environment variable to point to the directory containing the dll\'s.");
         throw var2;
      }
   }

   protected void finalize() {
   }

   private static native ErrorCode TTV_Java_Init(String var0, String var1);

   private static native ErrorCode TTV_Java_Shutdown();

   private static native ErrorCode TTV_Java_SetTraceLevel(int var0);

   private static native ErrorCode TTV_Java_SetTraceOutput(String var0);

   private static native String TTV_Java_ErrorToString(ErrorCode var0);

   public ErrorCode init(String var1, String var2) {
      return TTV_Java_Init(var1, var2);
   }

   public ErrorCode shutdown() {
      return TTV_Java_Shutdown();
   }

   public ErrorCode setTraceLevel(MessageLevel var1) {
      if(var1 == null) {
         var1 = MessageLevel.TTV_ML_NONE;
      }

      return TTV_Java_SetTraceLevel(var1.getValue());
   }

   public ErrorCode setTraceOutput(String var1) {
      return TTV_Java_SetTraceOutput(var1);
   }

   public String errorToString(ErrorCode var1) {
      return var1 == null?null:TTV_Java_ErrorToString(var1);
   }
}
