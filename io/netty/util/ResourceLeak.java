package io.netty.util;

public interface ResourceLeak {
   void record();

   boolean close();
}
