package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.LoggingInputStream;
import org.apache.http.impl.conn.LoggingOutputStream;
import org.apache.http.impl.conn.Wire;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@NotThreadSafe
class LoggingManagedHttpClientConnection extends DefaultManagedHttpClientConnection {
   private final Log log;
   private final Log headerlog;
   private final Wire wire;

   public LoggingManagedHttpClientConnection(String id, Log log, Log headerlog, Log wirelog, int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory requestWriterFactory, HttpMessageParserFactory responseParserFactory) {
      super(id, buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
      this.log = log;
      this.headerlog = headerlog;
      this.wire = new Wire(wirelog, id);
   }

   public void close() throws IOException {
      if(this.log.isDebugEnabled()) {
         this.log.debug(this.getId() + ": Close connection");
      }

      super.close();
   }

   public void shutdown() throws IOException {
      if(this.log.isDebugEnabled()) {
         this.log.debug(this.getId() + ": Shutdown connection");
      }

      super.shutdown();
   }

   protected InputStream getSocketInputStream(Socket socket) throws IOException {
      InputStream in = super.getSocketInputStream(socket);
      if(this.wire.enabled()) {
         in = new LoggingInputStream(in, this.wire);
      }

      return in;
   }

   protected OutputStream getSocketOutputStream(Socket socket) throws IOException {
      OutputStream out = super.getSocketOutputStream(socket);
      if(this.wire.enabled()) {
         out = new LoggingOutputStream(out, this.wire);
      }

      return out;
   }

   protected void onResponseReceived(HttpResponse response) {
      if(response != null && this.headerlog.isDebugEnabled()) {
         this.headerlog.debug(this.getId() + " << " + response.getStatusLine().toString());
         Header[] headers = response.getAllHeaders();

         for(Header header : headers) {
            this.headerlog.debug(this.getId() + " << " + header.toString());
         }
      }

   }

   protected void onRequestSubmitted(HttpRequest request) {
      if(request != null && this.headerlog.isDebugEnabled()) {
         this.headerlog.debug(this.getId() + " >> " + request.getRequestLine().toString());
         Header[] headers = request.getAllHeaders();

         for(Header header : headers) {
            this.headerlog.debug(this.getId() + " >> " + header.toString());
         }
      }

   }
}
