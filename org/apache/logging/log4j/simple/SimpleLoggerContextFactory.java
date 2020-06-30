package org.apache.logging.log4j.simple;

import java.net.URI;
import org.apache.logging.log4j.simple.SimpleLoggerContext;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class SimpleLoggerContextFactory implements LoggerContextFactory {
   private static LoggerContext context = new SimpleLoggerContext();

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      return context;
   }

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation) {
      return context;
   }

   public void removeContext(LoggerContext context) {
   }
}
