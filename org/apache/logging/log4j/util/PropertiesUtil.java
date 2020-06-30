package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ProviderUtil;

public class PropertiesUtil {
   private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil("log4j2.component.properties");
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Properties props;

   public PropertiesUtil(Properties props) {
      this.props = props;
   }

   static Properties loadClose(InputStream in, Object source) {
      Properties props = new Properties();
      if(null != in) {
         try {
            props.load(in);
         } catch (IOException var12) {
            LOGGER.error((String)("Unable to read " + source), (Throwable)var12);
         } finally {
            try {
               in.close();
            } catch (IOException var11) {
               LOGGER.error((String)("Unable to close " + source), (Throwable)var11);
            }

         }
      }

      return props;
   }

   public PropertiesUtil(String propsLocn) {
      ClassLoader loader = ProviderUtil.findClassLoader();
      InputStream in = loader.getResourceAsStream(propsLocn);
      this.props = loadClose(in, propsLocn);
   }

   public static PropertiesUtil getProperties() {
      return LOG4J_PROPERTIES;
   }

   public String getStringProperty(String name) {
      String prop = null;

      try {
         prop = System.getProperty(name);
      } catch (SecurityException var4) {
         ;
      }

      return prop == null?this.props.getProperty(name):prop;
   }

   public int getIntegerProperty(String name, int defaultValue) {
      String prop = null;

      try {
         prop = System.getProperty(name);
      } catch (SecurityException var6) {
         ;
      }

      if(prop == null) {
         prop = this.props.getProperty(name);
      }

      if(prop != null) {
         try {
            return Integer.parseInt(prop);
         } catch (Exception var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public long getLongProperty(String name, long defaultValue) {
      String prop = null;

      try {
         prop = System.getProperty(name);
      } catch (SecurityException var7) {
         ;
      }

      if(prop == null) {
         prop = this.props.getProperty(name);
      }

      if(prop != null) {
         try {
            return Long.parseLong(prop);
         } catch (Exception var6) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public String getStringProperty(String name, String defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null?defaultValue:prop;
   }

   public boolean getBooleanProperty(String name) {
      return this.getBooleanProperty(name, false);
   }

   public boolean getBooleanProperty(String name, boolean defaultValue) {
      String prop = this.getStringProperty(name);
      return prop == null?defaultValue:"true".equalsIgnoreCase(prop);
   }

   public static Properties getSystemProperties() {
      try {
         return new Properties(System.getProperties());
      } catch (SecurityException var1) {
         StatusLogger.getLogger().error("Unable to access system properties.");
         return new Properties();
      }
   }
}
