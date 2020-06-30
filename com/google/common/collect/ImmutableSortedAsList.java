package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedIterable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableSortedAsList extends RegularImmutableAsList implements SortedIterable {
   ImmutableSortedAsList(ImmutableSortedSet backingSet, ImmutableList backingList) {
      super(backingSet, (ImmutableList)backingList);
   }

   ImmutableSortedSet delegateCollection() {
      return (ImmutableSortedSet)super.delegateCollection();
   }

   public Comparator comparator() {
      return this.delegateCollection().comparator();
   }

   @GwtIncompatible("ImmutableSortedSet.indexOf")
   public int indexOf(@Nullable Object target) {
      int index = this.delegateCollection().indexOf(target);
      return index >= 0 && this.get(index).equals(target)?index:-1;
   }

   @GwtIncompatible("ImmutableSortedSet.indexOf")
   public int lastIndexOf(@Nullable Object target) {
      return this.indexOf(target);
   }

   public boolean contains(Object target) {
      return this.indexOf(target) >= 0;
   }

   @GwtIncompatible("super.subListUnchecked does not exist; inherited subList is valid if slow")
   ImmutableList subListUnchecked(int fromIndex, int toIndex) {
      return (new RegularImmutableSortedSet(super.subListUnchecked(fromIndex, toIndex), this.comparator())).asList();
   }
}
