package org.apache.http.util;

import java.util.Collection;
import org.apache.http.util.TextUtils;

public class Args {
   public static void check(boolean expression, String message) {
      if(!expression) {
         throw new IllegalArgumentException(message);
      }
   }

   public static void check(boolean expression, String message, Object... args) {
      if(!expression) {
         throw new IllegalArgumentException(String.format(message, args));
      }
   }

   public static Object notNull(Object argument, String name) {
      if(argument == null) {
         throw new IllegalArgumentException(name + " may not be null");
      } else {
         return argument;
      }
   }

   public static CharSequence notEmpty(CharSequence argument, String name) {
      if(argument == null) {
         throw new IllegalArgumentException(name + " may not be null");
      } else if(TextUtils.isEmpty(argument)) {
         throw new IllegalArgumentException(name + " may not be empty");
      } else {
         return argument;
      }
   }

   public static CharSequence notBlank(CharSequence argument, String name) {
      if(argument == null) {
         throw new IllegalArgumentException(name + " may not be null");
      } else if(TextUtils.isBlank(argument)) {
         throw new IllegalArgumentException(name + " may not be blank");
      } else {
         return argument;
      }
   }

   public static Collection notEmpty(Collection argument, String name) {
      if(argument == null) {
         throw new IllegalArgumentException(name + " may not be null");
      } else if(argument.isEmpty()) {
         throw new IllegalArgumentException(name + " may not be empty");
      } else {
         return argument;
      }
   }

   public static int positive(int n, String name) {
      if(n <= 0) {
         throw new IllegalArgumentException(name + " may not be negative or zero");
      } else {
         return n;
      }
   }

   public static long positive(long n, String name) {
      if(n <= 0L) {
         throw new IllegalArgumentException(name + " may not be negative or zero");
      } else {
         return n;
      }
   }

   public static int notNegative(int n, String name) {
      if(n < 0) {
         throw new IllegalArgumentException(name + " may not be negative");
      } else {
         return n;
      }
   }

   public static long notNegative(long n, String name) {
      if(n < 0L) {
         throw new IllegalArgumentException(name + " may not be negative");
      } else {
         return n;
      }
   }
}
