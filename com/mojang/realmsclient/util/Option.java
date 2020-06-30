package com.mojang.realmsclient.util;

public abstract class Option {
   public abstract Object get();

   public static Option.Some some(Object a) {
      return new Option.Some(a);
   }

   public static Option.None none() {
      return new Option.None();
   }

   public static final class None extends Option {
      public Object get() {
         throw new RuntimeException("None has no value");
      }
   }

   public static final class Some extends Option {
      private final Object a;

      public Some(Object a) {
         this.a = a;
      }

      public Object get() {
         return this.a;
      }

      public static Option of(Object value) {
         return new Option.Some(value);
      }
   }
}
