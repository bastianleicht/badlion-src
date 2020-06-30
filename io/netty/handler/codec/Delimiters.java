package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class Delimiters {
   public static ByteBuf[] nulDelimiter() {
      return new ByteBuf[]{Unpooled.wrappedBuffer(new byte[]{(byte)0})};
   }

   public static ByteBuf[] lineDelimiter() {
      return new ByteBuf[]{Unpooled.wrappedBuffer(new byte[]{(byte)13, (byte)10}), Unpooled.wrappedBuffer(new byte[]{(byte)10})};
   }
}
