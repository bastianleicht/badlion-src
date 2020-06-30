package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksMessageType;
import io.netty.handler.codec.socks.SocksProtocolVersion;

public abstract class SocksMessage {
   private final SocksMessageType type;
   private final SocksProtocolVersion protocolVersion = SocksProtocolVersion.SOCKS5;

   protected SocksMessage(SocksMessageType type) {
      if(type == null) {
         throw new NullPointerException("type");
      } else {
         this.type = type;
      }
   }

   public SocksMessageType type() {
      return this.type;
   }

   public SocksProtocolVersion protocolVersion() {
      return this.protocolVersion;
   }

   /** @deprecated */
   @Deprecated
   public abstract void encodeAsByteBuf(ByteBuf var1);
}
