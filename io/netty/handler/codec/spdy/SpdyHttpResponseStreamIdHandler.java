package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.util.ReferenceCountUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpdyHttpResponseStreamIdHandler extends MessageToMessageCodec {
   private static final Integer NO_ID = Integer.valueOf(-1);
   private final Queue ids = new LinkedList();

   public boolean acceptInboundMessage(Object msg) throws Exception {
      return msg instanceof HttpMessage || msg instanceof SpdyRstStreamFrame;
   }

   protected void encode(ChannelHandlerContext ctx, HttpMessage msg, List out) throws Exception {
      Integer id = (Integer)this.ids.poll();
      if(id != null && id.intValue() != NO_ID.intValue() && !msg.headers().contains("X-SPDY-Stream-ID")) {
         SpdyHttpHeaders.setStreamId(msg, id.intValue());
      }

      out.add(ReferenceCountUtil.retain(msg));
   }

   protected void decode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
      if(msg instanceof HttpMessage) {
         boolean contains = ((HttpMessage)msg).headers().contains("X-SPDY-Stream-ID");
         if(!contains) {
            this.ids.add(NO_ID);
         } else {
            this.ids.add(Integer.valueOf(SpdyHttpHeaders.getStreamId((HttpMessage)msg)));
         }
      } else if(msg instanceof SpdyRstStreamFrame) {
         this.ids.remove(Integer.valueOf(((SpdyRstStreamFrame)msg).streamId()));
      }

      out.add(ReferenceCountUtil.retain(msg));
   }
}
