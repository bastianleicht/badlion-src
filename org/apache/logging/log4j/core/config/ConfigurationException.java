package org.apache.logging.log4j.core.config;

public class ConfigurationException extends RuntimeException {
   private static final long serialVersionUID = -2413951820300775294L;

   public ConfigurationException(String message) {
      super(message);
   }

   public ConfigurationException(String message, Throwable cause) {
      super(message, cause);
   }
}
