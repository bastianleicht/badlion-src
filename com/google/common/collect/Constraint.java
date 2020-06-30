package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface Constraint {
   Object checkElement(Object var1);

   String toString();
}
