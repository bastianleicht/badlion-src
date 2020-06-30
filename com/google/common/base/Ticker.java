package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Platform;

@Beta
@GwtCompatible
public abstract class Ticker {
   private static final Ticker SYSTEM_TICKER = new Ticker() {
      public long read() {
         return Platform.systemNanoTime();
      }
   };

   public abstract long read();

   public static Ticker systemTicker() {
      return SYSTEM_TICKER;
   }
}
