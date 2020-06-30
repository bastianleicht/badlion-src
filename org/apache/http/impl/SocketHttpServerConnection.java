package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.apache.http.HttpInetConnection;
import org.apache.http.impl.AbstractHttpServerConnection;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
public class SocketHttpServerConnection extends AbstractHttpServerConnection implements HttpInetConnection {
   private volatile boolean open;
   private volatile Socket socket = null;

   protected void assertNotOpen() {
      Asserts.check(!this.open, "Connection is already open");
   }

   protected void assertOpen() {
      Asserts.check(this.open, "Connection is not open");
   }

   protected SessionInputBuffer createSessionInputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
      return new SocketInputBuffer(socket, buffersize, params);
   }

   protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
      return new SocketOutputBuffer(socket, buffersize, params);
   }

   protected void bind(Socket socket, HttpParams params) throws IOException {
      Args.notNull(socket, "Socket");
      Args.notNull(params, "HTTP parameters");
      this.socket = socket;
      int buffersize = params.getIntParameter("http.socket.buffer-size", -1);
      this.init(this.createSessionInputBuffer(socket, buffersize, params), this.createSessionOutputBuffer(socket, buffersize, params), params);
      this.open = true;
   }

   protected Socket getSocket() {
      return this.socket;
   }

   public boolean isOpen() {
      return this.open;
   }

   public InetAddress getLocalAddress() {
      return this.socket != null?this.socket.getLocalAddress():null;
   }

   public int getLocalPort() {
      return this.socket != null?this.socket.getLocalPort():-1;
   }

   public InetAddress getRemoteAddress() {
      return this.socket != null?this.socket.getInetAddress():null;
   }

   public int getRemotePort() {
      return this.socket != null?this.socket.getPort():-1;
   }

   public void setSocketTimeout(int timeout) {
      this.assertOpen();
      if(this.socket != null) {
         try {
            this.socket.setSoTimeout(timeout);
         } catch (SocketException var3) {
            ;
         }
      }

   }

   public int getSocketTimeout() {
      if(this.socket != null) {
         try {
            return this.socket.getSoTimeout();
         } catch (SocketException var2) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public void shutdown() throws IOException {
      this.open = false;
      Socket tmpsocket = this.socket;
      if(tmpsocket != null) {
         tmpsocket.close();
      }

   }

   public void close() throws IOException {
      if(this.open) {
         this.open = false;
         this.open = false;
         Socket sock = this.socket;

         try {
            this.doFlush();

            try {
               try {
                  sock.shutdownOutput();
               } catch (IOException var9) {
                  ;
               }

               try {
                  sock.shutdownInput();
               } catch (IOException var8) {
                  ;
               }
            } catch (UnsupportedOperationException var10) {
               ;
            }
         } finally {
            sock.close();
         }

      }
   }

   private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
      if(socketAddress instanceof InetSocketAddress) {
         InetSocketAddress addr = (InetSocketAddress)socketAddress;
         buffer.append(addr.getAddress() != null?addr.getAddress().getHostAddress():addr.getAddress()).append(':').append(addr.getPort());
      } else {
         buffer.append(socketAddress);
      }

   }

   public String toString() {
      if(this.socket != null) {
         StringBuilder buffer = new StringBuilder();
         SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
         SocketAddress localAddress = this.socket.getLocalSocketAddress();
         if(remoteAddress != null && localAddress != null) {
            formatAddress(buffer, localAddress);
            buffer.append("<->");
            formatAddress(buffer, remoteAddress);
         }

         return buffer.toString();
      } else {
         return super.toString();
      }
   }
}
