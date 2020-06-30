package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;

public interface SpdyFrameDecoderDelegate {
   void readDataFrame(int var1, boolean var2, ByteBuf var3);

   void readSynStreamFrame(int var1, int var2, byte var3, boolean var4, boolean var5);

   void readSynReplyFrame(int var1, boolean var2);

   void readRstStreamFrame(int var1, int var2);

   void readSettingsFrame(boolean var1);

   void readSetting(int var1, int var2, boolean var3, boolean var4);

   void readSettingsEnd();

   void readPingFrame(int var1);

   void readGoAwayFrame(int var1, int var2);

   void readHeadersFrame(int var1, boolean var2);

   void readWindowUpdateFrame(int var1, int var2);

   void readHeaderBlock(ByteBuf var1);

   void readHeaderBlockEnd();

   void readFrameError(String var1);
}
