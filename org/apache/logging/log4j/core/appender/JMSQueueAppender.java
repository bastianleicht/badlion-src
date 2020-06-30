package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.net.JMSQueueManager;

@Plugin(
   name = "JMSQueue",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class JMSQueueAppender extends AbstractAppender {
   private final JMSQueueManager manager;

   private JMSQueueAppender(String name, Filter filter, Layout layout, JMSQueueManager manager, boolean ignoreExceptions) {
      super(name, filter, layout, ignoreExceptions);
      this.manager = manager;
   }

   public void append(LogEvent event) {
      try {
         this.manager.send(this.getLayout().toSerializable(event));
      } catch (Exception var3) {
         throw new AppenderLoggingException(var3);
      }
   }

   @PluginFactory
   public static JMSQueueAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("factoryName") String factoryName, @PluginAttribute("providerURL") String providerURL, @PluginAttribute("urlPkgPrefixes") String urlPkgPrefixes, @PluginAttribute("securityPrincipalName") String securityPrincipalName, @PluginAttribute("securityCredentials") String securityCredentials, @PluginAttribute("factoryBindingName") String factoryBindingName, @PluginAttribute("queueBindingName") String queueBindingName, @PluginAttribute("userName") String userName, @PluginAttribute("password") String password, @PluginElement("Layout") Layout layout, @PluginElement("Filter") Filter filter, @PluginAttribute("ignoreExceptions") String ignore) {
      if(name == null) {
         LOGGER.error("No name provided for JMSQueueAppender");
         return null;
      } else {
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
         JMSQueueManager manager = JMSQueueManager.getJMSQueueManager(factoryName, providerURL, urlPkgPrefixes, securityPrincipalName, securityCredentials, factoryBindingName, queueBindingName, userName, password);
         if(manager == null) {
            return null;
         } else {
            if(layout == null) {
               layout = SerializedLayout.createLayout();
            }

            return new JMSQueueAppender(name, filter, (Layout)layout, manager, ignoreExceptions);
         }
      }
   }
}
