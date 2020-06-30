package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.layout.LoggerFields;
import org.apache.logging.log4j.core.layout.RFC5424Layout;
import org.apache.logging.log4j.core.layout.SyslogLayout;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.util.EnglishEnums;

@Plugin(
   name = "Syslog",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class SyslogAppender extends SocketAppender {
   protected static final String RFC5424 = "RFC5424";

   protected SyslogAppender(String name, Layout layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, AbstractSocketManager manager, Advertiser advertiser) {
      super(name, layout, filter, manager, ignoreExceptions, immediateFlush, advertiser);
   }

   @PluginFactory
   public static SyslogAppender createAppender(@PluginAttribute("host") String host, @PluginAttribute("port") String portNum, @PluginAttribute("protocol") String protocol, @PluginAttribute("reconnectionDelay") String delay, @PluginAttribute("immediateFail") String immediateFail, @PluginAttribute("name") String name, @PluginAttribute("immediateFlush") String immediateFlush, @PluginAttribute("ignoreExceptions") String ignore, @PluginAttribute("facility") String facility, @PluginAttribute("id") String id, @PluginAttribute("enterpriseNumber") String ein, @PluginAttribute("includeMDC") String includeMDC, @PluginAttribute("mdcId") String mdcId, @PluginAttribute("mdcPrefix") String mdcPrefix, @PluginAttribute("eventPrefix") String eventPrefix, @PluginAttribute("newLine") String includeNL, @PluginAttribute("newLineEscape") String escapeNL, @PluginAttribute("appName") String appName, @PluginAttribute("messageId") String msgId, @PluginAttribute("mdcExcludes") String excludes, @PluginAttribute("mdcIncludes") String includes, @PluginAttribute("mdcRequired") String required, @PluginAttribute("format") String format, @PluginElement("Filters") Filter filter, @PluginConfiguration Configuration config, @PluginAttribute("charset") String charsetName, @PluginAttribute("exceptionPattern") String exceptionPattern, @PluginElement("LoggerFields") LoggerFields[] loggerFields, @PluginAttribute("advertise") String advertise) {
      boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
      boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
      int reconnectDelay = AbstractAppender.parseInt(delay, 0);
      boolean fail = Booleans.parseBoolean(immediateFail, true);
      int port = AbstractAppender.parseInt(portNum, 0);
      boolean isAdvertise = Boolean.parseBoolean(advertise);
      Layout<? extends Serializable> layout = (Layout)("RFC5424".equalsIgnoreCase(format)?RFC5424Layout.createLayout(facility, id, ein, includeMDC, mdcId, mdcPrefix, eventPrefix, includeNL, escapeNL, appName, msgId, excludes, includes, required, exceptionPattern, "false", loggerFields, config):SyslogLayout.createLayout(facility, includeNL, escapeNL, charsetName));
      if(name == null) {
         LOGGER.error("No name provided for SyslogAppender");
         return null;
      } else {
         Protocol p = (Protocol)EnglishEnums.valueOf(Protocol.class, protocol);
         AbstractSocketManager manager = createSocketManager(p, host, port, reconnectDelay, fail, layout);
         return manager == null?null:new SyslogAppender(name, layout, filter, ignoreExceptions, isFlush, manager, isAdvertise?config.getAdvertiser():null);
      }
   }
}
