package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;

@Immutable
public class HttpRequestExecutor {
   public static final int DEFAULT_WAIT_FOR_CONTINUE = 3000;
   private final int waitForContinue;

   public HttpRequestExecutor(int waitForContinue) {
      this.waitForContinue = Args.positive(waitForContinue, "Wait for continue time");
   }

   public HttpRequestExecutor() {
      this(3000);
   }

   protected boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
      if("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
         return false;
      } else {
         int status = response.getStatusLine().getStatusCode();
         return status >= 200 && status != 204 && status != 304 && status != 205;
      }
   }

   public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
      Args.notNull(request, "HTTP request");
      Args.notNull(conn, "Client connection");
      Args.notNull(context, "HTTP context");

      try {
         HttpResponse response = this.doSendRequest(request, conn, context);
         if(response == null) {
            response = this.doReceiveResponse(request, conn, context);
         }

         return response;
      } catch (IOException var5) {
         closeConnection(conn);
         throw var5;
      } catch (HttpException var6) {
         closeConnection(conn);
         throw var6;
      } catch (RuntimeException var7) {
         closeConnection(conn);
         throw var7;
      }
   }

   private static void closeConnection(HttpClientConnection conn) {
      try {
         conn.close();
      } catch (IOException var2) {
         ;
      }

   }

   public void preProcess(HttpRequest request, HttpProcessor processor, HttpContext context) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      Args.notNull(processor, "HTTP processor");
      Args.notNull(context, "HTTP context");
      context.setAttribute("http.request", request);
      processor.process(request, context);
   }

   protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
      Args.notNull(request, "HTTP request");
      Args.notNull(conn, "Client connection");
      Args.notNull(context, "HTTP context");
      HttpResponse response = null;
      context.setAttribute("http.connection", conn);
      context.setAttribute("http.request_sent", Boolean.FALSE);
      conn.sendRequestHeader(request);
      if(request instanceof HttpEntityEnclosingRequest) {
         boolean sendentity = true;
         ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
         if(((HttpEntityEnclosingRequest)request).expectContinue() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
            conn.flush();
            if(conn.isResponseAvailable(this.waitForContinue)) {
               response = conn.receiveResponseHeader();
               if(this.canResponseHaveBody(request, response)) {
                  conn.receiveResponseEntity(response);
               }

               int status = response.getStatusLine().getStatusCode();
               if(status < 200) {
                  if(status != 100) {
                     throw new ProtocolException("Unexpected response: " + response.getStatusLine());
                  }

                  response = null;
               } else {
                  sendentity = false;
               }
            }
         }

         if(sendentity) {
            conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
         }
      }

      conn.flush();
      context.setAttribute("http.request_sent", Boolean.TRUE);
      return response;
   }

   protected HttpResponse doReceiveResponse(HttpRequest request, HttpClientConnection conn, HttpContext context) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      Args.notNull(conn, "Client connection");
      Args.notNull(context, "HTTP context");
      HttpResponse response = null;

      for(int statusCode = 0; response == null || statusCode < 200; statusCode = response.getStatusLine().getStatusCode()) {
         response = conn.receiveResponseHeader();
         if(this.canResponseHaveBody(request, response)) {
            conn.receiveResponseEntity(response);
         }
      }

      return response;
   }

   public void postProcess(HttpResponse response, HttpProcessor processor, HttpContext context) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      Args.notNull(processor, "HTTP processor");
      Args.notNull(context, "HTTP context");
      context.setAttribute("http.response", response);
      processor.process(response, context);
   }
}
