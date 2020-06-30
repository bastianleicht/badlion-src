package org.apache.logging.log4j;

import java.net.URI;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;

public class LogManager {
   private static LoggerContextFactory factory;
   private static final String FACTORY_PROPERTY_NAME = "log4j2.loggerContextFactory";
   private static final Logger LOGGER = StatusLogger.getLogger();
   public static final String ROOT_LOGGER_NAME = "";

   private static String getClassName(int depth) {
      return (new Throwable()).getStackTrace()[depth].getClassName();
   }

   public static LoggerContext getContext() {
      return factory.getContext(LogManager.class.getName(), (ClassLoader)null, true);
   }

   public static LoggerContext getContext(boolean currentContext) {
      return factory.getContext(LogManager.class.getName(), (ClassLoader)null, currentContext);
   }

   public static LoggerContext getContext(ClassLoader loader, boolean currentContext) {
      return factory.getContext(LogManager.class.getName(), loader, currentContext);
   }

   public static LoggerContext getContext(ClassLoader loader, boolean currentContext, URI configLocation) {
      return factory.getContext(LogManager.class.getName(), loader, currentContext, configLocation);
   }

   protected static LoggerContext getContext(String fqcn, boolean currentContext) {
      return factory.getContext(fqcn, (ClassLoader)null, currentContext);
   }

   protected static LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      return factory.getContext(fqcn, loader, currentContext);
   }

   public static LoggerContextFactory getFactory() {
      return factory;
   }

   public static Logger getFormatterLogger(Class clazz) {
      return getLogger((String)(clazz != null?clazz.getName():getClassName(2)), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   public static Logger getFormatterLogger(Object value) {
      return getLogger((String)(value != null?value.getClass().getName():getClassName(2)), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   public static Logger getFormatterLogger(String name) {
      return getLogger((String)(name != null?name:getClassName(2)), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   public static Logger getLogger() {
      return getLogger(getClassName(2));
   }

   public static Logger getLogger(Class clazz) {
      return getLogger(clazz != null?clazz.getName():getClassName(2));
   }

   public static Logger getLogger(Class clazz, MessageFactory messageFactory) {
      return getLogger(clazz != null?clazz.getName():getClassName(2), messageFactory);
   }

   public static Logger getLogger(MessageFactory messageFactory) {
      return getLogger(getClassName(2), messageFactory);
   }

   public static Logger getLogger(Object value) {
      return getLogger(value != null?value.getClass().getName():getClassName(2));
   }

   public static Logger getLogger(Object value, MessageFactory messageFactory) {
      return getLogger(value != null?value.getClass().getName():getClassName(2), messageFactory);
   }

   public static Logger getLogger(String name) {
      String actualName = name != null?name:getClassName(2);
      return factory.getContext(LogManager.class.getName(), (ClassLoader)null, false).getLogger(actualName);
   }

   public static Logger getLogger(String name, MessageFactory messageFactory) {
      String actualName = name != null?name:getClassName(2);
      return factory.getContext(LogManager.class.getName(), (ClassLoader)null, false).getLogger(actualName, messageFactory);
   }

   protected static Logger getLogger(String fqcn, String name) {
      return factory.getContext(fqcn, (ClassLoader)null, false).getLogger(name);
   }

   public static Logger getRootLogger() {
      return getLogger("");
   }

   static {
      PropertiesUtil managerProps = PropertiesUtil.getProperties();
      String factoryClass = managerProps.getStringProperty("log4j2.loggerContextFactory");
      ClassLoader cl = ProviderUtil.findClassLoader();
      if(factoryClass != null) {
         try {
            Class<?> clazz = cl.loadClass(factoryClass);
            if(LoggerContextFactory.class.isAssignableFrom(clazz)) {
               factory = (LoggerContextFactory)clazz.newInstance();
            }
         } catch (ClassNotFoundException var11) {
            LOGGER.error("Unable to locate configured LoggerContextFactory {}", new Object[]{factoryClass});
         } catch (Exception var12) {
            LOGGER.error("Unable to create configured LoggerContextFactory {}", new Object[]{factoryClass, var12});
         }
      }

      if(factory == null) {
         SortedMap<Integer, LoggerContextFactory> factories = new TreeMap();
         if(ProviderUtil.hasProviders()) {
            Iterator<Provider> providers = ProviderUtil.getProviders();

            while(providers.hasNext()) {
               Provider provider = (Provider)providers.next();
               String className = provider.getClassName();
               if(className != null) {
                  try {
                     Class<?> clazz = cl.loadClass(className);
                     if(LoggerContextFactory.class.isAssignableFrom(clazz)) {
                        factories.put(provider.getPriority(), (LoggerContextFactory)clazz.newInstance());
                     } else {
                        LOGGER.error(className + " does not implement " + LoggerContextFactory.class.getName());
                     }
                  } catch (ClassNotFoundException var8) {
                     LOGGER.error((String)("Unable to locate class " + className + " specified in " + provider.getURL().toString()), (Throwable)var8);
                  } catch (IllegalAccessException var9) {
                     LOGGER.error((String)("Unable to create class " + className + " specified in " + provider.getURL().toString()), (Throwable)var9);
                  } catch (Exception var10) {
                     LOGGER.error((String)("Unable to create class " + className + " specified in " + provider.getURL().toString()), (Throwable)var10);
                     var10.printStackTrace();
                  }
               }
            }

            if(factories.size() == 0) {
               LOGGER.error("Unable to locate a logging implementation, using SimpleLogger");
               factory = new SimpleLoggerContextFactory();
            } else {
               StringBuilder sb = new StringBuilder("Multiple logging implementations found: \n");

               for(Entry<Integer, LoggerContextFactory> entry : factories.entrySet()) {
                  sb.append("Factory: ").append(((LoggerContextFactory)entry.getValue()).getClass().getName());
                  sb.append(", Weighting: ").append(entry.getKey()).append("\n");
               }

               factory = (LoggerContextFactory)factories.get(factories.lastKey());
               sb.append("Using factory: ").append(factory.getClass().getName());
               LOGGER.warn(sb.toString());
            }
         } else {
            LOGGER.error("Unable to locate a logging implementation, using SimpleLogger");
            factory = new SimpleLoggerContextFactory();
         }
      }

   }
}
