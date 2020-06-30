package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WebSocketServerHandshaker {
   protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
   private final String uri;
   private final String[] subprotocols;
   private final WebSocketVersion version;
   private final int maxFramePayloadLength;
   private String selectedSubprotocol;
   public static final String SUB_PROTOCOL_WILDCARD = "*";

   protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, int maxFramePayloadLength) {
      this.version = version;
      this.uri = uri;
      if(subprotocols != null) {
         String[] subprotocolArray = StringUtil.split(subprotocols, ',');

         for(int i = 0; i < subprotocolArray.length; ++i) {
            subprotocolArray[i] = subprotocolArray[i].trim();
         }

         this.subprotocols = subprotocolArray;
      } else {
         this.subprotocols = EmptyArrays.EMPTY_STRINGS;
      }

      this.maxFramePayloadLength = maxFramePayloadLength;
   }

   public String uri() {
      return this.uri;
   }

   public Set subprotocols() {
      Set<String> ret = new LinkedHashSet();
      Collections.addAll(ret, this.subprotocols);
      return ret;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public ChannelFuture handshake(Channel channel, FullHttpRequest req) {
      return this.handshake(channel, req, (HttpHeaders)null, channel.newPromise());
   }

   public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, final ChannelPromise promise) {
      if(logger.isDebugEnabled()) {
         logger.debug("{} WebSocket version {} server handshake", channel, this.version());
      }

      FullHttpResponse response = this.newHandshakeResponse(req, responseHeaders);
      ChannelPipeline p = channel.pipeline();
      if(p.get(HttpObjectAggregator.class) != null) {
         p.remove(HttpObjectAggregator.class);
      }

      if(p.get(HttpContentCompressor.class) != null) {
         p.remove(HttpContentCompressor.class);
      }

      ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
      final String encoderName;
      if(ctx == null) {
         ctx = p.context(HttpServerCodec.class);
         if(ctx == null) {
            promise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
            return promise;
         }

         p.addBefore(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
         p.addBefore(ctx.name(), "wsencoder", this.newWebSocketEncoder());
         encoderName = ctx.name();
      } else {
         p.replace((String)ctx.name(), "wsdecoder", this.newWebsocketDecoder());
         encoderName = p.context(HttpResponseEncoder.class).name();
         p.addBefore(encoderName, "wsencoder", this.newWebSocketEncoder());
      }

      channel.writeAndFlush(response).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()) {
               ChannelPipeline p = future.channel().pipeline();
               p.remove(encoderName);
               promise.setSuccess();
            } else {
               promise.setFailure(future.cause());
            }

         }
      });
      return promise;
   }

   protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2);

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         return this.close(channel, frame, channel.newPromise());
      }
   }

   public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         return channel.writeAndFlush(frame, promise).addListener(ChannelFutureListener.CLOSE);
      }
   }

   protected String selectSubprotocol(String requestedSubprotocols) {
      if(requestedSubprotocols != null && this.subprotocols.length != 0) {
         String[] requestedSubprotocolArray = StringUtil.split(requestedSubprotocols, ',');

         for(String p : requestedSubprotocolArray) {
            String requestedSubprotocol = p.trim();

            for(String supportedSubprotocol : this.subprotocols) {
               if("*".equals(supportedSubprotocol) || requestedSubprotocol.equals(supportedSubprotocol)) {
                  this.selectedSubprotocol = requestedSubprotocol;
                  return requestedSubprotocol;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String selectedSubprotocol() {
      return this.selectedSubprotocol;
   }

   protected abstract WebSocketFrameDecoder newWebsocketDecoder();

   protected abstract WebSocketFrameEncoder newWebSocketEncoder();
}
