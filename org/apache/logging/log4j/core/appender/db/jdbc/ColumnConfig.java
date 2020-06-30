package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "Column",
   category = "Core",
   printObject = true
)
public final class ColumnConfig {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String columnName;
   private final PatternLayout layout;
   private final String literalValue;
   private final boolean eventTimestamp;
   private final boolean unicode;
   private final boolean clob;

   private ColumnConfig(String columnName, PatternLayout layout, String literalValue, boolean eventDate, boolean unicode, boolean clob) {
      this.columnName = columnName;
      this.layout = layout;
      this.literalValue = literalValue;
      this.eventTimestamp = eventDate;
      this.unicode = unicode;
      this.clob = clob;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public PatternLayout getLayout() {
      return this.layout;
   }

   public String getLiteralValue() {
      return this.literalValue;
   }

   public boolean isEventTimestamp() {
      return this.eventTimestamp;
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   public boolean isClob() {
      return this.clob;
   }

   public String toString() {
      return "{ name=" + this.columnName + ", layout=" + this.layout + ", literal=" + this.literalValue + ", timestamp=" + this.eventTimestamp + " }";
   }

   @PluginFactory
   public static ColumnConfig createColumnConfig(@PluginConfiguration Configuration config, @PluginAttribute("name") String name, @PluginAttribute("pattern") String pattern, @PluginAttribute("literal") String literalValue, @PluginAttribute("isEventTimestamp") String eventTimestamp, @PluginAttribute("isUnicode") String unicode, @PluginAttribute("isClob") String clob) {
      if(Strings.isEmpty(name)) {
         LOGGER.error("The column config is not valid because it does not contain a column name.");
         return null;
      } else {
         boolean isPattern = Strings.isNotEmpty(pattern);
         boolean isLiteralValue = Strings.isNotEmpty(literalValue);
         boolean isEventTimestamp = Boolean.parseBoolean(eventTimestamp);
         boolean isUnicode = Booleans.parseBoolean(unicode, true);
         boolean isClob = Boolean.parseBoolean(clob);
         if((!isPattern || !isLiteralValue) && (!isPattern || !isEventTimestamp) && (!isLiteralValue || !isEventTimestamp)) {
            if(isEventTimestamp) {
               return new ColumnConfig(name, (PatternLayout)null, (String)null, true, false, false);
            } else if(isLiteralValue) {
               return new ColumnConfig(name, (PatternLayout)null, literalValue, false, false, false);
            } else if(isPattern) {
               return new ColumnConfig(name, PatternLayout.createLayout(pattern, config, (RegexReplacement)null, (String)null, "false"), (String)null, false, isUnicode, isClob);
            } else {
               LOGGER.error("To configure a column you must specify a pattern or literal or set isEventDate to true.");
               return null;
            }
         } else {
            LOGGER.error("The pattern, literal, and isEventTimestamp attributes are mutually exclusive.");
            return null;
         }
      }
   }
}
