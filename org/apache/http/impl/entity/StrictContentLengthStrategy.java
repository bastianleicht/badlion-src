package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.util.Args;

@Immutable
public class StrictContentLengthStrategy implements ContentLengthStrategy {
   public static final StrictContentLengthStrategy INSTANCE = new StrictContentLengthStrategy();
   private final int implicitLen;

   public StrictContentLengthStrategy(int implicitLen) {
      this.implicitLen = implicitLen;
   }

   public StrictContentLengthStrategy() {
      this(-1);
   }

   public long determineLength(HttpMessage message) throws HttpException {
      Args.notNull(message, "HTTP message");
      Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
      if(transferEncodingHeader != null) {
         String s = transferEncodingHeader.getValue();
         if("chunked".equalsIgnoreCase(s)) {
            if(message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
               throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
            } else {
               return -2L;
            }
         } else if("identity".equalsIgnoreCase(s)) {
            return -1L;
         } else {
            throw new ProtocolException("Unsupported transfer encoding: " + s);
         }
      } else {
         Header contentLengthHeader = message.getFirstHeader("Content-Length");
         if(contentLengthHeader != null) {
            String s = contentLengthHeader.getValue();

            try {
               long len = Long.parseLong(s);
               if(len < 0L) {
                  throw new ProtocolException("Negative content length: " + s);
               } else {
                  return len;
               }
            } catch (NumberFormatException var7) {
               throw new ProtocolException("Invalid content length: " + s);
            }
         } else {
            return (long)this.implicitLen;
         }
      }
   }
}
