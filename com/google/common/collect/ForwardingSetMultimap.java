package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.SetMultimap;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSetMultimap extends ForwardingMultimap implements SetMultimap {
   protected abstract SetMultimap delegate();

   public Set entries() {
      return this.delegate().entries();
   }

   public Set get(@Nullable Object key) {
      return this.delegate().get(key);
   }

   public Set removeAll(@Nullable Object key) {
      return this.delegate().removeAll(key);
   }

   public Set replaceValues(Object key, Iterable values) {
      return this.delegate().replaceValues(key, values);
   }
}
