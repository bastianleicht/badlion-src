package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.annotation.Immutable;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.ConnSupport;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Immutable
public class DefaultBHttpServerConnectionFactory implements HttpConnectionFactory {
   public static final DefaultBHttpServerConnectionFactory INSTANCE = new DefaultBHttpServerConnectionFactory();
   private final ConnectionConfig cconfig;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;
   private final HttpMessageParserFactory requestParserFactory;
   private final HttpMessageWriterFactory responseWriterFactory;

   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory requestParserFactory, HttpMessageWriterFactory responseWriterFactory) {
      this.cconfig = cconfig != null?cconfig:ConnectionConfig.DEFAULT;
      this.incomingContentStrategy = incomingContentStrategy;
      this.outgoingContentStrategy = outgoingContentStrategy;
      this.requestParserFactory = requestParserFactory;
      this.responseWriterFactory = responseWriterFactory;
   }

   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig, HttpMessageParserFactory requestParserFactory, HttpMessageWriterFactory responseWriterFactory) {
      this(cconfig, (ContentLengthStrategy)null, (ContentLengthStrategy)null, requestParserFactory, responseWriterFactory);
   }

   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig) {
      this(cconfig, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory)null, (HttpMessageWriterFactory)null);
   }

   public DefaultBHttpServerConnectionFactory() {
      this((ConnectionConfig)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory)null, (HttpMessageWriterFactory)null);
   }

   public DefaultBHttpServerConnection createConnection(Socket socket) throws IOException {
      DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
      conn.bind(socket);
      return conn;
   }
}
