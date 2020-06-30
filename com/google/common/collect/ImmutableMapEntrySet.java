package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class ImmutableMapEntrySet extends ImmutableSet {
   abstract ImmutableMap map();

   public int size() {
      return this.map().size();
   }

   public boolean contains(@Nullable Object object) {
      if(!(object instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> entry = (Entry)object;
         V value = this.map().get(entry.getKey());
         return value != null && value.equals(entry.getValue());
      }
   }

   boolean isPartialView() {
      return this.map().isPartialView();
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new ImmutableMapEntrySet.EntrySetSerializedForm(this.map());
   }

   @GwtIncompatible("serialization")
   private static class EntrySetSerializedForm implements Serializable {
      final ImmutableMap map;
      private static final long serialVersionUID = 0L;

      EntrySetSerializedForm(ImmutableMap map) {
         this.map = map;
      }

      Object readResolve() {
         return this.map.entrySet();
      }
   }
}
