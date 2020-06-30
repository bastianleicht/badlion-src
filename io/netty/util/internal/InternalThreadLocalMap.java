package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.util.internal.UnpaddedInternalThreadLocalMap;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class InternalThreadLocalMap extends UnpaddedInternalThreadLocalMap {
   public static final Object UNSET = new Object();
   public long rp1;
   public long rp2;
   public long rp3;
   public long rp4;
   public long rp5;
   public long rp6;
   public long rp7;
   public long rp8;
   public long rp9;

   public static InternalThreadLocalMap getIfSet() {
      Thread thread = Thread.currentThread();
      InternalThreadLocalMap threadLocalMap;
      if(thread instanceof FastThreadLocalThread) {
         threadLocalMap = ((FastThreadLocalThread)thread).threadLocalMap();
      } else {
         ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
         if(slowThreadLocalMap == null) {
            threadLocalMap = null;
         } else {
            threadLocalMap = (InternalThreadLocalMap)slowThreadLocalMap.get();
         }
      }

      return threadLocalMap;
   }

   public static InternalThreadLocalMap get() {
      Thread thread = Thread.currentThread();
      return thread instanceof FastThreadLocalThread?fastGet((FastThreadLocalThread)thread):slowGet();
   }

   private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
      InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
      if(threadLocalMap == null) {
         thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
      }

      return threadLocalMap;
   }

   private static InternalThreadLocalMap slowGet() {
      ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
      if(slowThreadLocalMap == null) {
         UnpaddedInternalThreadLocalMap.slowThreadLocalMap = slowThreadLocalMap = new ThreadLocal();
      }

      InternalThreadLocalMap ret = (InternalThreadLocalMap)slowThreadLocalMap.get();
      if(ret == null) {
         ret = new InternalThreadLocalMap();
         slowThreadLocalMap.set(ret);
      }

      return ret;
   }

   public static void remove() {
      Thread thread = Thread.currentThread();
      if(thread instanceof FastThreadLocalThread) {
         ((FastThreadLocalThread)thread).setThreadLocalMap((InternalThreadLocalMap)null);
      } else {
         ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
         if(slowThreadLocalMap != null) {
            slowThreadLocalMap.remove();
         }
      }

   }

   public static void destroy() {
      slowThreadLocalMap = null;
   }

   public static int nextVariableIndex() {
      int index = nextIndex.getAndIncrement();
      if(index < 0) {
         nextIndex.decrementAndGet();
         throw new IllegalStateException("too many thread-local indexed variables");
      } else {
         return index;
      }
   }

   public static int lastVariableIndex() {
      return nextIndex.get() - 1;
   }

   private InternalThreadLocalMap() {
      super(newIndexedVariableTable());
   }

   private static Object[] newIndexedVariableTable() {
      Object[] array = new Object[32];
      Arrays.fill(array, UNSET);
      return array;
   }

   public int size() {
      int count = 0;
      if(this.futureListenerStackDepth != 0) {
         ++count;
      }

      if(this.localChannelReaderStackDepth != 0) {
         ++count;
      }

      if(this.handlerSharableCache != null) {
         ++count;
      }

      if(this.counterHashCode != null) {
         ++count;
      }

      if(this.random != null) {
         ++count;
      }

      if(this.typeParameterMatcherGetCache != null) {
         ++count;
      }

      if(this.typeParameterMatcherFindCache != null) {
         ++count;
      }

      if(this.stringBuilder != null) {
         ++count;
      }

      if(this.charsetEncoderCache != null) {
         ++count;
      }

      if(this.charsetDecoderCache != null) {
         ++count;
      }

      for(Object o : this.indexedVariables) {
         if(o != UNSET) {
            ++count;
         }
      }

      return count - 1;
   }

   public StringBuilder stringBuilder() {
      StringBuilder builder = this.stringBuilder;
      if(builder == null) {
         this.stringBuilder = builder = new StringBuilder(512);
      } else {
         builder.setLength(0);
      }

      return builder;
   }

   public Map charsetEncoderCache() {
      Map<Charset, CharsetEncoder> cache = this.charsetEncoderCache;
      if(cache == null) {
         this.charsetEncoderCache = cache = new IdentityHashMap();
      }

      return cache;
   }

   public Map charsetDecoderCache() {
      Map<Charset, CharsetDecoder> cache = this.charsetDecoderCache;
      if(cache == null) {
         this.charsetDecoderCache = cache = new IdentityHashMap();
      }

      return cache;
   }

   public int futureListenerStackDepth() {
      return this.futureListenerStackDepth;
   }

   public void setFutureListenerStackDepth(int futureListenerStackDepth) {
      this.futureListenerStackDepth = futureListenerStackDepth;
   }

   public ThreadLocalRandom random() {
      ThreadLocalRandom r = this.random;
      if(r == null) {
         this.random = r = new ThreadLocalRandom();
      }

      return r;
   }

   public Map typeParameterMatcherGetCache() {
      Map<Class<?>, TypeParameterMatcher> cache = this.typeParameterMatcherGetCache;
      if(cache == null) {
         this.typeParameterMatcherGetCache = cache = new IdentityHashMap();
      }

      return cache;
   }

   public Map typeParameterMatcherFindCache() {
      Map<Class<?>, Map<String, TypeParameterMatcher>> cache = this.typeParameterMatcherFindCache;
      if(cache == null) {
         this.typeParameterMatcherFindCache = cache = new IdentityHashMap();
      }

      return cache;
   }

   public IntegerHolder counterHashCode() {
      return this.counterHashCode;
   }

   public void setCounterHashCode(IntegerHolder counterHashCode) {
      this.counterHashCode = counterHashCode;
   }

   public Map handlerSharableCache() {
      Map<Class<?>, Boolean> cache = this.handlerSharableCache;
      if(cache == null) {
         this.handlerSharableCache = cache = new WeakHashMap(4);
      }

      return cache;
   }

   public int localChannelReaderStackDepth() {
      return this.localChannelReaderStackDepth;
   }

   public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
      this.localChannelReaderStackDepth = localChannelReaderStackDepth;
   }

   public Object indexedVariable(int index) {
      Object[] lookup = this.indexedVariables;
      return index < lookup.length?lookup[index]:UNSET;
   }

   public boolean setIndexedVariable(int index, Object value) {
      Object[] lookup = this.indexedVariables;
      if(index < lookup.length) {
         Object oldValue = lookup[index];
         lookup[index] = value;
         return oldValue == UNSET;
      } else {
         this.expandIndexedVariableTableAndSet(index, value);
         return true;
      }
   }

   private void expandIndexedVariableTableAndSet(int index, Object value) {
      Object[] oldArray = this.indexedVariables;
      int oldCapacity = oldArray.length;
      int newCapacity = index | index >>> 1;
      newCapacity = newCapacity | newCapacity >>> 2;
      newCapacity = newCapacity | newCapacity >>> 4;
      newCapacity = newCapacity | newCapacity >>> 8;
      newCapacity = newCapacity | newCapacity >>> 16;
      ++newCapacity;
      Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
      Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
      newArray[index] = value;
      this.indexedVariables = newArray;
   }

   public Object removeIndexedVariable(int index) {
      Object[] lookup = this.indexedVariables;
      if(index < lookup.length) {
         Object v = lookup[index];
         lookup[index] = UNSET;
         return v;
      } else {
         return UNSET;
      }
   }

   public boolean isIndexedVariableSet(int index) {
      Object[] lookup = this.indexedVariables;
      return index < lookup.length && lookup[index] != UNSET;
   }
}
