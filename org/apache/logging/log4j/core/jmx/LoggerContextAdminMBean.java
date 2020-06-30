package org.apache.logging.log4j.core.jmx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface LoggerContextAdminMBean {
   String PATTERN = "org.apache.logging.log4j2:type=LoggerContext,ctx=%s";
   String NOTIF_TYPE_RECONFIGURED = "com.apache.logging.log4j.core.jmx.config.reconfigured";

   String getStatus();

   String getName();

   String getConfigLocationURI();

   void setConfigLocationURI(String var1) throws URISyntaxException, IOException;

   String getConfigText() throws IOException;

   String getConfigText(String var1) throws IOException;

   void setConfigText(String var1, String var2);

   String getConfigName();

   String getConfigClassName();

   String getConfigFilter();

   String getConfigMonitorClassName();

   Map getConfigProperties();
}
