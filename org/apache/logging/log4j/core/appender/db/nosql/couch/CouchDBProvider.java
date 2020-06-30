package org.apache.logging.log4j.core.appender.db.nosql.couch;

import java.lang.reflect.Method;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;
import org.apache.logging.log4j.core.appender.db.nosql.couch.CouchDBConnection;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.NameUtil;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.status.StatusLogger;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

@Plugin(
   name = "CouchDb",
   category = "Core",
   printObject = true
)
public final class CouchDBProvider implements NoSQLProvider {
   private static final int HTTP = 80;
   private static final int HTTPS = 443;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final CouchDbClient client;
   private final String description;

   private CouchDBProvider(CouchDbClient client, String description) {
      this.client = client;
      this.description = "couchDb{ " + description + " }";
   }

   public CouchDBConnection getConnection() {
      return new CouchDBConnection(this.client);
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static CouchDBProvider createNoSQLProvider(@PluginAttribute("databaseName") String databaseName, @PluginAttribute("protocol") String protocol, @PluginAttribute("server") String server, @PluginAttribute("port") String port, @PluginAttribute("username") String username, @PluginAttribute("password") String password, @PluginAttribute("factoryClassName") String factoryClassName, @PluginAttribute("factoryMethodName") String factoryMethodName) {
      CouchDbClient client;
      String description;
      if(factoryClassName != null && factoryClassName.length() > 0 && factoryMethodName != null && factoryMethodName.length() > 0) {
         try {
            Class<?> factoryClass = Class.forName(factoryClassName);
            Method method = factoryClass.getMethod(factoryMethodName, new Class[0]);
            Object object = method.invoke((Object)null, new Object[0]);
            if(object instanceof CouchDbClient) {
               client = (CouchDbClient)object;
               description = "uri=" + client.getDBUri();
            } else {
               if(!(object instanceof CouchDbProperties)) {
                  if(object == null) {
                     LOGGER.error("The factory method [{}.{}()] returned null.", new Object[]{factoryClassName, factoryMethodName});
                     return null;
                  }

                  LOGGER.error("The factory method [{}.{}()] returned an unsupported type [{}].", new Object[]{factoryClassName, factoryMethodName, object.getClass().getName()});
                  return null;
               }

               CouchDbProperties properties = (CouchDbProperties)object;
               client = new CouchDbClient(properties);
               description = "uri=" + client.getDBUri() + ", username=" + properties.getUsername() + ", passwordHash=" + NameUtil.md5(password + CouchDBProvider.class.getName()) + ", maxConnections=" + properties.getMaxConnections() + ", connectionTimeout=" + properties.getConnectionTimeout() + ", socketTimeout=" + properties.getSocketTimeout();
            }
         } catch (ClassNotFoundException var14) {
            LOGGER.error("The factory class [{}] could not be loaded.", new Object[]{factoryClassName, var14});
            return null;
         } catch (NoSuchMethodException var15) {
            LOGGER.error("The factory class [{}] does not have a no-arg method named [{}].", new Object[]{factoryClassName, factoryMethodName, var15});
            return null;
         } catch (Exception var16) {
            LOGGER.error("The factory method [{}.{}()] could not be invoked.", new Object[]{factoryClassName, factoryMethodName, var16});
            return null;
         }
      } else {
         if(databaseName == null || databaseName.length() <= 0) {
            LOGGER.error("No factory method was provided so the database name is required.");
            return null;
         }

         if(protocol != null && protocol.length() > 0) {
            protocol = protocol.toLowerCase();
            if(!protocol.equals("http") && !protocol.equals("https")) {
               LOGGER.error("Only protocols [http] and [https] are supported, [{}] specified.", new Object[]{protocol});
               return null;
            }
         } else {
            protocol = "http";
            LOGGER.warn("No protocol specified, using default port [http].");
         }

         int portInt = AbstractAppender.parseInt(port, protocol.equals("https")?443:80);
         if(Strings.isEmpty(server)) {
            server = "localhost";
            LOGGER.warn("No server specified, using default server localhost.");
         }

         if(Strings.isEmpty(username) || Strings.isEmpty(password)) {
            LOGGER.error("You must provide a username and password for the CouchDB provider.");
            return null;
         }

         client = new CouchDbClient(databaseName, false, protocol, server, portInt, username, password);
         description = "uri=" + client.getDBUri() + ", username=" + username + ", passwordHash=" + NameUtil.md5(password + CouchDBProvider.class.getName());
      }

      return new CouchDBProvider(client, description);
   }
}
