package org.apache.logging.log4j.core.jmx;

public interface AppenderAdminMBean {
   String PATTERN = "org.apache.logging.log4j2:type=LoggerContext,ctx=%s,sub=Appender,name=%s";

   String getName();

   String getLayout();

   boolean isExceptionSuppressed();

   String getErrorHandler();
}
