package com.google.gson.internal;

public final class $Gson$Preconditions {
   public static Object checkNotNull(Object obj) {
      if(obj == null) {
         throw new NullPointerException();
      } else {
         return obj;
      }
   }

   public static void checkArgument(boolean condition) {
      if(!condition) {
         throw new IllegalArgumentException();
      }
   }
}
