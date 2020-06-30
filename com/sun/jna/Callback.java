package com.sun.jna;

import java.util.Arrays;
import java.util.Collection;

public interface Callback {
   String METHOD_NAME = "callback";
   Collection FORBIDDEN_NAMES = Arrays.asList(new String[]{"hashCode", "equals", "toString"});

   public interface UncaughtExceptionHandler {
      void uncaughtException(Callback var1, Throwable var2);
   }
}
