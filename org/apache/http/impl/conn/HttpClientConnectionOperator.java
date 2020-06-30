package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
class HttpClientConnectionOperator {
   static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
   private final Log log = LogFactory.getLog(this.getClass());
   private final Lookup socketFactoryRegistry;
   private final SchemePortResolver schemePortResolver;
   private final DnsResolver dnsResolver;

   HttpClientConnectionOperator(Lookup socketFactoryRegistry, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
      Args.notNull(socketFactoryRegistry, "Socket factory registry");
      this.socketFactoryRegistry = socketFactoryRegistry;
      this.schemePortResolver = (SchemePortResolver)(schemePortResolver != null?schemePortResolver:DefaultSchemePortResolver.INSTANCE);
      this.dnsResolver = (DnsResolver)(dnsResolver != null?dnsResolver:SystemDefaultDnsResolver.INSTANCE);
   }

   private Lookup getSocketFactoryRegistry(HttpContext context) {
      Lookup<ConnectionSocketFactory> reg = (Lookup)context.getAttribute("http.socket-factory-registry");
      if(reg == null) {
         reg = this.socketFactoryRegistry;
      }

      return reg;
   }

   public void connect(ManagedHttpClientConnection conn, HttpHost host, InetSocketAddress localAddress, int connectTimeout, SocketConfig socketConfig, HttpContext context) throws IOException {
      Lookup<ConnectionSocketFactory> registry = this.getSocketFactoryRegistry(context);
      ConnectionSocketFactory sf = (ConnectionSocketFactory)registry.lookup(host.getSchemeName());
      if(sf == null) {
         throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
      } else {
         InetAddress[] addresses = this.dnsResolver.resolve(host.getHostName());
         int port = this.schemePortResolver.resolve(host);

         for(int i = 0; i < addresses.length; ++i) {
            InetAddress address = addresses[i];
            boolean last = i == addresses.length - 1;
            Socket sock = sf.createSocket(context);
            sock.setReuseAddress(socketConfig.isSoReuseAddress());
            conn.bind(sock);
            InetSocketAddress remoteAddress = new InetSocketAddress(address, port);
            if(this.log.isDebugEnabled()) {
               this.log.debug("Connecting to " + remoteAddress);
            }

            try {
               sock.setSoTimeout(socketConfig.getSoTimeout());
               sock = sf.connectSocket(connectTimeout, sock, host, remoteAddress, localAddress, context);
               sock.setTcpNoDelay(socketConfig.isTcpNoDelay());
               sock.setKeepAlive(socketConfig.isSoKeepAlive());
               int linger = socketConfig.getSoLinger();
               if(linger >= 0) {
                  sock.setSoLinger(linger > 0, linger);
               }

               conn.bind(sock);
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Connection established " + conn);
               }

               return;
            } catch (SocketTimeoutException var18) {
               if(last) {
                  throw new ConnectTimeoutException(var18, host, addresses);
               }
            } catch (ConnectException var19) {
               if(last) {
                  String msg = var19.getMessage();
                  if("Connection timed out".equals(msg)) {
                     throw new ConnectTimeoutException(var19, host, addresses);
                  }

                  throw new HttpHostConnectException(var19, host, addresses);
               }
            }

            if(this.log.isDebugEnabled()) {
               this.log.debug("Connect to " + remoteAddress + " timed out. " + "Connection will be retried using another IP address");
            }
         }

      }
   }

   public void upgrade(ManagedHttpClientConnection conn, HttpHost host, HttpContext context) throws IOException {
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      Lookup<ConnectionSocketFactory> registry = this.getSocketFactoryRegistry(clientContext);
      ConnectionSocketFactory sf = (ConnectionSocketFactory)registry.lookup(host.getSchemeName());
      if(sf == null) {
         throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
      } else if(!(sf instanceof LayeredConnectionSocketFactory)) {
         throw new UnsupportedSchemeException(host.getSchemeName() + " protocol does not support connection upgrade");
      } else {
         LayeredConnectionSocketFactory lsf = (LayeredConnectionSocketFactory)sf;
         Socket sock = conn.getSocket();
         int port = this.schemePortResolver.resolve(host);
         sock = lsf.createLayeredSocket(sock, host.getHostName(), port, context);
         conn.bind(sock);
      }
   }
}
