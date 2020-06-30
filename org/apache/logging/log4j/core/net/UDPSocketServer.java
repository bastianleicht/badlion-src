package org.apache.logging.log4j.core.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractServer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.XMLConfiguration;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

public class UDPSocketServer extends AbstractServer implements Runnable {
   private final Logger logger;
   private static final int MAX_PORT = 65534;
   private volatile boolean isActive = true;
   private final DatagramSocket server;
   private final int maxBufferSize = 67584;

   public UDPSocketServer(int port) throws IOException {
      this.server = new DatagramSocket(port);
      this.logger = LogManager.getLogger(this.getClass().getName() + '.' + port);
   }

   public static void main(String[] args) throws Exception {
      if(args.length >= 1 && args.length <= 2) {
         int port = Integer.parseInt(args[0]);
         if(port > 0 && port < '\ufffe') {
            if(args.length == 2 && args[1].length() > 0) {
               ConfigurationFactory.setConfigurationFactory(new UDPSocketServer.ServerConfigurationFactory(args[1]));
            }

            UDPSocketServer sserver = new UDPSocketServer(port);
            Thread server = new Thread(sserver);
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

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
            byte[] buf = new byte[67584];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            this.server.receive(packet);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
            LogEvent event = (LogEvent)ois.readObject();
            if(event != null) {
               this.log(event);
            }
         } catch (OptionalDataException var5) {
            this.logger.error((String)("OptionalDataException eof=" + var5.eof + " length=" + var5.length), (Throwable)var5);
         } catch (ClassNotFoundException var6) {
            this.logger.error((String)"Unable to locate LogEvent class", (Throwable)var6);
         } catch (EOFException var7) {
            this.logger.info("EOF encountered");
         } catch (IOException var8) {
            this.logger.error((String)"Exception encountered on accept. Ignoring. Stack Trace :", (Throwable)var8);
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
}
