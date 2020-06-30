package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksResponse;
import io.netty.handler.codec.socks.SocksResponseType;

public final class UnknownSocksResponse extends SocksResponse {
   public UnknownSocksResponse() {
      super(SocksResponseType.UNKNOWN);
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
   }
}
