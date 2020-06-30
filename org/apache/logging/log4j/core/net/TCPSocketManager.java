package org.apache.logging.log4j.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.net.AbstractSocketManager;

public class TCPSocketManager extends AbstractSocketManager {
   public static final int DEFAULT_RECONNECTION_DELAY = 30000;
   private static final int DEFAULT_PORT = 4560;
   private static final TCPSocketManager.TCPSocketManagerFactory FACTORY = new TCPSocketManager.TCPSocketManagerFactory();
   private final int reconnectionDelay;
   private TCPSocketManager.Reconnector connector = null;
   private Socket socket;
   private final boolean retry;
   private final boolean immediateFail;

   public TCPSocketManager(String name, OutputStream os, Socket sock, InetAddress addr, String host, int port, int delay, boolean immediateFail, Layout layout) {
      super(name, os, addr, host, port, layout);
      this.reconnectionDelay = delay;
      this.socket = sock;
      this.immediateFail = immediateFail;
      this.retry = delay > 0;
      if(sock == null) {
         this.connector = new TCPSocketManager.Reconnector(this);
         this.connector.setDaemon(true);
         this.connector.setPriority(1);
         this.connector.start();
      }

   }

   public static TCPSocketManager getSocketManager(String host, int port, int delay, boolean immediateFail, Layout layout) {
      if(Strings.isEmpty(host)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if(port <= 0) {
            port = 4560;
         }

         if(delay == 0) {
            delay = 30000;
         }

         return (TCPSocketManager)getManager("TCP:" + host + ":" + port, new TCPSocketManager.FactoryData(host, port, delay, immediateFail, layout), FACTORY);
      }
   }

   protected void write(byte[] bytes, int offset, int length) {
      if(this.socket == null) {
         if(this.connector != null && !this.immediateFail) {
            this.connector.latch();
         }

         if(this.socket == null) {
            String msg = "Error writing to " + this.getName() + " socket not available";
            throw new AppenderLoggingException(msg);
         }
      }

      synchronized(this) {
         try {
            this.getOutputStream().write(bytes, offset, length);
         } catch (IOException var8) {
            if(this.retry && this.connector == null) {
               this.connector = new TCPSocketManager.Reconnector(this);
               this.connector.setDaemon(true);
               this.connector.setPriority(1);
               this.connector.start();
            }

            String msg = "Error writing to " + this.getName();
            throw new AppenderLoggingException(msg, var8);
         }

      }
   }

   protected synchronized void close() {
      super.close();
      if(this.connector != null) {
         this.connector.shutdown();
         this.connector.interrupt();
         this.connector = null;
      }

   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap(super.getContentFormat());
      result.put("protocol", "tcp");
      result.put("direction", "out");
      return result;
   }

   protected Socket createSocket(InetAddress host, int port) throws IOException {
      return this.createSocket(host.getHostName(), port);
   }

   protected Socket createSocket(String host, int port) throws IOException {
      return new Socket(host, port);
   }

   private static class FactoryData {
      private final String host;
      private final int port;
      private final int delay;
      private final boolean immediateFail;
      private final Layout layout;

      public FactoryData(String host, int port, int delay, boolean immediateFail, Layout layout) {
         this.host = host;
         this.port = port;
         this.delay = delay;
         this.immediateFail = immediateFail;
         this.layout = layout;
      }
   }

   private class Reconnector extends Thread {
      private final CountDownLatch latch = new CountDownLatch(1);
      private boolean shutdown = false;
      private final Object owner;

      public Reconnector(OutputStreamManager owner) {
         this.owner = owner;
      }

      public void latch() {
         try {
            this.latch.await();
         } catch (InterruptedException var2) {
            ;
         }

      }

      public void shutdown() {
         this.shutdown = true;
      }

      public void run() {
         while(!this.shutdown) {
            try {
               sleep((long)TCPSocketManager.this.reconnectionDelay);
               Socket sock = TCPSocketManager.this.createSocket(TCPSocketManager.this.address, TCPSocketManager.this.port);
               OutputStream newOS = sock.getOutputStream();
               synchronized(this.owner) {
                  try {
                     TCPSocketManager.this.getOutputStream().close();
                  } catch (IOException var13) {
                     ;
                  }

                  TCPSocketManager.this.setOutputStream(newOS);
                  TCPSocketManager.this.socket = sock;
                  TCPSocketManager.this.connector = null;
                  this.shutdown = true;
               }

               TCPSocketManager.LOGGER.debug("Connection to " + TCPSocketManager.this.host + ":" + TCPSocketManager.this.port + " reestablished.");
            } catch (InterruptedException var15) {
               TCPSocketManager.LOGGER.debug("Reconnection interrupted.");
            } catch (ConnectException var16) {
               TCPSocketManager.LOGGER.debug(TCPSocketManager.this.host + ":" + TCPSocketManager.this.port + " refused connection");
            } catch (IOException var17) {
               TCPSocketManager.LOGGER.debug("Unable to reconnect to " + TCPSocketManager.this.host + ":" + TCPSocketManager.this.port);
            } finally {
               this.latch.countDown();
            }
         }

      }
   }

   protected static class TCPSocketManagerFactory implements ManagerFactory {
      public TCPSocketManager createManager(String name, TCPSocketManager.FactoryData data) {
         InetAddress address;
         try {
            address = InetAddress.getByName(data.host);
         } catch (UnknownHostException var6) {
            TCPSocketManager.LOGGER.error((String)("Could not find address of " + data.host), (Throwable)var6);
            return null;
         }

         try {
            Socket socket = new Socket(data.host, data.port);
            OutputStream os = socket.getOutputStream();
            return new TCPSocketManager(name, os, socket, address, data.host, data.port, data.delay, data.immediateFail, data.layout);
         } catch (IOException var7) {
            TCPSocketManager.LOGGER.error("TCPSocketManager (" + name + ") " + var7);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            return data.delay == 0?null:new TCPSocketManager(name, os, (Socket)null, address, data.host, data.port, data.delay, data.immediateFail, data.layout);
         }
      }
   }
}
