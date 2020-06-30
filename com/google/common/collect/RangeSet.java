package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.collect.Range;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public interface RangeSet {
   boolean contains(Comparable var1);

   Range rangeContaining(Comparable var1);

   boolean encloses(Range var1);

   boolean enclosesAll(RangeSet var1);

   boolean isEmpty();

   Range span();

   Set asRanges();

   RangeSet complement();

   RangeSet subRangeSet(Range var1);

   void add(Range var1);

   void remove(Range var1);

   void clear();

   void addAll(RangeSet var1);

   void removeAll(RangeSet var1);

   boolean equals(@Nullable Object var1);

   int hashCode();

   String toString();
}
