package tv.twitch.broadcast;

import java.util.HashSet;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.AuthParams;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.VideoParams;

public abstract class StreamAPI {
   public abstract void setStreamCallbacks(IStreamCallbacks var1);

   public abstract IStreamCallbacks getStreamCallbacks();

   public abstract void setStatCallbacks(IStatCallbacks var1);

   public abstract IStatCallbacks getStatCallbacks();

   public abstract ErrorCode requestAuthToken(AuthParams var1, HashSet var2);

   public abstract ErrorCode login(AuthToken var1);

   public abstract ErrorCode getIngestServers(AuthToken var1);

   public abstract ErrorCode getUserInfo(AuthToken var1);

   public abstract ErrorCode getStreamInfo(AuthToken var1, String var2);

   public abstract ErrorCode setStreamInfo(AuthToken var1, String var2, StreamInfoForSetting var3);

   public abstract ErrorCode getArchivingState(AuthToken var1);

   public abstract ErrorCode runCommercial(AuthToken var1);

   public abstract ErrorCode setVolume(AudioDeviceType var1, float var2);

   public abstract float getVolume(AudioDeviceType var1);

   public abstract ErrorCode getGameNameList(String var1);

   public abstract ErrorCode getDefaultParams(VideoParams var1);

   public abstract int[] getMaxResolution(int var1, int var2, float var3, float var4);

   public abstract ErrorCode pollTasks();

   public abstract ErrorCode pollStats();

   public abstract ErrorCode sendActionMetaData(AuthToken var1, String var2, long var3, String var5, String var6);

   public abstract long sendStartSpanMetaData(AuthToken var1, String var2, long var3, String var5, String var6);

   public abstract ErrorCode sendEndSpanMetaData(AuthToken var1, String var2, long var3, long var5, String var7, String var8);

   public abstract ErrorCode submitVideoFrame(long var1);

   public abstract ErrorCode start(VideoParams var1, AudioParams var2, IngestServer var3, int var4, boolean var5);

   public abstract ErrorCode stop(boolean var1);

   public abstract ErrorCode pauseVideo();

   public abstract long allocateFrameBuffer(int var1);

   public abstract ErrorCode freeFrameBuffer(long var1);

   public abstract ErrorCode memsetFrameBuffer(long var1, int var3, int var4);

   public abstract ErrorCode randomizeFrameBuffer(long var1, int var3);

   public abstract ErrorCode captureFrameBuffer_ReadPixels(long var1);

   public abstract long getStreamTime();
}
