package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksInitRequest;
import io.netty.handler.codec.socks.SocksProtocolVersion;
import io.netty.handler.codec.socks.SocksRequest;
import java.util.ArrayList;
import java.util.List;

public class SocksInitRequestDecoder extends ReplayingDecoder {
   private static final String name = "SOCKS_INIT_REQUEST_DECODER";
   private final List authSchemes = new ArrayList();
   private SocksProtocolVersion version;
   private byte authSchemeNum;
   private SocksRequest msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;

   /** @deprecated */
   @Deprecated
   public static String getName() {
      return "SOCKS_INIT_REQUEST_DECODER";
   }

   public SocksInitRequestDecoder() {
      super(SocksInitRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
      switch((SocksInitRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         this.version = SocksProtocolVersion.valueOf(byteBuf.readByte());
         if(this.version != SocksProtocolVersion.SOCKS5) {
            break;
         }

         this.checkpoint(SocksInitRequestDecoder.State.READ_AUTH_SCHEMES);
      case READ_AUTH_SCHEMES:
         this.authSchemes.clear();
         this.authSchemeNum = byteBuf.readByte();

         for(int i = 0; i < this.authSchemeNum; ++i) {
            this.authSchemes.add(SocksAuthScheme.valueOf(byteBuf.readByte()));
         }

         this.msg = new SocksInitRequest(this.authSchemes);
      }

      ctx.pipeline().remove((ChannelHandler)this);
      out.add(this.msg);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_AUTH_SCHEMES;
   }
}
