package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class SctpOutboundByteStreamHandler extends MessageToMessageEncoder {
   private final int streamIdentifier;
   private final int protocolIdentifier;

   public SctpOutboundByteStreamHandler(int streamIdentifier, int protocolIdentifier) {
      this.streamIdentifier = streamIdentifier;
      this.protocolIdentifier = protocolIdentifier;
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
      out.add(new SctpMessage(this.streamIdentifier, this.protocolIdentifier, msg.retain()));
   }
}
