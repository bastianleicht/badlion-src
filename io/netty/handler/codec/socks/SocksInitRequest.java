package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksRequestType;
import java.util.Collections;
import java.util.List;

public final class SocksInitRequest extends SocksRequest {
   private final List authSchemes;

   public SocksInitRequest(List authSchemes) {
      super(SocksRequestType.INIT);
      if(authSchemes == null) {
         throw new NullPointerException("authSchemes");
      } else {
         this.authSchemes = authSchemes;
      }
   }

   public List authSchemes() {
      return Collections.unmodifiableList(this.authSchemes);
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(this.protocolVersion().byteValue());
      byteBuf.writeByte(this.authSchemes.size());

      for(SocksAuthScheme authScheme : this.authSchemes) {
         byteBuf.writeByte(authScheme.byteValue());
      }

   }
}
