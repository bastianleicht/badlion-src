package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;

public interface TriggeringPolicy {
   void initialize(RollingFileManager var1);

   boolean isTriggeringEvent(LogEvent var1);
}
