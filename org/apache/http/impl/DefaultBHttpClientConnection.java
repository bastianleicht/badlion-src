package org.apache.http.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.BHttpConnectionBase;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.io.DefaultHttpResponseParserFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.util.Args;

@NotThreadSafe
public class DefaultBHttpClientConnection extends BHttpConnectionBase implements HttpClientConnection {
   private final HttpMessageParser responseParser;
   private final HttpMessageWriter requestWriter;

   public DefaultBHttpClientConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory requestWriterFactory, HttpMessageParserFactory responseParserFactory) {
      super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy);
      this.requestWriter = ((HttpMessageWriterFactory)(requestWriterFactory != null?requestWriterFactory:DefaultHttpRequestWriterFactory.INSTANCE)).create(this.getSessionOutputBuffer());
      this.responseParser = ((HttpMessageParserFactory)(responseParserFactory != null?responseParserFactory:DefaultHttpResponseParserFactory.INSTANCE)).create(this.getSessionInputBuffer(), constraints);
   }

   public DefaultBHttpClientConnection(int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints) {
      this(buffersize, buffersize, chardecoder, charencoder, constraints, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory)null, (HttpMessageParserFactory)null);
   }

   public DefaultBHttpClientConnection(int buffersize) {
      this(buffersize, buffersize, (CharsetDecoder)null, (CharsetEncoder)null, (MessageConstraints)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory)null, (HttpMessageParserFactory)null);
   }

   protected void onResponseReceived(HttpResponse response) {
   }

   protected void onRequestSubmitted(HttpRequest request) {
   }

   public void bind(Socket socket) throws IOException {
      super.bind(socket);
   }

   public boolean isResponseAvailable(int timeout) throws IOException {
      this.ensureOpen();

      try {
         return this.awaitInput(timeout);
      } catch (SocketTimeoutException var3) {
         return false;
      }
   }

   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.ensureOpen();
      this.requestWriter.write(request);
      this.onRequestSubmitted(request);
      this.incrementRequestCount();
   }

   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.ensureOpen();
      HttpEntity entity = request.getEntity();
      if(entity != null) {
         OutputStream outstream = this.prepareOutput(request);
         entity.writeTo(outstream);
         outstream.close();
      }
   }

   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
      this.ensureOpen();
      HttpResponse response = (HttpResponse)this.responseParser.parse();
      this.onResponseReceived(response);
      if(response.getStatusLine().getStatusCode() >= 200) {
         this.incrementResponseCount();
      }

      return response;
   }

   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      this.ensureOpen();
      HttpEntity entity = this.prepareInput(response);
      response.setEntity(entity);
   }

   public void flush() throws IOException {
      this.ensureOpen();
      this.doFlush();
   }
}
