package org.apache.logging.log4j.core.config;

import java.util.Map;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.ConfigurationMonitor;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.filter.Filterable;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;

public interface Configuration extends Filterable {
   String CONTEXT_PROPERTIES = "ContextProperties";

   String getName();

   LoggerConfig getLoggerConfig(String var1);

   Map getAppenders();

   Map getLoggers();

   void addLoggerAppender(Logger var1, Appender var2);

   void addLoggerFilter(Logger var1, Filter var2);

   void setLoggerAdditive(Logger var1, boolean var2);

   Map getProperties();

   void start();

   void stop();

   void addListener(ConfigurationListener var1);

   void removeListener(ConfigurationListener var1);

   StrSubstitutor getStrSubstitutor();

   void createConfiguration(Node var1, LogEvent var2);

   Object getComponent(String var1);

   void addComponent(String var1, Object var2);

   void setConfigurationMonitor(ConfigurationMonitor var1);

   ConfigurationMonitor getConfigurationMonitor();

   void setAdvertiser(Advertiser var1);

   Advertiser getAdvertiser();

   boolean isShutdownHookEnabled();
}
