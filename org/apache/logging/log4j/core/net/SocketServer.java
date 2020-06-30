package org.apache.logging.log4j.core.net;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractServer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.XMLConfiguration;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

public class SocketServer extends AbstractServer implements Runnable {
   private final Logger logger;
   private static final int MAX_PORT = 65534;
   private volatile boolean isActive = true;
   private final ServerSocket server;
   private final ConcurrentMap handlers = new ConcurrentHashMap();

   public SocketServer(int port) throws IOException {
      this.server = new ServerSocket(port);
      this.logger = LogManager.getLogger(this.getClass().getName() + '.' + port);
   }

   public static void main(String[] args) throws Exception {
      if(args.length >= 1 && args.length <= 2) {
         int port = Integer.parseInt(args[0]);
         if(port > 0 && port < '\ufffe') {
            if(args.length == 2 && args[1].length() > 0) {
               ConfigurationFactory.setConfigurationFactory(new SocketServer.ServerConfigurationFactory(args[1]));
            }

            SocketServer sserver = new SocketServer(port);
            Thread server = new Thread(sserver);
            server.start();
            Charset enc = Charset.defaultCharset();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, enc));

            while(true) {
               String line = reader.readLine();
               if(line == null || line.equalsIgnoreCase("Quit") || line.equalsIgnoreCase("Stop") || line.equalsIgnoreCase("Exit")) {
                  break;
               }
            }

            sserver.shutdown();
            server.join();
         } else {
            System.err.println("Invalid port number");
            printUsage();
         }
      } else {
         System.err.println("Incorrect number of arguments");
         printUsage();
      }
   }

   private static void printUsage() {
      System.out.println("Usage: ServerSocket port configFilePath");
   }

   public void shutdown() {
      this.isActive = false;
      Thread.currentThread().interrupt();
   }

   public void run() {
      while(this.isActive) {
         try {
            Socket clientSocket = this.server.accept();
            clientSocket.setSoLinger(true, 0);
            SocketServer.SocketHandler handler = new SocketServer.SocketHandler(clientSocket);
            this.handlers.put(Long.valueOf(handler.getId()), handler);
            handler.start();
         } catch (IOException var5) {
            System.out.println("Exception encountered on accept. Ignoring. Stack Trace :");
            var5.printStackTrace();
         }
      }

      for(Entry<Long, SocketServer.SocketHandler> entry : this.handlers.entrySet()) {
         SocketServer.SocketHandler handler = (SocketServer.SocketHandler)entry.getValue();
         handler.shutdown();

         try {
            handler.join();
         } catch (InterruptedException var6) {
            ;
         }
      }

   }

   private static class ServerConfigurationFactory extends XMLConfigurationFactory {
      private final String path;

      public ServerConfigurationFactory(String path) {
         this.path = path;
      }

      public Configuration getConfiguration(String name, URI configLocation) {
         if(this.path != null && this.path.length() > 0) {
            File file = null;
            ConfigurationFactory.ConfigurationSource source = null;

            try {
               file = new File(this.path);
               FileInputStream is = new FileInputStream(file);
               source = new ConfigurationFactory.ConfigurationSource(is, file);
            } catch (FileNotFoundException var9) {
               ;
            }

            if(source == null) {
               try {
                  URL url = new URL(this.path);
                  source = new ConfigurationFactory.ConfigurationSource(url.openStream(), this.path);
               } catch (MalformedURLException var7) {
                  ;
               } catch (IOException var8) {
                  ;
               }
            }

            try {
               if(source != null) {
                  return new XMLConfiguration(source);
               }
            } catch (Exception var6) {
               ;
            }

            System.err.println("Unable to process configuration at " + this.path + ", using default.");
         }

         return super.getConfiguration(name, configLocation);
      }
   }

   private class SocketHandler extends Thread {
      private final ObjectInputStream ois;
      private boolean shutdown = false;

      public SocketHandler(Socket socket) throws IOException {
         this.ois = new ObjectInputStream(socket.getInputStream());
      }

      public void shutdown() {
         this.shutdown = true;
         this.interrupt();
      }

      public void run() {
         boolean closed = false;

         try {
            try {
               while(!this.shutdown) {
                  LogEvent event = (LogEvent)this.ois.readObject();
                  if(event != null) {
                     SocketServer.this.log(event);
                  }
               }
            } catch (EOFException var11) {
               closed = true;
            } catch (OptionalDataException var12) {
               SocketServer.this.logger.error((String)("OptionalDataException eof=" + var12.eof + " length=" + var12.length), (Throwable)var12);
            } catch (ClassNotFoundException var13) {
               SocketServer.this.logger.error((String)"Unable to locate LogEvent class", (Throwable)var13);
            } catch (IOException var14) {
               SocketServer.this.logger.error((String)"IOException encountered while reading from socket", (Throwable)var14);
            }

            if(!closed) {
               try {
                  this.ois.close();
               } catch (Exception var10) {
                  ;
               }
            }
         } finally {
            SocketServer.this.handlers.remove(Long.valueOf(this.getId()));
         }

      }
   }
}
