package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.BaseConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class NullConfiguration extends BaseConfiguration {
   public static final String NULL_NAME = "Null";

   public NullConfiguration() {
      this.setName("Null");
      LoggerConfig root = this.getRootLogger();
      root.setLevel(Level.OFF);
   }
}
