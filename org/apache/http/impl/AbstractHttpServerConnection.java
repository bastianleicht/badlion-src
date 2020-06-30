package org.apache.http.impl;

import java.io.IOException;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpResponseWriter;
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
public abstract class AbstractHttpServerConnection implements HttpServerConnection {
   private final EntitySerializer entityserializer = this.createEntitySerializer();
   private final EntityDeserializer entitydeserializer = this.createEntityDeserializer();
   private SessionInputBuffer inbuffer = null;
   private SessionOutputBuffer outbuffer = null;
   private EofSensor eofSensor = null;
   private HttpMessageParser requestParser = null;
   private HttpMessageWriter responseWriter = null;
   private HttpConnectionMetricsImpl metrics = null;

   protected abstract void assertOpen() throws IllegalStateException;

   protected EntityDeserializer createEntityDeserializer() {
      return new EntityDeserializer(new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0)));
   }

   protected EntitySerializer createEntitySerializer() {
      return new EntitySerializer(new StrictContentLengthStrategy());
   }

   protected HttpRequestFactory createHttpRequestFactory() {
      return DefaultHttpRequestFactory.INSTANCE;
   }

   protected HttpMessageParser createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
      return new DefaultHttpRequestParser(buffer, (LineParser)null, requestFactory, params);
   }

   protected HttpMessageWriter createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
      return new HttpResponseWriter(buffer, (LineFormatter)null, params);
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

      this.requestParser = this.createRequestParser(inbuffer, this.createHttpRequestFactory(), params);
      this.responseWriter = this.createResponseWriter(outbuffer, params);
      this.metrics = this.createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
   }

   public HttpRequest receiveRequestHeader() throws HttpException, IOException {
      this.assertOpen();
      HttpRequest request = (HttpRequest)this.requestParser.parse();
      this.metrics.incrementRequestCount();
      return request;
   }

   public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      this.assertOpen();
      HttpEntity entity = this.entitydeserializer.deserialize(this.inbuffer, request);
      request.setEntity(entity);
   }

   protected void doFlush() throws IOException {
      this.outbuffer.flush();
   }

   public void flush() throws IOException {
      this.assertOpen();
      this.doFlush();
   }

   public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      this.assertOpen();
      this.responseWriter.write(response);
      if(response.getStatusLine().getStatusCode() >= 200) {
         this.metrics.incrementResponseCount();
      }

   }

   public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
      if(response.getEntity() != null) {
         this.entityserializer.serialize(this.outbuffer, response, response.getEntity());
      }
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
         } catch (IOException var2) {
            return true;
         }
      }
   }

   public HttpConnectionMetrics getMetrics() {
      return this.metrics;
   }
}
