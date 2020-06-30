package io.netty.handler.codec.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageLite.Builder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder {
   protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List out) throws Exception {
      if(msg instanceof MessageLite) {
         out.add(Unpooled.wrappedBuffer(((MessageLite)msg).toByteArray()));
      } else {
         if(msg instanceof Builder) {
            out.add(Unpooled.wrappedBuffer(((Builder)msg).build().toByteArray()));
         }

      }
   }
}
