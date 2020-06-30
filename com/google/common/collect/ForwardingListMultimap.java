package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingListMultimap extends ForwardingMultimap implements ListMultimap {
   protected abstract ListMultimap delegate();

   public List get(@Nullable Object key) {
      return this.delegate().get(key);
   }

   public List removeAll(@Nullable Object key) {
      return this.delegate().removeAll(key);
   }

   public List replaceValues(Object key, Iterable values) {
      return this.delegate().replaceValues(key, values);
   }
}
