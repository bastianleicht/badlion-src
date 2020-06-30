package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedMap extends ForwardingMap implements SortedMap {
   protected abstract SortedMap delegate();

   public Comparator comparator() {
      return this.delegate().comparator();
   }

   public Object firstKey() {
      return this.delegate().firstKey();
   }

   public SortedMap headMap(Object toKey) {
      return this.delegate().headMap(toKey);
   }

   public Object lastKey() {
      return this.delegate().lastKey();
   }

   public SortedMap subMap(Object fromKey, Object toKey) {
      return this.delegate().subMap(fromKey, toKey);
   }

   public SortedMap tailMap(Object fromKey) {
      return this.delegate().tailMap(fromKey);
   }

   private int unsafeCompare(Object k1, Object k2) {
      Comparator<? super K> comparator = this.comparator();
      return comparator == null?((Comparable)k1).compareTo(k2):comparator.compare(k1, k2);
   }

   @Beta
   protected boolean standardContainsKey(@Nullable Object key) {
      try {
         Object ceilingKey = this.tailMap(key).firstKey();
         return this.unsafeCompare(ceilingKey, key) == 0;
      } catch (ClassCastException var4) {
         return false;
      } catch (NoSuchElementException var5) {
         return false;
      } catch (NullPointerException var6) {
         return false;
      }
   }

   @Beta
   protected SortedMap standardSubMap(Object fromKey, Object toKey) {
      Preconditions.checkArgument(this.unsafeCompare(fromKey, toKey) <= 0, "fromKey must be <= toKey");
      return this.tailMap(fromKey).headMap(toKey);
   }

   @Beta
   protected class StandardKeySet extends Maps.SortedKeySet {
      public StandardKeySet() {
         super(ForwardingSortedMap.this);
      }
   }
}
