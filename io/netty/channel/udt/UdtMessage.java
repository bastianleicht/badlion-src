package io.netty.channel.udt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public final class UdtMessage extends DefaultByteBufHolder {
   public UdtMessage(ByteBuf data) {
      super(data);
   }

   public UdtMessage copy() {
      return new UdtMessage(this.content().copy());
   }

   public UdtMessage duplicate() {
      return new UdtMessage(this.content().duplicate());
   }

   public UdtMessage retain() {
      super.retain();
      return this;
   }

   public UdtMessage retain(int increment) {
      super.retain(increment);
      return this;
   }
}
