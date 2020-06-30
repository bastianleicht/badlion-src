package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public interface PeekingIterator extends Iterator {
   Object peek();

   Object next();

   void remove();
}
