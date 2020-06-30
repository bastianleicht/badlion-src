package tv.twitch.broadcast;

import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.PixelFormat;

public class VideoParams {
   public int outputWidth;
   public int outputHeight;
   public PixelFormat pixelFormat = PixelFormat.TTV_PF_BGRA;
   public int maxKbps;
   public int targetFps;
   public EncodingCpuUsage encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
   public boolean disableAdaptiveBitrate = false;
   public boolean verticalFlip = false;

   public VideoParams clone() {
      VideoParams var1 = new VideoParams();
      var1.outputWidth = this.outputWidth;
      var1.outputHeight = this.outputHeight;
      var1.pixelFormat = this.pixelFormat;
      var1.maxKbps = this.maxKbps;
      var1.targetFps = this.targetFps;
      var1.encodingCpuUsage = this.encodingCpuUsage;
      var1.disableAdaptiveBitrate = this.disableAdaptiveBitrate;
      var1.verticalFlip = this.verticalFlip;
      return var1;
   }
}
