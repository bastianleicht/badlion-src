package org.apache.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpMessage;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.ContentLengthOutputStream;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.NetUtils;

@NotThreadSafe
public class BHttpConnectionBase implements HttpConnection, HttpInetConnection {
   private final SessionInputBufferImpl inbuffer;
   private final SessionOutputBufferImpl outbuffer;
   private final HttpConnectionMetricsImpl connMetrics;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;
   private volatile boolean open;
   private volatile Socket socket;

   protected BHttpConnectionBase(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
      Args.positive(buffersize, "Buffer size");
      HttpTransportMetricsImpl inTransportMetrics = new HttpTransportMetricsImpl();
      HttpTransportMetricsImpl outTransportMetrics = new HttpTransportMetricsImpl();
      this.inbuffer = new SessionInputBufferImpl(inTransportMetrics, buffersize, -1, constraints != null?constraints:MessageConstraints.DEFAULT, chardecoder);
      this.outbuffer = new SessionOutputBufferImpl(outTransportMetrics, buffersize, fragmentSizeHint, charencoder);
      this.connMetrics = new HttpConnectionMetricsImpl(inTransportMetrics, outTransportMetrics);
      this.incomingContentStrategy = (ContentLengthStrategy)(incomingContentStrategy != null?incomingContentStrategy:LaxContentLengthStrategy.INSTANCE);
      this.outgoingContentStrategy = (ContentLengthStrategy)(outgoingContentStrategy != null?outgoingContentStrategy:StrictContentLengthStrategy.INSTANCE);
   }

   protected void ensureOpen() throws IOException {
      Asserts.check(this.open, "Connection is not open");
      if(!this.inbuffer.isBound()) {
         this.inbuffer.bind(this.getSocketInputStream(this.socket));
      }

      if(!this.outbuffer.isBound()) {
         this.outbuffer.bind(this.getSocketOutputStream(this.socket));
      }

   }

   protected InputStream getSocketInputStream(Socket socket) throws IOException {
      return socket.getInputStream();
   }

   protected OutputStream getSocketOutputStream(Socket socket) throws IOException {
      return socket.getOutputStream();
   }

   protected void bind(Socket socket) throws IOException {
      Args.notNull(socket, "Socket");
      this.socket = socket;
      this.open = true;
      this.inbuffer.bind((InputStream)null);
      this.outbuffer.bind((OutputStream)null);
   }

   protected SessionInputBuffer getSessionInputBuffer() {
      return this.inbuffer;
   }

   protected SessionOutputBuffer getSessionOutputBuffer() {
      return this.outbuffer;
   }

   protected void doFlush() throws IOException {
      this.outbuffer.flush();
   }

   public boolean isOpen() {
      return this.open;
   }

   protected Socket getSocket() {
      return this.socket;
   }

   protected OutputStream createOutputStream(long len, SessionOutputBuffer outbuffer) {
      return (OutputStream)(len == -2L?new ChunkedOutputStream(2048, outbuffer):(len == -1L?new IdentityOutputStream(outbuffer):new ContentLengthOutputStream(outbuffer, len)));
   }

   protected OutputStream prepareOutput(HttpMessage message) throws HttpException {
      long len = this.outgoingContentStrategy.determineLength(message);
      return this.createOutputStream(len, this.outbuffer);
   }

   protected InputStream createInputStream(long len, SessionInputBuffer inbuffer) {
      return (InputStream)(len == -2L?new ChunkedInputStream(inbuffer):(len == -1L?new IdentityInputStream(inbuffer):new ContentLengthInputStream(inbuffer, len)));
   }

   protected HttpEntity prepareInput(HttpMessage message) throws HttpException {
      BasicHttpEntity entity = new BasicHttpEntity();
      long len = this.incomingContentStrategy.determineLength(message);
      InputStream instream = this.createInputStream(len, this.inbuffer);
      if(len == -2L) {
         entity.setChunked(true);
         entity.setContentLength(-1L);
         entity.setContent(instream);
      } else if(len == -1L) {
         entity.setChunked(false);
         entity.setContentLength(-1L);
         entity.setContent(instream);
      } else {
         entity.setChunked(false);
         entity.setContentLength(len);
         entity.setContent(instream);
      }

      Header contentTypeHeader = message.getFirstHeader("Content-Type");
      if(contentTypeHeader != null) {
         entity.setContentType(contentTypeHeader);
      }

      Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
      if(contentEncodingHeader != null) {
         entity.setContentEncoding(contentEncodingHeader);
      }

      return entity;
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
         Socket sock = this.socket;

         try {
            this.inbuffer.clear();
            this.outbuffer.flush();

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

   private int fillInputBuffer(int timeout) throws IOException {
      int oldtimeout = this.socket.getSoTimeout();

      int var3;
      try {
         this.socket.setSoTimeout(timeout);
         var3 = this.inbuffer.fillBuffer();
      } finally {
         this.socket.setSoTimeout(oldtimeout);
      }

      return var3;
   }

   protected boolean awaitInput(int timeout) throws IOException {
      if(this.inbuffer.hasBufferedData()) {
         return true;
      } else {
         this.fillInputBuffer(timeout);
         return this.inbuffer.hasBufferedData();
      }
   }

   public boolean isStale() {
      if(!this.isOpen()) {
         return true;
      } else {
         try {
            int bytesRead = this.fillInputBuffer(1);
            return bytesRead < 0;
         } catch (SocketTimeoutException var2) {
            return false;
         } catch (IOException var3) {
            return true;
         }
      }
   }

   protected void incrementRequestCount() {
      this.connMetrics.incrementRequestCount();
   }

   protected void incrementResponseCount() {
      this.connMetrics.incrementResponseCount();
   }

   public HttpConnectionMetrics getMetrics() {
      return this.connMetrics;
   }

   public String toString() {
      if(this.socket != null) {
         StringBuilder buffer = new StringBuilder();
         SocketAddress remoteAddress = this.socket.getRemoteSocketAddress();
         SocketAddress localAddress = this.socket.getLocalSocketAddress();
         if(remoteAddress != null && localAddress != null) {
            NetUtils.formatAddress(buffer, localAddress);
            buffer.append("<->");
            NetUtils.formatAddress(buffer, remoteAddress);
         }

         return buffer.toString();
      } else {
         return "[Not bound]";
      }
   }
}
