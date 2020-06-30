package org.apache.logging.log4j.core.config;

import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.BaseConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.util.PropertiesUtil;

public class DefaultConfiguration extends BaseConfiguration {
   public static final String DEFAULT_NAME = "Default";
   public static final String DEFAULT_LEVEL = "org.apache.logging.log4j.level";

   public DefaultConfiguration() {
      this.setName("Default");
      Layout<? extends Serializable> layout = PatternLayout.createLayout("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n", (Configuration)null, (RegexReplacement)null, (String)null, (String)null);
      Appender appender = ConsoleAppender.createAppender(layout, (Filter)null, "SYSTEM_OUT", "Console", "false", "true");
      appender.start();
      this.addAppender(appender);
      LoggerConfig root = this.getRootLogger();
      root.addAppender(appender, (Level)null, (Filter)null);
      String levelName = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.level");
      Level level = levelName != null && Level.valueOf(levelName) != null?Level.valueOf(levelName):Level.ERROR;
      root.setLevel(level);
   }

   protected void doConfigure() {
   }
}
