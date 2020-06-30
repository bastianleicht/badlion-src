package org.apache.logging.log4j.core.config;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.status.StatusLogger;

public final class Configurator {
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();

   public static LoggerContext initialize(String name, ClassLoader loader, String configLocation) {
      return initialize(name, loader, (String)configLocation, (Object)null);
   }

   public static LoggerContext initialize(String name, ClassLoader loader, String configLocation, Object externalContext) {
      try {
         URI uri = configLocation == null?null:new URI(configLocation);
         return initialize(name, loader, uri, externalContext);
      } catch (URISyntaxException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public static LoggerContext initialize(String name, String configLocation) {
      return initialize(name, (ClassLoader)null, (String)configLocation);
   }

   public static LoggerContext initialize(String name, ClassLoader loader, URI configLocation) {
      return initialize(name, loader, (URI)configLocation, (Object)null);
   }

   public static LoggerContext initialize(String name, ClassLoader loader, URI configLocation, Object externalContext) {
      try {
         org.apache.logging.log4j.spi.LoggerContext context = LogManager.getContext(loader, false, configLocation);
         if(context instanceof LoggerContext) {
            LoggerContext ctx = (LoggerContext)context;
            ContextAnchor.THREAD_CONTEXT.set(ctx);
            if(externalContext != null) {
               ctx.setExternalContext(externalContext);
            }

            Configuration config = ConfigurationFactory.getInstance().getConfiguration(name, configLocation);
            ctx.start(config);
            ContextAnchor.THREAD_CONTEXT.remove();
            return ctx;
         }

         LOGGER.error("LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j", new Object[]{context.getClass().getName(), LoggerContext.class.getName()});
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return null;
   }

   public static LoggerContext initialize(ClassLoader loader, ConfigurationFactory.ConfigurationSource source) {
      try {
         URI configLocation = null;

         try {
            configLocation = source.getLocation() == null?null:new URI(source.getLocation());
         } catch (Exception var6) {
            ;
         }

         org.apache.logging.log4j.spi.LoggerContext context = LogManager.getContext(loader, false, configLocation);
         if(context instanceof LoggerContext) {
            LoggerContext ctx = (LoggerContext)context;
            ContextAnchor.THREAD_CONTEXT.set(ctx);
            Configuration config = ConfigurationFactory.getInstance().getConfiguration(source);
            ctx.start(config);
            ContextAnchor.THREAD_CONTEXT.remove();
            return ctx;
         }

         LOGGER.error("LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j", new Object[]{context.getClass().getName(), LoggerContext.class.getName()});
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return null;
   }

   public static void shutdown(LoggerContext ctx) {
      if(ctx != null) {
         ctx.stop();
      }

   }
}
