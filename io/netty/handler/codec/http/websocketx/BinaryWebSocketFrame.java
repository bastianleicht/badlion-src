package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class BinaryWebSocketFrame extends WebSocketFrame {
   public BinaryWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public BinaryWebSocketFrame(ByteBuf binaryData) {
      super(binaryData);
   }

   public BinaryWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
      super(finalFragment, rsv, binaryData);
   }

   public BinaryWebSocketFrame copy() {
      return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
   }

   public BinaryWebSocketFrame duplicate() {
      return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
   }

   public BinaryWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public BinaryWebSocketFrame retain(int increment) {
      super.retain(increment);
      return this;
   }
}
