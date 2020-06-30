package tv.twitch.broadcast;

import java.util.HashSet;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.AuthFlag;
import tv.twitch.broadcast.AuthParams;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamAPI;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.VideoParams;

public class DesktopStreamAPI extends StreamAPI {
   protected void finalize() {
      TTV_Java_SetStreamCallbacks((IStreamCallbacks)null);
      TTV_Java_SetStatCallbacks((IStatCallbacks)null);
   }

   private static native void TTV_Java_SetStreamCallbacks(IStreamCallbacks var0);

   private static native IStreamCallbacks TTV_Java_GetStreamCallbacks();

   private static native void TTV_Java_SetStatCallbacks(IStatCallbacks var0);

   private static native IStatCallbacks TTV_Java_GetStatCallbacks();

   private static native ErrorCode TTV_Java_RequestAuthToken(AuthParams var0, int var1);

   private static native ErrorCode TTV_Java_Login(AuthToken var0);

   private static native ErrorCode TTV_Java_GetIngestServers(AuthToken var0);

   private static native ErrorCode TTV_Java_GetUserInfo(AuthToken var0);

   private static native ErrorCode TTV_Java_GetStreamInfo(AuthToken var0, String var1);

   private static native ErrorCode TTV_Java_SetStreamInfo(AuthToken var0, String var1, StreamInfoForSetting var2);

   private static native ErrorCode TTV_Java_GetArchivingState(AuthToken var0);

   private static native ErrorCode TTV_Java_RunCommercial(AuthToken var0);

   private static native ErrorCode TTV_Java_SetVolume(AudioDeviceType var0, float var1);

   private static native float TTV_Java_GetVolume(AudioDeviceType var0);

   private static native ErrorCode TTV_Java_GetGameNameList(String var0);

   private static native ErrorCode TTV_Java_GetDefaultParams(VideoParams var0);

   private static native ErrorCode TTV_GetMaxResolution(int var0, int var1, float var2, float var3, int[] var4);

   private static native ErrorCode TTV_Java_PollTasks();

   private static native ErrorCode TTV_Java_PollStats();

   private static native ErrorCode TTV_Java_Init(int var0);

   private static native ErrorCode TTV_Java_Shutdown();

   private static native ErrorCode TTV_Java_SendActionMetaData(AuthToken var0, String var1, long var2, String var4, String var5);

   private static native long TTV_Java_SendStartSpanMetaData(AuthToken var0, String var1, long var2, String var4, String var5);

   private static native ErrorCode TTV_Java_SendEndSpanMetaData(AuthToken var0, String var1, long var2, long var4, String var6, String var7);

   private static native ErrorCode TTV_Java_SubmitVideoFrame(long var0);

   private static native ErrorCode TTV_Java_Start(VideoParams var0, AudioParams var1, IngestServer var2, int var3, boolean var4);

   private static native ErrorCode TTV_Java_Stop(boolean var0);

   private static native ErrorCode TTV_Java_PauseVideo();

   private static native long TTV_Java_AllocateFrameBuffer(int var0);

   private static native ErrorCode TTV_Java_FreeFrameBuffer(long var0);

   private static native ErrorCode TTV_Java_MemsetFrameBuffer(long var0, int var2, int var3);

   private static native ErrorCode TTV_Java_RandomizeFrameBuffer(long var0, int var2);

   private static native ErrorCode TTV_Java_CaptureFrameBuffer_ReadPixels(long var0);

   private static native long TTV_Java_GetStreamTime();

   public void setStreamCallbacks(IStreamCallbacks var1) {
      TTV_Java_SetStreamCallbacks(var1);
   }

   public IStreamCallbacks getStreamCallbacks() {
      return TTV_Java_GetStreamCallbacks();
   }

   public void setStatCallbacks(IStatCallbacks var1) {
      TTV_Java_SetStatCallbacks(var1);
   }

   public IStatCallbacks getStatCallbacks() {
      return TTV_Java_GetStatCallbacks();
   }

   public ErrorCode requestAuthToken(AuthParams var1, HashSet var2) {
      if(var2 == null) {
         var2 = new HashSet();
      }

      int var3 = AuthFlag.getNativeValue(var2);
      return TTV_Java_RequestAuthToken(var1, var3);
   }

   public ErrorCode login(AuthToken var1) {
      return TTV_Java_Login(var1);
   }

   public ErrorCode getIngestServers(AuthToken var1) {
      return TTV_Java_GetIngestServers(var1);
   }

   public ErrorCode getUserInfo(AuthToken var1) {
      return TTV_Java_GetUserInfo(var1);
   }

   public ErrorCode getStreamInfo(AuthToken var1, String var2) {
      return TTV_Java_GetStreamInfo(var1, var2);
   }

   public ErrorCode setStreamInfo(AuthToken var1, String var2, StreamInfoForSetting var3) {
      return TTV_Java_SetStreamInfo(var1, var2, var3);
   }

   public ErrorCode getArchivingState(AuthToken var1) {
      return TTV_Java_GetArchivingState(var1);
   }

   public ErrorCode runCommercial(AuthToken var1) {
      return TTV_Java_RunCommercial(var1);
   }

   public ErrorCode setVolume(AudioDeviceType var1, float var2) {
      return TTV_Java_SetVolume(var1, var2);
   }

   public float getVolume(AudioDeviceType var1) {
      return TTV_Java_GetVolume(var1);
   }

   public ErrorCode getGameNameList(String var1) {
      return TTV_Java_GetGameNameList(var1);
   }

   public ErrorCode getDefaultParams(VideoParams var1) {
      return TTV_Java_GetDefaultParams(var1);
   }

   public int[] getMaxResolution(int var1, int var2, float var3, float var4) {
      int[] var5 = new int[]{0, 0};
      TTV_GetMaxResolution(var1, var2, var3, var4, var5);
      return var5;
   }

   public ErrorCode pollTasks() {
      return TTV_Java_PollTasks();
   }

   public ErrorCode pollStats() {
      return TTV_Java_PollStats();
   }

   public ErrorCode sendActionMetaData(AuthToken var1, String var2, long var3, String var5, String var6) {
      return TTV_Java_SendActionMetaData(var1, var2, var3, var5, var6);
   }

   public long sendStartSpanMetaData(AuthToken var1, String var2, long var3, String var5, String var6) {
      return TTV_Java_SendStartSpanMetaData(var1, var2, var3, var5, var6);
   }

   public ErrorCode sendEndSpanMetaData(AuthToken var1, String var2, long var3, long var5, String var7, String var8) {
      return TTV_Java_SendEndSpanMetaData(var1, var2, var3, var5, var7, var8);
   }

   public ErrorCode submitVideoFrame(long var1) {
      return TTV_Java_SubmitVideoFrame(var1);
   }

   public ErrorCode start(VideoParams var1, AudioParams var2, IngestServer var3, int var4, boolean var5) {
      return TTV_Java_Start(var1, var2, var3, var4, var5);
   }

   public ErrorCode stop(boolean var1) {
      return TTV_Java_Stop(var1);
   }

   public ErrorCode pauseVideo() {
      return TTV_Java_PauseVideo();
   }

   public long allocateFrameBuffer(int var1) {
      return TTV_Java_AllocateFrameBuffer(var1);
   }

   public ErrorCode freeFrameBuffer(long var1) {
      return TTV_Java_FreeFrameBuffer(var1);
   }

   public ErrorCode memsetFrameBuffer(long var1, int var3, int var4) {
      return TTV_Java_MemsetFrameBuffer(var1, var3, var4);
   }

   public ErrorCode randomizeFrameBuffer(long var1, int var3) {
      return TTV_Java_RandomizeFrameBuffer(var1, var3);
   }

   public ErrorCode captureFrameBuffer_ReadPixels(long var1) {
      return TTV_Java_CaptureFrameBuffer_ReadPixels(var1);
   }

   public long getStreamTime() {
      return TTV_Java_GetStreamTime();
   }
}
