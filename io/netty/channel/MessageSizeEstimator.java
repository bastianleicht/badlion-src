package io.netty.channel;

public interface MessageSizeEstimator {
   MessageSizeEstimator.Handle newHandle();

   public interface Handle {
      int size(Object var1);
   }
}
