package tv.twitch;

import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;

public abstract class CoreAPI {
   public abstract ErrorCode init(String var1, String var2);

   public abstract ErrorCode shutdown();

   public abstract ErrorCode setTraceLevel(MessageLevel var1);

   public abstract ErrorCode setTraceOutput(String var1);

   public abstract String errorToString(ErrorCode var1);
}
