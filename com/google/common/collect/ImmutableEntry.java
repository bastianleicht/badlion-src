package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractMapEntry;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
class ImmutableEntry extends AbstractMapEntry implements Serializable {
   final Object key;
   final Object value;
   private static final long serialVersionUID = 0L;

   ImmutableEntry(@Nullable Object key, @Nullable Object value) {
      this.key = key;
      this.value = value;
   }

   @Nullable
   public final Object getKey() {
      return this.key;
   }

   @Nullable
   public final Object getValue() {
      return this.value;
   }

   public final Object setValue(Object value) {
      throw new UnsupportedOperationException();
   }
}
