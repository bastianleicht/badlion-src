package tv.twitch.chat;

import tv.twitch.ErrorCode;

public interface IChatAPIListener {
   void chatInitializationCallback(ErrorCode var1);

   void chatShutdownCallback(ErrorCode var1);

   void chatEmoticonDataDownloadCallback(ErrorCode var1);
}
