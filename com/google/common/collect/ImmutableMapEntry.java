package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableEntry;
import javax.annotation.Nullable;

@GwtIncompatible("unnecessary")
abstract class ImmutableMapEntry extends ImmutableEntry {
   ImmutableMapEntry(Object key, Object value) {
      super(key, value);
      CollectPreconditions.checkEntryNotNull(key, value);
   }

   ImmutableMapEntry(ImmutableMapEntry contents) {
      super(contents.getKey(), contents.getValue());
   }

   @Nullable
   abstract ImmutableMapEntry getNextInKeyBucket();

   @Nullable
   abstract ImmutableMapEntry getNextInValueBucket();

   static final class TerminalEntry extends ImmutableMapEntry {
      TerminalEntry(ImmutableMapEntry contents) {
         super(contents);
      }

      TerminalEntry(Object key, Object value) {
         super(key, value);
      }

      @Nullable
      ImmutableMapEntry getNextInKeyBucket() {
         return null;
      }

      @Nullable
      ImmutableMapEntry getNextInValueBucket() {
         return null;
      }
   }
}
