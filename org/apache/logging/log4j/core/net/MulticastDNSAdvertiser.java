package org.apache.logging.log4j.core.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.Integers;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "multicastdns",
   category = "Core",
   elementType = "advertiser",
   printObject = false
)
public class MulticastDNSAdvertiser implements Advertiser {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static Object jmDNS = initializeJMDNS();
   private static Class jmDNSClass;
   private static Class serviceInfoClass;

   public Object advertise(Map properties) {
      Map<String, String> truncatedProperties = new HashMap();

      for(Entry<String, String> entry : properties.entrySet()) {
         if(((String)entry.getKey()).length() <= 255 && ((String)entry.getValue()).length() <= 255) {
            truncatedProperties.put(entry.getKey(), entry.getValue());
         }
      }

      String protocol = (String)truncatedProperties.get("protocol");
      String zone = "._log4j._" + (protocol != null?protocol:"tcp") + ".local.";
      String portString = (String)truncatedProperties.get("port");
      int port = Integers.parseInt(portString, 4555);
      String name = (String)truncatedProperties.get("name");
      if(jmDNS != null) {
         boolean isVersion3 = false;

         try {
            jmDNSClass.getMethod("create", (Class[])null);
            isVersion3 = true;
         } catch (NoSuchMethodException var14) {
            ;
         }

         Object serviceInfo;
         if(isVersion3) {
            serviceInfo = this.buildServiceInfoVersion3(zone, port, name, truncatedProperties);
         } else {
            serviceInfo = this.buildServiceInfoVersion1(zone, port, name, truncatedProperties);
         }

         try {
            Method method = jmDNSClass.getMethod("registerService", new Class[]{serviceInfoClass});
            method.invoke(jmDNS, new Object[]{serviceInfo});
         } catch (IllegalAccessException var11) {
            LOGGER.warn((String)"Unable to invoke registerService method", (Throwable)var11);
         } catch (NoSuchMethodException var12) {
            LOGGER.warn((String)"No registerService method", (Throwable)var12);
         } catch (InvocationTargetException var13) {
            LOGGER.warn((String)"Unable to invoke registerService method", (Throwable)var13);
         }

         return serviceInfo;
      } else {
         LOGGER.warn("JMDNS not available - will not advertise ZeroConf support");
         return null;
      }
   }

   public void unadvertise(Object serviceInfo) {
      if(jmDNS != null) {
         try {
            Method method = jmDNSClass.getMethod("unregisterService", new Class[]{serviceInfoClass});
            method.invoke(jmDNS, new Object[]{serviceInfo});
         } catch (IllegalAccessException var3) {
            LOGGER.warn((String)"Unable to invoke unregisterService method", (Throwable)var3);
         } catch (NoSuchMethodException var4) {
            LOGGER.warn((String)"No unregisterService method", (Throwable)var4);
         } catch (InvocationTargetException var5) {
            LOGGER.warn((String)"Unable to invoke unregisterService method", (Throwable)var5);
         }
      }

   }

   private static Object createJmDNSVersion1() {
      try {
         return jmDNSClass.newInstance();
      } catch (InstantiationException var1) {
         LOGGER.warn((String)"Unable to instantiate JMDNS", (Throwable)var1);
      } catch (IllegalAccessException var2) {
         LOGGER.warn((String)"Unable to instantiate JMDNS", (Throwable)var2);
      }

      return null;
   }

   private static Object createJmDNSVersion3() {
      try {
         Method jmDNSCreateMethod = jmDNSClass.getMethod("create", (Class[])null);
         return jmDNSCreateMethod.invoke((Object)null, (Object[])null);
      } catch (IllegalAccessException var1) {
         LOGGER.warn((String)"Unable to instantiate jmdns class", (Throwable)var1);
      } catch (NoSuchMethodException var2) {
         LOGGER.warn((String)"Unable to access constructor", (Throwable)var2);
      } catch (InvocationTargetException var3) {
         LOGGER.warn((String)"Unable to call constructor", (Throwable)var3);
      }

      return null;
   }

   private Object buildServiceInfoVersion1(String zone, int port, String name, Map properties) {
      Hashtable<String, String> hashtableProperties = new Hashtable(properties);

      try {
         Class<?>[] args = new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Hashtable.class};
         Constructor<?> constructor = serviceInfoClass.getConstructor(args);
         Object[] values = new Object[]{zone, name, Integer.valueOf(port), Integer.valueOf(0), Integer.valueOf(0), hashtableProperties};
         return constructor.newInstance(values);
      } catch (IllegalAccessException var9) {
         LOGGER.warn((String)"Unable to construct ServiceInfo instance", (Throwable)var9);
      } catch (NoSuchMethodException var10) {
         LOGGER.warn((String)"Unable to get ServiceInfo constructor", (Throwable)var10);
      } catch (InstantiationException var11) {
         LOGGER.warn((String)"Unable to construct ServiceInfo instance", (Throwable)var11);
      } catch (InvocationTargetException var12) {
         LOGGER.warn((String)"Unable to construct ServiceInfo instance", (Throwable)var12);
      }

      return null;
   }

   private Object buildServiceInfoVersion3(String zone, int port, String name, Map properties) {
      try {
         Class<?>[] args = new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Map.class};
         Method serviceInfoCreateMethod = serviceInfoClass.getMethod("create", args);
         Object[] values = new Object[]{zone, name, Integer.valueOf(port), Integer.valueOf(0), Integer.valueOf(0), properties};
         return serviceInfoCreateMethod.invoke((Object)null, values);
      } catch (IllegalAccessException var8) {
         LOGGER.warn((String)"Unable to invoke create method", (Throwable)var8);
      } catch (NoSuchMethodException var9) {
         LOGGER.warn((String)"Unable to find create method", (Throwable)var9);
      } catch (InvocationTargetException var10) {
         LOGGER.warn((String)"Unable to invoke create method", (Throwable)var10);
      }

      return null;
   }

   private static Object initializeJMDNS() {
      try {
         jmDNSClass = Class.forName("javax.jmdns.JmDNS");
         serviceInfoClass = Class.forName("javax.jmdns.ServiceInfo");
         boolean isVersion3 = false;

         try {
            jmDNSClass.getMethod("create", (Class[])null);
            isVersion3 = true;
         } catch (NoSuchMethodException var2) {
            ;
         }

         if(isVersion3) {
            return createJmDNSVersion3();
         }

         return createJmDNSVersion1();
      } catch (ClassNotFoundException var3) {
         LOGGER.warn((String)"JmDNS or serviceInfo class not found", (Throwable)var3);
      } catch (ExceptionInInitializerError var4) {
         LOGGER.warn((String)"JmDNS or serviceInfo class not found", (Throwable)var4);
      }

      return null;
   }
}
