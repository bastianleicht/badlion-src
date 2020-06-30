package com.mojang.realmsclient.util;

public class Pair {
   private final Object first;
   private final Object second;

   protected Pair(Object first, Object second) {
      this.first = first;
      this.second = second;
   }

   public static Pair of(Object a, Object b) {
      return new Pair(a, b);
   }

   public Object first() {
      return this.first;
   }

   public Object second() {
      return this.second;
   }

   public String mkString(String separator) {
      return String.format("%s%s%s", new Object[]{this.first, separator, this.second});
   }
}
