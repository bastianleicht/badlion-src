package org.apache.logging.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

public interface Logger {
   void catching(Level var1, Throwable var2);

   void catching(Throwable var1);

   void debug(Marker var1, Message var2);

   void debug(Marker var1, Message var2, Throwable var3);

   void debug(Marker var1, Object var2);

   void debug(Marker var1, Object var2, Throwable var3);

   void debug(Marker var1, String var2);

   void debug(Marker var1, String var2, Object... var3);

   void debug(Marker var1, String var2, Throwable var3);

   void debug(Message var1);

   void debug(Message var1, Throwable var2);

   void debug(Object var1);

   void debug(Object var1, Throwable var2);

   void debug(String var1);

   void debug(String var1, Object... var2);

   void debug(String var1, Throwable var2);

   void entry();

   void entry(Object... var1);

   void error(Marker var1, Message var2);

   void error(Marker var1, Message var2, Throwable var3);

   void error(Marker var1, Object var2);

   void error(Marker var1, Object var2, Throwable var3);

   void error(Marker var1, String var2);

   void error(Marker var1, String var2, Object... var3);

   void error(Marker var1, String var2, Throwable var3);

   void error(Message var1);

   void error(Message var1, Throwable var2);

   void error(Object var1);

   void error(Object var1, Throwable var2);

   void error(String var1);

   void error(String var1, Object... var2);

   void error(String var1, Throwable var2);

   void exit();

   Object exit(Object var1);

   void fatal(Marker var1, Message var2);

   void fatal(Marker var1, Message var2, Throwable var3);

   void fatal(Marker var1, Object var2);

   void fatal(Marker var1, Object var2, Throwable var3);

   void fatal(Marker var1, String var2);

   void fatal(Marker var1, String var2, Object... var3);

   void fatal(Marker var1, String var2, Throwable var3);

   void fatal(Message var1);

   void fatal(Message var1, Throwable var2);

   void fatal(Object var1);

   void fatal(Object var1, Throwable var2);

   void fatal(String var1);

   void fatal(String var1, Object... var2);

   void fatal(String var1, Throwable var2);

   MessageFactory getMessageFactory();

   String getName();

   void info(Marker var1, Message var2);

   void info(Marker var1, Message var2, Throwable var3);

   void info(Marker var1, Object var2);

   void info(Marker var1, Object var2, Throwable var3);

   void info(Marker var1, String var2);

   void info(Marker var1, String var2, Object... var3);

   void info(Marker var1, String var2, Throwable var3);

   void info(Message var1);

   void info(Message var1, Throwable var2);

   void info(Object var1);

   void info(Object var1, Throwable var2);

   void info(String var1);

   void info(String var1, Object... var2);

   void info(String var1, Throwable var2);

   boolean isDebugEnabled();

   boolean isDebugEnabled(Marker var1);

   boolean isEnabled(Level var1);

   boolean isEnabled(Level var1, Marker var2);

   boolean isErrorEnabled();

   boolean isErrorEnabled(Marker var1);

   boolean isFatalEnabled();

   boolean isFatalEnabled(Marker var1);

   boolean isInfoEnabled();

   boolean isInfoEnabled(Marker var1);

   boolean isTraceEnabled();

   boolean isTraceEnabled(Marker var1);

   boolean isWarnEnabled();

   boolean isWarnEnabled(Marker var1);

   void log(Level var1, Marker var2, Message var3);

   void log(Level var1, Marker var2, Message var3, Throwable var4);

   void log(Level var1, Marker var2, Object var3);

   void log(Level var1, Marker var2, Object var3, Throwable var4);

   void log(Level var1, Marker var2, String var3);

   void log(Level var1, Marker var2, String var3, Object... var4);

   void log(Level var1, Marker var2, String var3, Throwable var4);

   void log(Level var1, Message var2);

   void log(Level var1, Message var2, Throwable var3);

   void log(Level var1, Object var2);

   void log(Level var1, Object var2, Throwable var3);

   void log(Level var1, String var2);

   void log(Level var1, String var2, Object... var3);

   void log(Level var1, String var2, Throwable var3);

   void printf(Level var1, Marker var2, String var3, Object... var4);

   void printf(Level var1, String var2, Object... var3);

   Throwable throwing(Level var1, Throwable var2);

   Throwable throwing(Throwable var1);

   void trace(Marker var1, Message var2);

   void trace(Marker var1, Message var2, Throwable var3);

   void trace(Marker var1, Object var2);

   void trace(Marker var1, Object var2, Throwable var3);

   void trace(Marker var1, String var2);

   void trace(Marker var1, String var2, Object... var3);

   void trace(Marker var1, String var2, Throwable var3);

   void trace(Message var1);

   void trace(Message var1, Throwable var2);

   void trace(Object var1);

   void trace(Object var1, Throwable var2);

   void trace(String var1);

   void trace(String var1, Object... var2);

   void trace(String var1, Throwable var2);

   void warn(Marker var1, Message var2);

   void warn(Marker var1, Message var2, Throwable var3);

   void warn(Marker var1, Object var2);

   void warn(Marker var1, Object var2, Throwable var3);

   void warn(Marker var1, String var2);

   void warn(Marker var1, String var2, Object... var3);

   void warn(Marker var1, String var2, Throwable var3);

   void warn(Message var1);

   void warn(Message var1, Throwable var2);

   void warn(Object var1);

   void warn(Object var1, Throwable var2);

   void warn(String var1);

   void warn(String var1, Object... var2);

   void warn(String var1, Throwable var2);
}
