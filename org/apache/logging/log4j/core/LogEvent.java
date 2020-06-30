package org.apache.logging.log4j.core;

import java.io.Serializable;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;

public interface LogEvent extends Serializable {
   Level getLevel();

   String getLoggerName();

   StackTraceElement getSource();

   Message getMessage();

   Marker getMarker();

   String getThreadName();

   long getMillis();

   Throwable getThrown();

   Map getContextMap();

   ThreadContext.ContextStack getContextStack();

   String getFQCN();

   boolean isIncludeLocation();

   void setIncludeLocation(boolean var1);

   boolean isEndOfBatch();

   void setEndOfBatch(boolean var1);
}
