package org.apache.logging.log4j.core.appender.db.nosql.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;
import org.apache.logging.log4j.core.appender.db.nosql.mongo.MongoDBConnection;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.NameUtil;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "MongoDb",
   category = "Core",
   printObject = true
)
public final class MongoDBProvider implements NoSQLProvider {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String collectionName;
   private final DB database;
   private final String description;
   private final WriteConcern writeConcern;

   private MongoDBProvider(DB database, WriteConcern writeConcern, String collectionName, String description) {
      this.database = database;
      this.writeConcern = writeConcern;
      this.collectionName = collectionName;
      this.description = "mongoDb{ " + description + " }";
   }

   public MongoDBConnection getConnection() {
      return new MongoDBConnection(this.database, this.writeConcern, this.collectionName);
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static MongoDBProvider createNoSQLProvider(@PluginAttribute("collectionName") String collectionName, @PluginAttribute("writeConcernConstant") String writeConcernConstant, @PluginAttribute("writeConcernConstantClass") String writeConcernConstantClassName, @PluginAttribute("databaseName") String databaseName, @PluginAttribute("server") String server, @PluginAttribute("port") String port, @PluginAttribute("username") String username, @PluginAttribute("password") String password, @PluginAttribute("factoryClassName") String factoryClassName, @PluginAttribute("factoryMethodName") String factoryMethodName) {
      DB database;
      String description;
      if(factoryClassName != null && factoryClassName.length() > 0 && factoryMethodName != null && factoryMethodName.length() > 0) {
         try {
            Class<?> factoryClass = Class.forName(factoryClassName);
            Method method = factoryClass.getMethod(factoryMethodName, new Class[0]);
            Object object = method.invoke((Object)null, new Object[0]);
            if(object instanceof DB) {
               database = (DB)object;
            } else {
               if(!(object instanceof MongoClient)) {
                  if(object == null) {
                     LOGGER.error("The factory method [{}.{}()] returned null.", new Object[]{factoryClassName, factoryMethodName});
                     return null;
                  }

                  LOGGER.error("The factory method [{}.{}()] returned an unsupported type [{}].", new Object[]{factoryClassName, factoryMethodName, object.getClass().getName()});
                  return null;
               }

               if(databaseName == null || databaseName.length() <= 0) {
                  LOGGER.error("The factory method [{}.{}()] returned a MongoClient so the database name is required.", new Object[]{factoryClassName, factoryMethodName});
                  return null;
               }

               database = ((MongoClient)object).getDB(databaseName);
            }

            description = "database=" + database.getName();
            List<ServerAddress> addresses = database.getMongo().getAllAddress();
            if(addresses.size() == 1) {
               description = description + ", server=" + ((ServerAddress)addresses.get(0)).getHost() + ", port=" + ((ServerAddress)addresses.get(0)).getPort();
            } else {
               description = description + ", servers=[";

               for(ServerAddress address : addresses) {
                  description = description + " { " + address.getHost() + ", " + address.getPort() + " } ";
               }

               description = description + "]";
            }
         } catch (ClassNotFoundException var20) {
            LOGGER.error("The factory class [{}] could not be loaded.", new Object[]{factoryClassName, var20});
            return null;
         } catch (NoSuchMethodException var21) {
            LOGGER.error("The factory class [{}] does not have a no-arg method named [{}].", new Object[]{factoryClassName, factoryMethodName, var21});
            return null;
         } catch (Exception var22) {
            LOGGER.error("The factory method [{}.{}()] could not be invoked.", new Object[]{factoryClassName, factoryMethodName, var22});
            return null;
         }
      } else {
         if(databaseName == null || databaseName.length() <= 0) {
            LOGGER.error("No factory method was provided so the database name is required.");
            return null;
         }

         description = "database=" + databaseName;

         try {
            if(server != null && server.length() > 0) {
               int portInt = AbstractAppender.parseInt(port, 0);
               description = description + ", server=" + server;
               if(portInt > 0) {
                  description = description + ", port=" + portInt;
                  database = (new MongoClient(server, portInt)).getDB(databaseName);
               } else {
                  database = (new MongoClient(server)).getDB(databaseName);
               }
            } else {
               database = (new MongoClient()).getDB(databaseName);
            }
         } catch (Exception var19) {
            LOGGER.error("Failed to obtain a database instance from the MongoClient at server [{}] and port [{}].", new Object[]{server, port});
            return null;
         }
      }

      if(!database.isAuthenticated()) {
         if(username == null || username.length() <= 0 || password == null || password.length() <= 0) {
            LOGGER.error("The database is not already authenticated so you must supply a username and password for the MongoDB provider.");
            return null;
         }

         description = description + ", username=" + username + ", passwordHash=" + NameUtil.md5(password + MongoDBProvider.class.getName());
         MongoDBConnection.authenticate(database, username, password);
      }

      WriteConcern writeConcern;
      if(writeConcernConstant != null && writeConcernConstant.length() > 0) {
         if(writeConcernConstantClassName != null && writeConcernConstantClassName.length() > 0) {
            try {
               Class<?> writeConcernConstantClass = Class.forName(writeConcernConstantClassName);
               Field field = writeConcernConstantClass.getField(writeConcernConstant);
               writeConcern = (WriteConcern)field.get((Object)null);
            } catch (Exception var18) {
               LOGGER.error("Write concern constant [{}.{}] not found, using default.", new Object[]{writeConcernConstantClassName, writeConcernConstant});
               writeConcern = WriteConcern.ACKNOWLEDGED;
            }
         } else {
            writeConcern = WriteConcern.valueOf(writeConcernConstant);
            if(writeConcern == null) {
               LOGGER.warn("Write concern constant [{}] not found, using default.", new Object[]{writeConcernConstant});
               writeConcern = WriteConcern.ACKNOWLEDGED;
            }
         }
      } else {
         writeConcern = WriteConcern.ACKNOWLEDGED;
      }

      return new MongoDBProvider(database, writeConcern, collectionName, description);
   }
}
