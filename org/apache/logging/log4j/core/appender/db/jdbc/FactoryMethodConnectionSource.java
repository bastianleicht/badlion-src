package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ConnectionFactory",
   category = "Core",
   elementType = "connectionSource",
   printObject = true
)
public final class FactoryMethodConnectionSource implements ConnectionSource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final DataSource dataSource;
   private final String description;

   private FactoryMethodConnectionSource(DataSource dataSource, String className, String methodName, String returnType) {
      this.dataSource = dataSource;
      this.description = "factory{ public static " + returnType + " " + className + "." + methodName + "() }";
   }

   public Connection getConnection() throws SQLException {
      return this.dataSource.getConnection();
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static FactoryMethodConnectionSource createConnectionSource(@PluginAttribute("class") String className, @PluginAttribute("method") String methodName) {
      if(!Strings.isEmpty(className) && !Strings.isEmpty(methodName)) {
         final Method method;
         try {
            Class<?> factoryClass = Class.forName(className);
            method = factoryClass.getMethod(methodName, new Class[0]);
         } catch (Exception var8) {
            LOGGER.error((String)var8.toString(), (Throwable)var8);
            return null;
         }

         Class<?> returnType = method.getReturnType();
         String returnTypeString = returnType.getName();
         DataSource dataSource;
         if(returnType == DataSource.class) {
            try {
               dataSource = (DataSource)method.invoke((Object)null, new Object[0]);
               returnTypeString = returnTypeString + "[" + dataSource + "]";
            } catch (Exception var7) {
               LOGGER.error((String)var7.toString(), (Throwable)var7);
               return null;
            }
         } else {
            if(returnType != Connection.class) {
               LOGGER.error("Method [{}.{}()] returns unsupported type [{}].", new Object[]{className, methodName, returnType.getName()});
               return null;
            }

            dataSource = new DataSource() {
               public Connection getConnection() throws SQLException {
                  try {
                     return (Connection)method.invoke((Object)null, new Object[0]);
                  } catch (Exception var2) {
                     throw new SQLException("Failed to obtain connection from factory method.", var2);
                  }
               }

               public Connection getConnection(String username, String password) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public int getLoginTimeout() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public PrintWriter getLogWriter() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public java.util.logging.Logger getParentLogger() {
                  throw new UnsupportedOperationException();
               }

               public boolean isWrapperFor(Class iface) throws SQLException {
                  return false;
               }

               public void setLoginTimeout(int seconds) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public void setLogWriter(PrintWriter out) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public Object unwrap(Class iface) throws SQLException {
                  return null;
               }
            };
         }

         return new FactoryMethodConnectionSource(dataSource, className, methodName, returnTypeString);
      } else {
         LOGGER.error("No class name or method name specified for the connection factory method.");
         return null;
      }
   }
}
