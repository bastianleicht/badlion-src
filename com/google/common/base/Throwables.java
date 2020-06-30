package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class Throwables {
   public static void propagateIfInstanceOf(@Nullable Throwable throwable, Class declaredType) throws Throwable {
      if(throwable != null && declaredType.isInstance(throwable)) {
         throw (Throwable)declaredType.cast(throwable);
      }
   }

   public static void propagateIfPossible(@Nullable Throwable throwable) {
      propagateIfInstanceOf(throwable, Error.class);
      propagateIfInstanceOf(throwable, RuntimeException.class);
   }

   public static void propagateIfPossible(@Nullable Throwable throwable, Class declaredType) throws Throwable {
      propagateIfInstanceOf(throwable, declaredType);
      propagateIfPossible(throwable);
   }

   public static void propagateIfPossible(@Nullable Throwable throwable, Class declaredType1, Class declaredType2) throws Throwable, Throwable {
      Preconditions.checkNotNull(declaredType2);
      propagateIfInstanceOf(throwable, declaredType1);
      propagateIfPossible(throwable, declaredType2);
   }

   public static RuntimeException propagate(Throwable throwable) {
      propagateIfPossible((Throwable)Preconditions.checkNotNull(throwable));
      throw new RuntimeException(throwable);
   }

   public static Throwable getRootCause(Throwable throwable) {
      Throwable cause;
      while((cause = throwable.getCause()) != null) {
         throwable = cause;
      }

      return throwable;
   }

   @Beta
   public static List getCausalChain(Throwable throwable) {
      Preconditions.checkNotNull(throwable);

      List<Throwable> causes;
      for(causes = new ArrayList(4); throwable != null; throwable = throwable.getCause()) {
         causes.add(throwable);
      }

      return Collections.unmodifiableList(causes);
   }

   public static String getStackTraceAsString(Throwable throwable) {
      StringWriter stringWriter = new StringWriter();
      throwable.printStackTrace(new PrintWriter(stringWriter));
      return stringWriter.toString();
   }
}
