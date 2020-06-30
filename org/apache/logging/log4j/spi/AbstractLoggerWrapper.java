package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;

public class AbstractLoggerWrapper extends AbstractLogger {
   protected final AbstractLogger logger;

   public AbstractLoggerWrapper(AbstractLogger logger, String name, MessageFactory messageFactory) {
      super(name, messageFactory);
      this.logger = logger;
   }

   public void log(Marker marker, String fqcn, Level level, Message data, Throwable t) {
      this.logger.log(marker, fqcn, level, data, t);
   }

   public boolean isEnabled(Level level, Marker marker, String data) {
      return this.logger.isEnabled(level, marker, data);
   }

   public boolean isEnabled(Level level, Marker marker, String data, Throwable t) {
      return this.logger.isEnabled(level, marker, data, t);
   }

   public boolean isEnabled(Level level, Marker marker, String data, Object... p1) {
      return this.logger.isEnabled(level, marker, data, p1);
   }

   public boolean isEnabled(Level level, Marker marker, Object data, Throwable t) {
      return this.logger.isEnabled(level, marker, data, t);
   }

   public boolean isEnabled(Level level, Marker marker, Message data, Throwable t) {
      return this.logger.isEnabled(level, marker, data, t);
   }
}
