package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.util.Args;

@Immutable
public class LaxContentLengthStrategy implements ContentLengthStrategy {
   public static final LaxContentLengthStrategy INSTANCE = new LaxContentLengthStrategy();
   private final int implicitLen;

   public LaxContentLengthStrategy(int implicitLen) {
      this.implicitLen = implicitLen;
   }

   public LaxContentLengthStrategy() {
      this(-1);
   }

   public long determineLength(HttpMessage message) throws HttpException {
      Args.notNull(message, "HTTP message");
      Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
      if(transferEncodingHeader != null) {
         HeaderElement[] encodings;
         try {
            encodings = transferEncodingHeader.getElements();
         } catch (ParseException var10) {
            throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, var10);
         }

         int len = encodings.length;
         return "identity".equalsIgnoreCase(transferEncodingHeader.getValue())?-1L:(len > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName())?-2L:-1L);
      } else {
         Header contentLengthHeader = message.getFirstHeader("Content-Length");
         if(contentLengthHeader == null) {
            return (long)this.implicitLen;
         } else {
            long contentlen = -1L;
            Header[] headers = message.getHeaders("Content-Length");

            for(int i = headers.length - 1; i >= 0; --i) {
               Header header = headers[i];

               try {
                  contentlen = Long.parseLong(header.getValue());
                  break;
               }
            }

            return contentlen >= 0L?contentlen:-1L;
         }
      }
   }
}
