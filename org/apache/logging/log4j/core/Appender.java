package org.apache.logging.log4j.core;

import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;

public interface Appender extends LifeCycle {
   void append(LogEvent var1);

   String getName();

   Layout getLayout();

   boolean ignoreExceptions();

   ErrorHandler getHandler();

   void setHandler(ErrorHandler var1);
}
