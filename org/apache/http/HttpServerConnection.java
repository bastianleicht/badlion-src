package org.apache.http;

import java.io.IOException;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface HttpServerConnection extends HttpConnection {
   HttpRequest receiveRequestHeader() throws HttpException, IOException;

   void receiveRequestEntity(HttpEntityEnclosingRequest var1) throws HttpException, IOException;

   void sendResponseHeader(HttpResponse var1) throws HttpException, IOException;

   void sendResponseEntity(HttpResponse var1) throws HttpException, IOException;

   void flush() throws IOException;
}
