package io.netty.util;

public interface ReferenceCounted {
   int refCnt();

   ReferenceCounted retain();

   ReferenceCounted retain(int var1);

   boolean release();

   boolean release(int var1);
}
