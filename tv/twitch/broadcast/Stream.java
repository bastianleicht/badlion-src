package tv.twitch.broadcast;

import java.util.HashSet;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.AuthParams;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StreamAPI;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.VideoParams;

public class Stream {
   static Stream s_Instance = null;
   StreamAPI m_StreamAPI = null;

   public static Stream getInstance() {
      return s_Instance;
   }

   public Stream(StreamAPI var1) {
      this.m_StreamAPI = var1;
      if(s_Instance == null) {
         s_Instance = this;
      }

   }

   protected void finalize() {
      if(s_Instance == this) {
         s_Instance = null;
      }

   }

   public IStreamCallbacks getStreamCallbacks() {
      return this.m_StreamAPI.getStreamCallbacks();
   }

   public void setStreamCallbacks(IStreamCallbacks var1) {
      this.m_StreamAPI.setStreamCallbacks(var1);
   }

   public IStatCallbacks getStatCallbacks() {
      return this.m_StreamAPI.getStatCallbacks();
   }

   public void setStatCallbacks(IStatCallbacks var1) {
      this.m_StreamAPI.setStatCallbacks(var1);
   }

   public FrameBuffer allocateFrameBuffer(int var1) {
      return new FrameBuffer(this.m_StreamAPI, var1);
   }

   public ErrorCode memsetFrameBuffer(FrameBuffer var1, int var2) {
      return this.m_StreamAPI.memsetFrameBuffer(var1.getAddress(), var1.getSize(), var2);
   }

   public ErrorCode randomizeFrameBuffer(FrameBuffer var1) {
      return this.m_StreamAPI.randomizeFrameBuffer(var1.getAddress(), var1.getSize());
   }

   public ErrorCode requestAuthToken(AuthParams var1, HashSet var2) {
      ErrorCode var3 = this.m_StreamAPI.requestAuthToken(var1, var2);
      return var3;
   }

   public ErrorCode login(AuthToken var1) {
      ErrorCode var2 = this.m_StreamAPI.login(var1);
      return var2;
   }

   public ErrorCode getIngestServers(AuthToken var1) {
      return this.m_StreamAPI.getIngestServers(var1);
   }

   public ErrorCode getUserInfo(AuthToken var1) {
      return this.m_StreamAPI.getUserInfo(var1);
   }

   public ErrorCode getStreamInfo(AuthToken var1, String var2) {
      return this.m_StreamAPI.getStreamInfo(var1, var2);
   }

   public ErrorCode setStreamInfo(AuthToken var1, String var2, StreamInfoForSetting var3) {
      return this.m_StreamAPI.setStreamInfo(var1, var2, var3);
   }

   public ErrorCode getArchivingState(AuthToken var1) {
      return this.m_StreamAPI.getArchivingState(var1);
   }

   public ErrorCode runCommercial(AuthToken var1) {
      return this.m_StreamAPI.runCommercial(var1);
   }

   public ErrorCode setVolume(AudioDeviceType var1, float var2) {
      return this.m_StreamAPI.setVolume(var1, var2);
   }

   public float getVolume(AudioDeviceType var1) {
      return this.m_StreamAPI.getVolume(var1);
   }

   public ErrorCode getGameNameList(String var1) {
      return this.m_StreamAPI.getGameNameList(var1);
   }

   public ErrorCode getDefaultParams(VideoParams var1) {
      return this.m_StreamAPI.getDefaultParams(var1);
   }

   public int[] getMaxResolution(int var1, int var2, float var3, float var4) {
      return this.m_StreamAPI.getMaxResolution(var1, var2, var3, var4);
   }

   public ErrorCode pollTasks() {
      ErrorCode var1 = this.m_StreamAPI.pollTasks();
      return var1;
   }

   public ErrorCode pollStats() {
      ErrorCode var1 = this.m_StreamAPI.pollStats();
      return var1;
   }

   public ErrorCode sendActionMetaData(AuthToken var1, String var2, long var3, String var5, String var6) {
      ErrorCode var7 = this.m_StreamAPI.sendActionMetaData(var1, var2, var3, var5, var6);
      return var7;
   }

   public long sendStartSpanMetaData(AuthToken var1, String var2, long var3, String var5, String var6) {
      long var7 = this.m_StreamAPI.sendStartSpanMetaData(var1, var2, var3, var5, var6);
      return var7;
   }

   public ErrorCode sendEndSpanMetaData(AuthToken var1, String var2, long var3, long var5, String var7, String var8) {
      ErrorCode var9 = this.m_StreamAPI.sendEndSpanMetaData(var1, var2, var3, var5, var7, var8);
      return var9;
   }

   public ErrorCode submitVideoFrame(FrameBuffer var1) {
      ErrorCode var2 = this.m_StreamAPI.submitVideoFrame(var1.getAddress());
      return var2;
   }

   public ErrorCode captureFrameBuffer_ReadPixels(FrameBuffer var1) {
      ErrorCode var2 = this.m_StreamAPI.captureFrameBuffer_ReadPixels(var1.getAddress());
      return var2;
   }

   public ErrorCode start(VideoParams var1, AudioParams var2, IngestServer var3, StartFlags var4, boolean var5) {
      if(var4 == null) {
         var4 = StartFlags.None;
      }

      ErrorCode var6 = this.m_StreamAPI.start(var1, var2, var3, var4.getValue(), var5);
      return var6;
   }

   public ErrorCode stop(boolean var1) {
      ErrorCode var2 = this.m_StreamAPI.stop(var1);
      return var2;
   }

   public ErrorCode pauseVideo() {
      ErrorCode var1 = this.m_StreamAPI.pauseVideo();
      return var1;
   }

   public long getStreamTime() {
      return this.m_StreamAPI.getStreamTime();
   }
}
