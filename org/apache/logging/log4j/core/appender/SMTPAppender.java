package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.layout.HTMLLayout;
import org.apache.logging.log4j.core.net.SMTPManager;

@Plugin(
   name = "SMTP",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class SMTPAppender extends AbstractAppender {
   private static final int DEFAULT_BUFFER_SIZE = 512;
   protected final SMTPManager manager;

   private SMTPAppender(String name, Filter filter, Layout layout, SMTPManager manager, boolean ignoreExceptions) {
      super(name, filter, layout, ignoreExceptions);
      this.manager = manager;
   }

   @PluginFactory
   public static SMTPAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("to") String to, @PluginAttribute("cc") String cc, @PluginAttribute("bcc") String bcc, @PluginAttribute("from") String from, @PluginAttribute("replyTo") String replyTo, @PluginAttribute("subject") String subject, @PluginAttribute("smtpProtocol") String smtpProtocol, @PluginAttribute("smtpHost") String smtpHost, @PluginAttribute("smtpPort") String smtpPortStr, @PluginAttribute("smtpUsername") String smtpUsername, @PluginAttribute("smtpPassword") String smtpPassword, @PluginAttribute("smtpDebug") String smtpDebug, @PluginAttribute("bufferSize") String bufferSizeStr, @PluginElement("Layout") Layout layout, @PluginElement("Filter") Filter filter, @PluginAttribute("ignoreExceptions") String ignore) {
      if(name == null) {
         LOGGER.error("No name provided for SMTPAppender");
         return null;
      } else {
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
         int smtpPort = AbstractAppender.parseInt(smtpPortStr, 0);
         boolean isSmtpDebug = Boolean.parseBoolean(smtpDebug);
         int bufferSize = bufferSizeStr == null?512:Integer.parseInt(bufferSizeStr);
         if(layout == null) {
            layout = HTMLLayout.createLayout((String)null, (String)null, (String)null, (String)null, (String)null, (String)null);
         }

         if(filter == null) {
            filter = ThresholdFilter.createFilter((String)null, (String)null, (String)null);
         }

         SMTPManager manager = SMTPManager.getSMTPManager(to, cc, bcc, from, replyTo, subject, smtpProtocol, smtpHost, smtpPort, smtpUsername, smtpPassword, isSmtpDebug, filter.toString(), bufferSize);
         return manager == null?null:new SMTPAppender(name, (Filter)filter, (Layout)layout, manager, ignoreExceptions);
      }
   }

   public boolean isFiltered(LogEvent event) {
      boolean filtered = super.isFiltered(event);
      if(filtered) {
         this.manager.add(event);
      }

      return filtered;
   }

   public void append(LogEvent event) {
      this.manager.sendEvents(this.getLayout(), event);
   }
}
