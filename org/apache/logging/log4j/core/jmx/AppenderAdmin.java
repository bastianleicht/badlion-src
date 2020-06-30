package org.apache.logging.log4j.core.jmx;

import javax.management.ObjectName;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.helpers.Assert;
import org.apache.logging.log4j.core.jmx.AppenderAdminMBean;
import org.apache.logging.log4j.core.jmx.Server;

public class AppenderAdmin implements AppenderAdminMBean {
   private final String contextName;
   private final Appender appender;
   private final ObjectName objectName;

   public AppenderAdmin(String contextName, Appender appender) {
      this.contextName = (String)Assert.isNotNull(contextName, "contextName");
      this.appender = (Appender)Assert.isNotNull(appender, "appender");

      try {
         String ctxName = Server.escape(this.contextName);
         String configName = Server.escape(appender.getName());
         String name = String.format("org.apache.logging.log4j2:type=LoggerContext,ctx=%s,sub=Appender,name=%s", new Object[]{ctxName, configName});
         this.objectName = new ObjectName(name);
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   public String getName() {
      return this.appender.getName();
   }

   public String getLayout() {
      return String.valueOf(this.appender.getLayout());
   }

   public boolean isExceptionSuppressed() {
      return this.appender.ignoreExceptions();
   }

   public String getErrorHandler() {
      return String.valueOf(this.appender.getHandler());
   }
}
