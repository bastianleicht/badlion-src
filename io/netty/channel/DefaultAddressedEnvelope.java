package io.netty.channel;

import io.netty.channel.AddressedEnvelope;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope implements AddressedEnvelope {
   private final Object message;
   private final SocketAddress sender;
   private final SocketAddress recipient;

   public DefaultAddressedEnvelope(Object message, SocketAddress recipient, SocketAddress sender) {
      if(message == null) {
         throw new NullPointerException("message");
      } else {
         this.message = message;
         this.sender = sender;
         this.recipient = recipient;
      }
   }

   public DefaultAddressedEnvelope(Object message, SocketAddress recipient) {
      this(message, recipient, (SocketAddress)null);
   }

   public Object content() {
      return this.message;
   }

   public SocketAddress sender() {
      return this.sender;
   }

   public SocketAddress recipient() {
      return this.recipient;
   }

   public int refCnt() {
      return this.message instanceof ReferenceCounted?((ReferenceCounted)this.message).refCnt():1;
   }

   public AddressedEnvelope retain() {
      ReferenceCountUtil.retain(this.message);
      return this;
   }

   public AddressedEnvelope retain(int increment) {
      ReferenceCountUtil.retain(this.message, increment);
      return this;
   }

   public boolean release() {
      return ReferenceCountUtil.release(this.message);
   }

   public boolean release(int decrement) {
      return ReferenceCountUtil.release(this.message, decrement);
   }

   public String toString() {
      return this.sender != null?StringUtil.simpleClassName((Object)this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')':StringUtil.simpleClassName((Object)this) + "(=> " + this.recipient + ", " + this.message + ')';
   }
}
