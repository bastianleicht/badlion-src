package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedIterable;
import com.google.common.collect.SortedMultisetBridge;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

@Beta
@GwtCompatible(
   emulated = true
)
public interface SortedMultiset extends SortedMultisetBridge, SortedIterable {
   Comparator comparator();

   Multiset.Entry firstEntry();

   Multiset.Entry lastEntry();

   Multiset.Entry pollFirstEntry();

   Multiset.Entry pollLastEntry();

   NavigableSet elementSet();

   Set entrySet();

   Iterator iterator();

   SortedMultiset descendingMultiset();

   SortedMultiset headMultiset(Object var1, BoundType var2);

   SortedMultiset subMultiset(Object var1, BoundType var2, Object var3, BoundType var4);

   SortedMultiset tailMultiset(Object var1, BoundType var2);
}
