package org.apache.http.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.io.EofSensor;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.message.LineParser;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@NotThreadSafe
public abstract class AbstractHttpClientConnection implements HttpClientConnection {
   private final EntitySerializer entityserializer = this.createEntitySerializer();
   private final EntityDeserializer entitydeserializer = this.createEntityDeserializer();
   private SessionInputBuffer inbuffer = null;
   private SessionOutputBuffer outbuffer = null;
   private EofSensor eofSensor = null;
   private HttpMessageParser responseParser = null;
   private HttpMessageWriter requestWriter = null;
   private HttpConnectionMetricsImpl metrics = null;

   protected abstract void assertOpen() throws IllegalStateException;

   protected EntityDeserializer createEntityDeserializer() {
      return new EntityDeserializer(new LaxContentLengthStrategy());
   }

   protected EntitySerializer createEntitySerializer() {
      return new EntitySerializer(new StrictContentLengthStrategy());
   }

   protected HttpResponseFactory createHttpResponseFactory() {
      return DefaultHttpResponseFactory.INSTANCE;
   }

   protected HttpMessageParser createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
      return new DefaultHttpResponseParser(buffer, (LineParser)null, responseFactory, params);
   }

   protected HttpMessageWriter createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
      return new HttpRequestWriter(buffer, (LineFormatter)null, params);
   }

   protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
      return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
   }

   protected void init(SessionInputBuffer inbuffer, SessionOutputBuffer outbuffer, HttpParams params) {
      this.inbuffer = (SessionInputBuffer)Args.notNull(inbuffer, "Input session buffer");
      this.outbuffer = (SessionOutputBuffer)Args.notNull(outbuffer, "Output session buffer");
      if(inbuffer instanceof EofSensor) {
         this.eofSensor = (EofSensor)inbuffer;
      }

      this.responseParser = this.createResponseParser(inbuffer, this.createHttpResponseFactory(), params);
      this.requestWriter = this.createRequestWriter(outbuffer, params);
      this.metrics = this.createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
   }

   public boolean isResponseAvailable(int timeout) throws IOException {
      this.assertOpen();

      try {
         return this.inbuffer.isDataAvailable(timeout);
      } catch (SocketTimeoutException var3) {
         return false;
      }
   }

   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.assertOpen();
      this.requestWriter.write(request);
      this.metrics.incrementRequestCount();
   }

   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.assertOpen();
      if(request.getEntity() != null) {
         this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
      }
   }

   protected void doFlush() throws IOException {
      this.outbuffer.flush();
   }

   public void flush() throws IOException {
      this.assertOpen();
      this.doFlush();
   }

   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
      this.assertOpen();
      HttpResponse response = (HttpResponse)this.responseParser.parse();
      if(response.getStatusLine().getStatusCode() >= 200) {
         this.metrics.incrementResponseCount();
      }

      return response;
   }

   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      this.assertOpen();
      HttpEntity entity = this.entitydeserializer.deserialize(this.inbuffer, response);
      response.setEntity(entity);
   }

   protected boolean isEof() {
      return this.eofSensor != null && this.eofSensor.isEof();
   }

   public boolean isStale() {
      if(!this.isOpen()) {
         return true;
      } else if(this.isEof()) {
         return true;
      } else {
         try {
            this.inbuffer.isDataAvailable(1);
            return this.isEof();
         } catch (SocketTimeoutException var2) {
            return false;
         } catch (IOException var3) {
            return true;
         }
      }
   }

   public HttpConnectionMetrics getMetrics() {
      return this.metrics;
   }
}
