package com.ibm.icu.impl;

import com.ibm.icu.impl.CacheBase;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SoftCache extends CacheBase {
   private ConcurrentHashMap map = new ConcurrentHashMap();

   public final Object getInstance(Object key, Object data) {
      SoftCache.SettableSoftReference<V> valueRef = (SoftCache.SettableSoftReference)this.map.get(key);
      if(valueRef != null) {
         synchronized(valueRef) {
            V value = valueRef.ref.get();
            if(value != null) {
               return value;
            } else {
               value = this.createInstance(key, data);
               if(value != null) {
                  valueRef.ref = new SoftReference(value);
               }

               return value;
            }
         }
      } else {
         Object value = this.createInstance(key, data);
         if(value == null) {
            return null;
         } else {
            valueRef = (SoftCache.SettableSoftReference)this.map.putIfAbsent(key, new SoftCache.SettableSoftReference(value));
            return valueRef == null?value:valueRef.setIfAbsent(value);
         }
      }
   }

   private static final class SettableSoftReference {
      private SoftReference ref;

      private SettableSoftReference(Object value) {
         this.ref = new SoftReference(value);
      }

      private synchronized Object setIfAbsent(Object value) {
         V oldValue = this.ref.get();
         if(oldValue == null) {
            this.ref = new SoftReference(value);
            return value;
         } else {
            return oldValue;
         }
      }
   }
}
