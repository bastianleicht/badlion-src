package org.apache.logging.log4j;

public class LoggingException extends RuntimeException {
   private static final long serialVersionUID = 6366395965071580537L;

   public LoggingException(String message) {
      super(message);
   }

   public LoggingException(String message, Throwable cause) {
      super(message, cause);
   }

   public LoggingException(Throwable cause) {
      super(cause);
   }
}
