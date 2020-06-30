package org.apache.http.impl.conn;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Immutable;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.LoggingManagedHttpClientConnection;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Immutable
public class ManagedHttpClientConnectionFactory implements HttpConnectionFactory {
   private static final AtomicLong COUNTER = new AtomicLong();
   public static final ManagedHttpClientConnectionFactory INSTANCE = new ManagedHttpClientConnectionFactory();
   private final Log log;
   private final Log headerlog;
   private final Log wirelog;
   private final HttpMessageWriterFactory requestWriterFactory;
   private final HttpMessageParserFactory responseParserFactory;

   public ManagedHttpClientConnectionFactory(HttpMessageWriterFactory requestWriterFactory, HttpMessageParserFactory responseParserFactory) {
      this.log = LogFactory.getLog(DefaultManagedHttpClientConnection.class);
      this.headerlog = LogFactory.getLog("org.apache.http.headers");
      this.wirelog = LogFactory.getLog("org.apache.http.wire");
      this.requestWriterFactory = (HttpMessageWriterFactory)(requestWriterFactory != null?requestWriterFactory:DefaultHttpRequestWriterFactory.INSTANCE);
      this.responseParserFactory = (HttpMessageParserFactory)(responseParserFactory != null?responseParserFactory:DefaultHttpResponseParserFactory.INSTANCE);
   }

   public ManagedHttpClientConnectionFactory(HttpMessageParserFactory responseParserFactory) {
      this((HttpMessageWriterFactory)null, responseParserFactory);
   }

   public ManagedHttpClientConnectionFactory() {
      this((HttpMessageWriterFactory)null, (HttpMessageParserFactory)null);
   }

   public ManagedHttpClientConnection create(HttpRoute route, ConnectionConfig config) {
      ConnectionConfig cconfig = config != null?config:ConnectionConfig.DEFAULT;
      CharsetDecoder chardecoder = null;
      CharsetEncoder charencoder = null;
      Charset charset = cconfig.getCharset();
      CodingErrorAction malformedInputAction = cconfig.getMalformedInputAction() != null?cconfig.getMalformedInputAction():CodingErrorAction.REPORT;
      CodingErrorAction unmappableInputAction = cconfig.getUnmappableInputAction() != null?cconfig.getUnmappableInputAction():CodingErrorAction.REPORT;
      if(charset != null) {
         chardecoder = charset.newDecoder();
         chardecoder.onMalformedInput(malformedInputAction);
         chardecoder.onUnmappableCharacter(unmappableInputAction);
         charencoder = charset.newEncoder();
         charencoder.onMalformedInput(malformedInputAction);
         charencoder.onUnmappableCharacter(unmappableInputAction);
      }

      String id = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());
      return new LoggingManagedHttpClientConnection(id, this.log, this.headerlog, this.wirelog, cconfig.getBufferSize(), cconfig.getFragmentSizeHint(), chardecoder, charencoder, cconfig.getMessageConstraints(), (ContentLengthStrategy)null, (ContentLengthStrategy)null, this.requestWriterFactory, this.responseParserFactory);
   }
}
