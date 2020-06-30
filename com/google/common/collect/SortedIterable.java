package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;

@GwtCompatible
interface SortedIterable extends Iterable {
   Comparator comparator();

   Iterator iterator();
}
