package org.apache.logging.log4j.core.web;

import javax.servlet.UnavailableException;

interface Log4jWebInitializer {
   String LOG4J_CONTEXT_NAME = "log4jContextName";
   String LOG4J_CONFIG_LOCATION = "log4jConfiguration";
   String IS_LOG4J_CONTEXT_SELECTOR_NAMED = "isLog4jContextSelectorNamed";
   String INITIALIZER_ATTRIBUTE = Log4jWebInitializer.class.getName() + ".INSTANCE";

   void initialize() throws UnavailableException;

   void deinitialize();

   void setLoggerContext();

   void clearLoggerContext();
}
