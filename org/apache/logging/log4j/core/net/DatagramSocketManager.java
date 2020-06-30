package org.apache.logging.log4j.core.net;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.DatagramOutputStream;

public class DatagramSocketManager extends AbstractSocketManager {
   private static final DatagramSocketManager.DatagramSocketManagerFactory FACTORY = new DatagramSocketManager.DatagramSocketManagerFactory();

   protected DatagramSocketManager(String name, OutputStream os, InetAddress address, String host, int port, Layout layout) {
      super(name, os, address, host, port, layout);
   }

   public static DatagramSocketManager getSocketManager(String host, int port, Layout layout) {
      if(Strings.isEmpty(host)) {
         throw new IllegalArgumentException("A host name is required");
      } else if(port <= 0) {
         throw new IllegalArgumentException("A port value is required");
      } else {
         return (DatagramSocketManager)getManager("UDP:" + host + ":" + port, new DatagramSocketManager.FactoryData(host, port, layout), FACTORY);
      }
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap(super.getContentFormat());
      result.put("protocol", "udp");
      result.put("direction", "out");
      return result;
   }

   private static class DatagramSocketManagerFactory implements ManagerFactory {
      private DatagramSocketManagerFactory() {
      }

      public DatagramSocketManager createManager(String name, DatagramSocketManager.FactoryData data) {
         OutputStream os = new DatagramOutputStream(data.host, data.port, data.layout.getHeader(), data.layout.getFooter());

         InetAddress address;
         try {
            address = InetAddress.getByName(data.host);
         } catch (UnknownHostException var6) {
            DatagramSocketManager.LOGGER.error((String)("Could not find address of " + data.host), (Throwable)var6);
            return null;
         }

         return new DatagramSocketManager(name, os, address, data.host, data.port, data.layout);
      }
   }

   private static class FactoryData {
      private final String host;
      private final int port;
      private final Layout layout;

      public FactoryData(String host, int port, Layout layout) {
         this.host = host;
         this.port = port;
         this.layout = layout;
      }
   }
}
