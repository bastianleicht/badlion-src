package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ListIterator;

@GwtCompatible
public abstract class UnmodifiableListIterator extends UnmodifiableIterator implements ListIterator {
   /** @deprecated */
   @Deprecated
   public final void add(Object e) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void set(Object e) {
      throw new UnsupportedOperationException();
   }
}
