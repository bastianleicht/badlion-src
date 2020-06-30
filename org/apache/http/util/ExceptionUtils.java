package org.apache.http.util;

import java.lang.reflect.Method;

/** @deprecated */
@Deprecated
public final class ExceptionUtils {
   private static final Method INIT_CAUSE_METHOD = getInitCauseMethod();

   private static Method getInitCauseMethod() {
      try {
         Class<?>[] paramsClasses = new Class[]{Throwable.class};
         return Throwable.class.getMethod("initCause", paramsClasses);
      } catch (NoSuchMethodException var1) {
         return null;
      }
   }

   public static void initCause(Throwable throwable, Throwable cause) {
      if(INIT_CAUSE_METHOD != null) {
         try {
            INIT_CAUSE_METHOD.invoke(throwable, new Object[]{cause});
         } catch (Exception var3) {
            ;
         }
      }

   }
}
