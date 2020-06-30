package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksRequestType;

public final class UnknownSocksRequest extends SocksRequest {
   public UnknownSocksRequest() {
      super(SocksRequestType.UNKNOWN);
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
   }
}
