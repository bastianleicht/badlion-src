package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableListIterator;

@GwtCompatible(
   emulated = true
)
class RegularImmutableAsList extends ImmutableAsList {
   private final ImmutableCollection delegate;
   private final ImmutableList delegateList;

   RegularImmutableAsList(ImmutableCollection delegate, ImmutableList delegateList) {
      this.delegate = delegate;
      this.delegateList = delegateList;
   }

   RegularImmutableAsList(ImmutableCollection delegate, Object[] array) {
      this(delegate, ImmutableList.asImmutableList(array));
   }

   ImmutableCollection delegateCollection() {
      return this.delegate;
   }

   ImmutableList delegateList() {
      return this.delegateList;
   }

   public UnmodifiableListIterator listIterator(int index) {
      return this.delegateList.listIterator(index);
   }

   @GwtIncompatible("not present in emulated superclass")
   int copyIntoArray(Object[] dst, int offset) {
      return this.delegateList.copyIntoArray(dst, offset);
   }

   public Object get(int index) {
      return this.delegateList.get(index);
   }
}
