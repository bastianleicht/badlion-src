package org.apache.commons.logging;

public interface Log {
   boolean isDebugEnabled();

   boolean isErrorEnabled();

   boolean isFatalEnabled();

   boolean isInfoEnabled();

   boolean isTraceEnabled();

   boolean isWarnEnabled();

   void trace(Object var1);

   void trace(Object var1, Throwable var2);

   void debug(Object var1);

   void debug(Object var1, Throwable var2);

   void info(Object var1);

   void info(Object var1, Throwable var2);

   void warn(Object var1);

   void warn(Object var1, Throwable var2);

   void error(Object var1);

   void error(Object var1, Throwable var2);

   void fatal(Object var1);

   void fatal(Object var1, Throwable var2);
}
