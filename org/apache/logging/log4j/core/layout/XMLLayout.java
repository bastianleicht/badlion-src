package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Charsets;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.helpers.Throwables;
import org.apache.logging.log4j.core.helpers.Transform;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;

@Plugin(
   name = "XMLLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public class XMLLayout extends AbstractStringLayout {
   private static final String XML_NAMESPACE = "http://logging.apache.org/log4j/2.0/events";
   private static final String ROOT_TAG = "Events";
   private static final int DEFAULT_SIZE = 256;
   private static final String DEFAULT_EOL = "\r\n";
   private static final String COMPACT_EOL = "";
   private static final String DEFAULT_INDENT = "  ";
   private static final String COMPACT_INDENT = "";
   private static final String DEFAULT_NS_PREFIX = "log4j";
   private static final String[] FORMATS = new String[]{"xml"};
   private final boolean locationInfo;
   private final boolean properties;
   private final boolean complete;
   private final String namespacePrefix;
   private final String eol;
   private final String indent1;
   private final String indent2;
   private final String indent3;

   protected XMLLayout(boolean locationInfo, boolean properties, boolean complete, boolean compact, String nsPrefix, Charset charset) {
      super(charset);
      this.locationInfo = locationInfo;
      this.properties = properties;
      this.complete = complete;
      this.eol = compact?"":"\r\n";
      this.indent1 = compact?"":"  ";
      this.indent2 = this.indent1 + this.indent1;
      this.indent3 = this.indent2 + this.indent1;
      this.namespacePrefix = (Strings.isEmpty(nsPrefix)?"log4j":nsPrefix) + ":";
   }

   public String toSerializable(LogEvent event) {
      StringBuilder buf = new StringBuilder(256);
      buf.append(this.indent1);
      buf.append('<');
      if(!this.complete) {
         buf.append(this.namespacePrefix);
      }

      buf.append("Event logger=\"");
      String name = event.getLoggerName();
      if(name.isEmpty()) {
         name = "root";
      }

      buf.append(Transform.escapeHtmlTags(name));
      buf.append("\" timestamp=\"");
      buf.append(event.getMillis());
      buf.append("\" level=\"");
      buf.append(Transform.escapeHtmlTags(String.valueOf(event.getLevel())));
      buf.append("\" thread=\"");
      buf.append(Transform.escapeHtmlTags(event.getThreadName()));
      buf.append("\">");
      buf.append(this.eol);
      Message msg = event.getMessage();
      if(msg != null) {
         boolean xmlSupported = false;
         if(msg instanceof MultiformatMessage) {
            String[] formats = ((MultiformatMessage)msg).getFormats();

            for(String format : formats) {
               if(format.equalsIgnoreCase("XML")) {
                  xmlSupported = true;
                  break;
               }
            }
         }

         buf.append(this.indent2);
         buf.append('<');
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Message>");
         if(xmlSupported) {
            buf.append(((MultiformatMessage)msg).getFormattedMessage(FORMATS));
         } else {
            buf.append("<![CDATA[");
            Transform.appendEscapingCDATA(buf, event.getMessage().getFormattedMessage());
            buf.append("]]>");
         }

         buf.append("</");
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Message>");
         buf.append(this.eol);
      }

      if(event.getContextStack().getDepth() > 0) {
         buf.append(this.indent2);
         buf.append('<');
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("NDC><![CDATA[");
         Transform.appendEscapingCDATA(buf, event.getContextStack().toString());
         buf.append("]]></");
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("NDC>");
         buf.append(this.eol);
      }

      Throwable throwable = event.getThrown();
      if(throwable != null) {
         List<String> s = Throwables.toStringList(throwable);
         buf.append(this.indent2);
         buf.append('<');
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Throwable><![CDATA[");

         for(String str : s) {
            Transform.appendEscapingCDATA(buf, str);
            buf.append(this.eol);
         }

         buf.append("]]></");
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Throwable>");
         buf.append(this.eol);
      }

      if(this.locationInfo) {
         StackTraceElement element = event.getSource();
         buf.append(this.indent2);
         buf.append('<');
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("LocationInfo class=\"");
         buf.append(Transform.escapeHtmlTags(element.getClassName()));
         buf.append("\" method=\"");
         buf.append(Transform.escapeHtmlTags(element.getMethodName()));
         buf.append("\" file=\"");
         buf.append(Transform.escapeHtmlTags(element.getFileName()));
         buf.append("\" line=\"");
         buf.append(element.getLineNumber());
         buf.append("\"/>");
         buf.append(this.eol);
      }

      if(this.properties && event.getContextMap().size() > 0) {
         buf.append(this.indent2);
         buf.append('<');
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Properties>");
         buf.append(this.eol);

         for(Entry<String, String> entry : event.getContextMap().entrySet()) {
            buf.append(this.indent3);
            buf.append('<');
            if(!this.complete) {
               buf.append(this.namespacePrefix);
            }

            buf.append("Data name=\"");
            buf.append(Transform.escapeHtmlTags((String)entry.getKey()));
            buf.append("\" value=\"");
            buf.append(Transform.escapeHtmlTags(String.valueOf(entry.getValue())));
            buf.append("\"/>");
            buf.append(this.eol);
         }

         buf.append(this.indent2);
         buf.append("</");
         if(!this.complete) {
            buf.append(this.namespacePrefix);
         }

         buf.append("Properties>");
         buf.append(this.eol);
      }

      buf.append(this.indent1);
      buf.append("</");
      if(!this.complete) {
         buf.append(this.namespacePrefix);
      }

      buf.append("Event>");
      buf.append(this.eol);
      return buf.toString();
   }

   public byte[] getHeader() {
      if(!this.complete) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("<?xml version=\"1.0\" encoding=\"");
         buf.append(this.getCharset().name());
         buf.append("\"?>");
         buf.append(this.eol);
         buf.append('<');
         buf.append("Events");
         buf.append(" xmlns=\"http://logging.apache.org/log4j/2.0/events\">");
         buf.append(this.eol);
         return buf.toString().getBytes(this.getCharset());
      }
   }

   public byte[] getFooter() {
      return !this.complete?null:("</Events>" + this.eol).getBytes(this.getCharset());
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap();
      result.put("xsd", "log4j-events.xsd");
      result.put("version", "2.0");
      return result;
   }

   public String getContentType() {
      return "text/xml; charset=" + this.getCharset();
   }

   @PluginFactory
   public static XMLLayout createLayout(@PluginAttribute("locationInfo") String locationInfo, @PluginAttribute("properties") String properties, @PluginAttribute("complete") String completeStr, @PluginAttribute("compact") String compactStr, @PluginAttribute("namespacePrefix") String namespacePrefix, @PluginAttribute("charset") String charsetName) {
      Charset charset = Charsets.getSupportedCharset(charsetName, Charsets.UTF_8);
      boolean info = Boolean.parseBoolean(locationInfo);
      boolean props = Boolean.parseBoolean(properties);
      boolean complete = Boolean.parseBoolean(completeStr);
      boolean compact = Boolean.parseBoolean(compactStr);
      return new XMLLayout(info, props, complete, compact, namespacePrefix, charset);
   }
}
