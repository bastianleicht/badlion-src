package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMap;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap extends ForwardingMap implements ConcurrentMap {
   protected abstract ConcurrentMap delegate();

   public Object putIfAbsent(Object key, Object value) {
      return this.delegate().putIfAbsent(key, value);
   }

   public boolean remove(Object key, Object value) {
      return this.delegate().remove(key, value);
   }

   public Object replace(Object key, Object value) {
      return this.delegate().replace(key, value);
   }

   public boolean replace(Object key, Object oldValue, Object newValue) {
      return this.delegate().replace(key, oldValue, newValue);
   }
}
