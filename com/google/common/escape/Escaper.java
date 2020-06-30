package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;

@Beta
@GwtCompatible
public abstract class Escaper {
   private final Function asFunction = new Function() {
      public String apply(String from) {
         return Escaper.this.escape(from);
      }
   };

   public abstract String escape(String var1);

   public final Function asFunction() {
      return this.asFunction;
   }
}
