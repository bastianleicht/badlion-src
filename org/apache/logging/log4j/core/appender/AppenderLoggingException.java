package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.LoggingException;

public class AppenderLoggingException extends LoggingException {
   private static final long serialVersionUID = 6545990597472958303L;

   public AppenderLoggingException(String message) {
      super(message);
   }

   public AppenderLoggingException(String message, Throwable cause) {
      super(message, cause);
   }

   public AppenderLoggingException(Throwable cause) {
      super(cause);
   }
}
