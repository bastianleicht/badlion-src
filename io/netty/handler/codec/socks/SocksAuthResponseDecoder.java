package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksResponse;
import io.netty.handler.codec.socks.SocksSubnegotiationVersion;
import java.util.List;

public class SocksAuthResponseDecoder extends ReplayingDecoder {
   private static final String name = "SOCKS_AUTH_RESPONSE_DECODER";
   private SocksSubnegotiationVersion version;
   private SocksAuthStatus authStatus;
   private SocksResponse msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;

   /** @deprecated */
   @Deprecated
   public static String getName() {
      return "SOCKS_AUTH_RESPONSE_DECODER";
   }

   public SocksAuthResponseDecoder() {
      super(SocksAuthResponseDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List out) throws Exception {
      switch((SocksAuthResponseDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         this.version = SocksSubnegotiationVersion.valueOf(byteBuf.readByte());
         if(this.version != SocksSubnegotiationVersion.AUTH_PASSWORD) {
            break;
         }

         this.checkpoint(SocksAuthResponseDecoder.State.READ_AUTH_RESPONSE);
      case READ_AUTH_RESPONSE:
         this.authStatus = SocksAuthStatus.valueOf(byteBuf.readByte());
         this.msg = new SocksAuthResponse(this.authStatus);
      }

      channelHandlerContext.pipeline().remove((ChannelHandler)this);
      out.add(this.msg);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_AUTH_RESPONSE;
   }
}
