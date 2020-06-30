package io.netty.util.internal;

import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class UnpaddedInternalThreadLocalMap {
   static ThreadLocal slowThreadLocalMap;
   static final AtomicInteger nextIndex = new AtomicInteger();
   Object[] indexedVariables;
   int futureListenerStackDepth;
   int localChannelReaderStackDepth;
   Map handlerSharableCache;
   IntegerHolder counterHashCode;
   ThreadLocalRandom random;
   Map typeParameterMatcherGetCache;
   Map typeParameterMatcherFindCache;
   StringBuilder stringBuilder;
   Map charsetEncoderCache;
   Map charsetDecoderCache;

   UnpaddedInternalThreadLocalMap(Object[] indexedVariables) {
      this.indexedVariables = indexedVariables;
   }
}
