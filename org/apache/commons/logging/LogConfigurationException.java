package org.apache.commons.logging;

public class LogConfigurationException extends RuntimeException {
   private static final long serialVersionUID = 8486587136871052495L;
   protected Throwable cause;

   public LogConfigurationException() {
      this.cause = null;
   }

   public LogConfigurationException(String message) {
      super(message);
      this.cause = null;
   }

   public LogConfigurationException(Throwable cause) {
      this(cause == null?null:cause.toString(), cause);
   }

   public LogConfigurationException(String message, Throwable cause) {
      super(message + " (Caused by " + cause + ")");
      this.cause = null;
      this.cause = cause;
   }

   public Throwable getCause() {
      return this.cause;
   }
}
