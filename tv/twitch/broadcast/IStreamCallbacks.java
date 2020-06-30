package tv.twitch.broadcast;

import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.UserInfo;

public interface IStreamCallbacks {
   void requestAuthTokenCallback(ErrorCode var1, AuthToken var2);

   void loginCallback(ErrorCode var1, ChannelInfo var2);

   void getIngestServersCallback(ErrorCode var1, IngestList var2);

   void getUserInfoCallback(ErrorCode var1, UserInfo var2);

   void getStreamInfoCallback(ErrorCode var1, StreamInfo var2);

   void getArchivingStateCallback(ErrorCode var1, ArchivingState var2);

   void runCommercialCallback(ErrorCode var1);

   void setStreamInfoCallback(ErrorCode var1);

   void getGameNameListCallback(ErrorCode var1, GameInfoList var2);

   void bufferUnlockCallback(long var1);

   void startCallback(ErrorCode var1);

   void stopCallback(ErrorCode var1);

   void sendActionMetaDataCallback(ErrorCode var1);

   void sendStartSpanMetaDataCallback(ErrorCode var1);

   void sendEndSpanMetaDataCallback(ErrorCode var1);
}
