package org.apache.http.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.BHttpConnectionBase;
import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpRequestParserFactory;
import org.apache.http.impl.io.DefaultHttpResponseWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.util.Args;

@NotThreadSafe
public class DefaultBHttpServerConnection extends BHttpConnectionBase implements HttpServerConnection {
   private final HttpMessageParser requestParser;
   private final HttpMessageWriter responseWriter;

   public DefaultBHttpServerConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory requestParserFactory, HttpMessageWriterFactory responseWriterFactory) {
      super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, (ContentLengthStrategy)(incomingContentStrategy != null?incomingContentStrategy:DisallowIdentityContentLengthStrategy.INSTANCE), outgoingContentStrategy);
      this.requestParser = ((HttpMessageParserFactory)(requestParserFactory != null?requestParserFactory:DefaultHttpRequestParserFactory.INSTANCE)).create(this.getSessionInputBuffer(), constraints);
      this.responseWriter = ((HttpMessageWriterFactory)(responseWriterFactory != null?responseWriterFactory:DefaultHttpResponseWriterFactory.INSTANCE)).create(this.getSessionOutputBuffer());
   }

   public DefaultBHttpServerConnection(int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints) {
      this(buffersize, buffersize, chardecoder, charencoder, constraints, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory)null, (HttpMessageWriterFactory)null);
   }

   public DefaultBHttpServerConnection(int buffersize) {
      this(buffersize, buffersize, (CharsetDecoder)null, (CharsetEncoder)null, (MessageConstraints)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory)null, (HttpMessageWriterFactory)null);
   }

   protected void onRequestReceived(HttpRequest request) {
   }

   protected void onResponseSubmitted(HttpResponse response) {
   }

   public void bind(Socket socket) throws IOException {
      super.bind(socket);
   }

   public HttpRequest receiveRequestHeader() throws HttpException, IOException {
      this.ensureOpen();
      HttpRequest request = (HttpRequest)this.requestParser.parse();
      this.onRequestReceived(request);
      this.incrementRequestCount();
      return request;
   }

   public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.ensureOpen();
      HttpEntity entity = this.prepareInput(request);
      request.setEntity(entity);
   }

   public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      this.ensureOpen();
      this.responseWriter.write(response);
      this.onResponseSubmitted(response);
      if(response.getStatusLine().getStatusCode() >= 200) {
         this.incrementResponseCount();
      }

   }

   public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      this.ensureOpen();
      HttpEntity entity = response.getEntity();
      if(entity != null) {
         OutputStream outstream = this.prepareOutput(response);
         entity.writeTo(outstream);
         outstream.close();
      }
   }

   public void flush() throws IOException {
      this.ensureOpen();
      this.doFlush();
   }
}
