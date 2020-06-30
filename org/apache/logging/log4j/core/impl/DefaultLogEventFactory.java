package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.message.Message;

public class DefaultLogEventFactory implements LogEventFactory {
   public LogEvent createEvent(String loggerName, Marker marker, String fqcn, Level level, Message data, List properties, Throwable t) {
      return new Log4jLogEvent(loggerName, marker, fqcn, level, data, properties, t);
   }
}
