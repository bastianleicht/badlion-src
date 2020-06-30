package io.netty.channel.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import java.net.InetSocketAddress;

public final class DatagramPacket extends DefaultAddressedEnvelope implements ByteBufHolder {
   public DatagramPacket(ByteBuf data, InetSocketAddress recipient) {
      super(data, recipient);
   }

   public DatagramPacket(ByteBuf data, InetSocketAddress recipient, InetSocketAddress sender) {
      super(data, recipient, sender);
   }

   public DatagramPacket copy() {
      return new DatagramPacket(((ByteBuf)this.content()).copy(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
   }

   public DatagramPacket duplicate() {
      return new DatagramPacket(((ByteBuf)this.content()).duplicate(), (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
   }

   public DatagramPacket retain() {
      super.retain();
      return this;
   }

   public DatagramPacket retain(int increment) {
      super.retain(increment);
      return this;
   }
}
