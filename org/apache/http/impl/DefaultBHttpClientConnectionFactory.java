package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.annotation.Immutable;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.ConnSupport;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Immutable
public class DefaultBHttpClientConnectionFactory implements HttpConnectionFactory {
   public static final DefaultBHttpClientConnectionFactory INSTANCE = new DefaultBHttpClientConnectionFactory();
   private final ConnectionConfig cconfig;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;
   private final HttpMessageWriterFactory requestWriterFactory;
   private final HttpMessageParserFactory responseParserFactory;

   public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory requestWriterFactory, HttpMessageParserFactory responseParserFactory) {
      this.cconfig = cconfig != null?cconfig:ConnectionConfig.DEFAULT;
      this.incomingContentStrategy = incomingContentStrategy;
      this.outgoingContentStrategy = outgoingContentStrategy;
      this.requestWriterFactory = requestWriterFactory;
      this.responseParserFactory = responseParserFactory;
   }

   public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig, HttpMessageWriterFactory requestWriterFactory, HttpMessageParserFactory responseParserFactory) {
      this(cconfig, (ContentLengthStrategy)null, (ContentLengthStrategy)null, requestWriterFactory, responseParserFactory);
   }

   public DefaultBHttpClientConnectionFactory(ConnectionConfig cconfig) {
      this(cconfig, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory)null, (HttpMessageParserFactory)null);
   }

   public DefaultBHttpClientConnectionFactory() {
      this((ConnectionConfig)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory)null, (HttpMessageParserFactory)null);
   }

   public DefaultBHttpClientConnection createConnection(Socket socket) throws IOException {
      DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
      conn.bind(socket);
      return conn;
   }
}
