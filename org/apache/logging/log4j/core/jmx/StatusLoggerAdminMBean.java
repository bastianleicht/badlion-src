package org.apache.logging.log4j.core.jmx;

import java.util.List;

public interface StatusLoggerAdminMBean {
   String NAME = "org.apache.logging.log4j2:type=StatusLogger";
   String NOTIF_TYPE_DATA = "com.apache.logging.log4j.core.jmx.statuslogger.data";
   String NOTIF_TYPE_MESSAGE = "com.apache.logging.log4j.core.jmx.statuslogger.message";

   List getStatusData();

   String[] getStatusDataHistory();

   String getLevel();

   void setLevel(String var1);
}
