package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.List;

public class WebSocket08FrameEncoder extends MessageToMessageEncoder implements WebSocketFrameEncoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private final boolean maskPayload;

   public WebSocket08FrameEncoder(boolean maskPayload) {
      this.maskPayload = maskPayload;
   }

   protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List out) throws Exception {
      ByteBuf data = msg.content();
      byte opcode;
      if(msg instanceof TextWebSocketFrame) {
         opcode = 1;
      } else if(msg instanceof PingWebSocketFrame) {
         opcode = 9;
      } else if(msg instanceof PongWebSocketFrame) {
         opcode = 10;
      } else if(msg instanceof CloseWebSocketFrame) {
         opcode = 8;
      } else if(msg instanceof BinaryWebSocketFrame) {
         opcode = 2;
      } else {
         if(!(msg instanceof ContinuationWebSocketFrame)) {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
         }

         opcode = 0;
      }

      int length = data.readableBytes();
      if(logger.isDebugEnabled()) {
         logger.debug("Encoding WebSocket Frame opCode=" + opcode + " length=" + length);
      }

      int b0 = 0;
      if(msg.isFinalFragment()) {
         b0 |= 128;
      }

      b0 = b0 | msg.rsv() % 8 << 4;
      b0 = b0 | opcode % 128;
      if(opcode == 9 && length > 125) {
         throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + length);
      } else {
         boolean release = true;
         ByteBuf buf = null;

         try {
            int maskLength = this.maskPayload?4:0;
            if(length <= 125) {
               int size = 2 + maskLength;
               if(this.maskPayload) {
                  size += length;
               }

               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               byte b = (byte)(this.maskPayload?128 | (byte)length:(byte)length);
               buf.writeByte(b);
            } else if(length <= '\uffff') {
               int size = 4 + maskLength;
               if(this.maskPayload) {
                  size += length;
               }

               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               buf.writeByte(this.maskPayload?254:126);
               buf.writeByte(length >>> 8 & 255);
               buf.writeByte(length & 255);
            } else {
               int size = 10 + maskLength;
               if(this.maskPayload) {
                  size += length;
               }

               buf = ctx.alloc().buffer(size);
               buf.writeByte(b0);
               buf.writeByte(this.maskPayload?255:127);
               buf.writeLong((long)length);
            }

            if(!this.maskPayload) {
               if(buf.writableBytes() >= data.readableBytes()) {
                  buf.writeBytes(data);
                  out.add(buf);
               } else {
                  out.add(buf);
                  out.add(data.retain());
               }
            } else {
               int random = (int)(Math.random() * 2.147483647E9D);
               byte[] mask = ByteBuffer.allocate(4).putInt(random).array();
               buf.writeBytes(mask);
               int counter = 0;

               for(int i = data.readerIndex(); i < data.writerIndex(); ++i) {
                  byte byteData = data.getByte(i);
                  buf.writeByte(byteData ^ mask[counter++ % 4]);
               }

               out.add(buf);
            }

            release = false;
         } finally {
            if(release && buf != null) {
               buf.release();
            }

         }

      }
   }
}
