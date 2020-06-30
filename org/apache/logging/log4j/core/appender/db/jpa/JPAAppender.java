package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.jpa.AbstractLogEventWrapperEntity;
import org.apache.logging.log4j.core.appender.db.jpa.JPADatabaseManager;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Strings;

@Plugin(
   name = "JPA",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class JPAAppender extends AbstractDatabaseAppender {
   private final String description = this.getName() + "{ manager=" + this.getManager() + " }";

   private JPAAppender(String name, Filter filter, boolean ignoreExceptions, JPADatabaseManager manager) {
      super(name, filter, ignoreExceptions, manager);
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static JPAAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("Filter") Filter filter, @PluginAttribute("bufferSize") String bufferSize, @PluginAttribute("entityClassName") String entityClassName, @PluginAttribute("persistenceUnitName") String persistenceUnitName) {
      if(!Strings.isEmpty(entityClassName) && !Strings.isEmpty(persistenceUnitName)) {
         int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

         try {
            Class<? extends AbstractLogEventWrapperEntity> entityClass = Class.forName(entityClassName);
            if(!AbstractLogEventWrapperEntity.class.isAssignableFrom(entityClass)) {
               LOGGER.error("Entity class [{}] does not extend AbstractLogEventWrapperEntity.", new Object[]{entityClassName});
               return null;
            } else {
               try {
                  entityClass.getConstructor(new Class[0]);
               } catch (NoSuchMethodException var12) {
                  LOGGER.error("Entity class [{}] does not have a no-arg constructor. The JPA provider will reject it.", new Object[]{entityClassName});
                  return null;
               }

               Constructor<? extends AbstractLogEventWrapperEntity> entityConstructor = entityClass.getConstructor(new Class[]{LogEvent.class});
               String managerName = "jpaManager{ description=" + name + ", bufferSize=" + bufferSizeInt + ", persistenceUnitName=" + persistenceUnitName + ", entityClass=" + entityClass.getName() + "}";
               JPADatabaseManager manager = JPADatabaseManager.getJPADatabaseManager(managerName, bufferSizeInt, entityClass, entityConstructor, persistenceUnitName);
               return manager == null?null:new JPAAppender(name, filter, ignoreExceptions, manager);
            }
         } catch (ClassNotFoundException var13) {
            LOGGER.error("Could not load entity class [{}].", new Object[]{entityClassName, var13});
            return null;
         } catch (NoSuchMethodException var14) {
            LOGGER.error("Entity class [{}] does not have a constructor with a single argument of type LogEvent.", new Object[]{entityClassName});
            return null;
         }
      } else {
         LOGGER.error("Attributes entityClassName and persistenceUnitName are required for JPA Appender.");
         return null;
      }
   }
}
