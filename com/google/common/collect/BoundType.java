package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType {
   OPEN {
      BoundType flip() {
         return CLOSED;
      }
   },
   CLOSED {
      BoundType flip() {
         return OPEN;
      }
   };

   private BoundType() {
   }

   static BoundType forBoolean(boolean inclusive) {
      return inclusive?CLOSED:OPEN;
   }

   abstract BoundType flip();
}
