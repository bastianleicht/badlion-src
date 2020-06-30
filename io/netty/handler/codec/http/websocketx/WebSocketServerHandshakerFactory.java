package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker00;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker07;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker08;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class WebSocketServerHandshakerFactory {
   private final String webSocketURL;
   private final String subprotocols;
   private final boolean allowExtensions;
   private final int maxFramePayloadLength;

   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
      this(webSocketURL, subprotocols, allowExtensions, 65536);
   }

   public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
      this.webSocketURL = webSocketURL;
      this.subprotocols = subprotocols;
      this.allowExtensions = allowExtensions;
      this.maxFramePayloadLength = maxFramePayloadLength;
   }

   public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
      String version = req.headers().get("Sec-WebSocket-Version");
      return (WebSocketServerHandshaker)(version != null?(version.equals(WebSocketVersion.V13.toHttpHeaderValue())?new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength):(version.equals(WebSocketVersion.V08.toHttpHeaderValue())?new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength):(version.equals(WebSocketVersion.V07.toHttpHeaderValue())?new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength):null))):new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.maxFramePayloadLength));
   }

   /** @deprecated */
   @Deprecated
   public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
      sendUnsupportedVersionResponse(channel);
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel) {
      return sendUnsupportedVersionResponse(channel, channel.newPromise());
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise) {
      HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED);
      res.headers().set((String)"Sec-WebSocket-Version", (Object)WebSocketVersion.V13.toHttpHeaderValue());
      return channel.write(res, promise);
   }
}
