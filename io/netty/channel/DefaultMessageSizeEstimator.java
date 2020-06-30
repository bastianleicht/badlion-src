package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.FileRegion;
import io.netty.channel.MessageSizeEstimator;

public final class DefaultMessageSizeEstimator implements MessageSizeEstimator {
   public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator(0);
   private final MessageSizeEstimator.Handle handle;

   public DefaultMessageSizeEstimator(int unknownSize) {
      if(unknownSize < 0) {
         throw new IllegalArgumentException("unknownSize: " + unknownSize + " (expected: >= 0)");
      } else {
         this.handle = new DefaultMessageSizeEstimator.HandleImpl(unknownSize);
      }
   }

   public MessageSizeEstimator.Handle newHandle() {
      return this.handle;
   }

   private static final class HandleImpl implements MessageSizeEstimator.Handle {
      private final int unknownSize;

      private HandleImpl(int unknownSize) {
         this.unknownSize = unknownSize;
      }

      public int size(Object msg) {
         return msg instanceof ByteBuf?((ByteBuf)msg).readableBytes():(msg instanceof ByteBufHolder?((ByteBufHolder)msg).content().readableBytes():(msg instanceof FileRegion?0:this.unknownSize));
      }
   }
}
