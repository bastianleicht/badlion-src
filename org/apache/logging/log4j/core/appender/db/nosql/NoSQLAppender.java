package org.apache.logging.log4j.core.appender.db.nosql;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLDatabaseManager;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;

@Plugin(
   name = "NoSql",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class NoSQLAppender extends AbstractDatabaseAppender {
   private final String description = this.getName() + "{ manager=" + this.getManager() + " }";

   private NoSQLAppender(String name, Filter filter, boolean ignoreExceptions, NoSQLDatabaseManager manager) {
      super(name, filter, ignoreExceptions, manager);
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static NoSQLAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("Filter") Filter filter, @PluginAttribute("bufferSize") String bufferSize, @PluginElement("NoSqlProvider") NoSQLProvider provider) {
      if(provider == null) {
         LOGGER.error("NoSQL provider not specified for appender [{}].", new Object[]{name});
         return null;
      } else {
         int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
         String managerName = "noSqlManager{ description=" + name + ", bufferSize=" + bufferSizeInt + ", provider=" + provider + " }";
         NoSQLDatabaseManager<?> manager = NoSQLDatabaseManager.getNoSQLDatabaseManager(managerName, bufferSizeInt, provider);
         return manager == null?null:new NoSQLAppender(name, filter, ignoreExceptions, manager);
      }
   }
}
