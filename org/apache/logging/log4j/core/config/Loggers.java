package org.apache.logging.log4j.core.config;

import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class Loggers {
   private final ConcurrentMap map;
   private final LoggerConfig root;

   public Loggers(ConcurrentMap map, LoggerConfig root) {
      this.map = map;
      this.root = root;
   }

   public ConcurrentMap getMap() {
      return this.map;
   }

   public LoggerConfig getRoot() {
      return this.root;
   }
}
