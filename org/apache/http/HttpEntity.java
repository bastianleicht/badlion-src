package org.apache.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;

public interface HttpEntity {
   boolean isRepeatable();

   boolean isChunked();

   long getContentLength();

   Header getContentType();

   Header getContentEncoding();

   InputStream getContent() throws IOException, IllegalStateException;

   void writeTo(OutputStream var1) throws IOException;

   boolean isStreaming();

   /** @deprecated */
   @Deprecated
   void consumeContent() throws IOException;
}
