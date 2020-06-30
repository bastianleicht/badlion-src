package org.apache.http.protocol;

import java.nio.charset.Charset;
import org.apache.http.Consts;

public final class HTTP {
   public static final int CR = 13;
   public static final int LF = 10;
   public static final int SP = 32;
   public static final int HT = 9;
   public static final String TRANSFER_ENCODING = "Transfer-Encoding";
   public static final String CONTENT_LEN = "Content-Length";
   public static final String CONTENT_TYPE = "Content-Type";
   public static final String CONTENT_ENCODING = "Content-Encoding";
   public static final String EXPECT_DIRECTIVE = "Expect";
   public static final String CONN_DIRECTIVE = "Connection";
   public static final String TARGET_HOST = "Host";
   public static final String USER_AGENT = "User-Agent";
   public static final String DATE_HEADER = "Date";
   public static final String SERVER_HEADER = "Server";
   public static final String EXPECT_CONTINUE = "100-continue";
   public static final String CONN_CLOSE = "Close";
   public static final String CONN_KEEP_ALIVE = "Keep-Alive";
   public static final String CHUNK_CODING = "chunked";
   public static final String IDENTITY_CODING = "identity";
   public static final Charset DEF_CONTENT_CHARSET = Consts.ISO_8859_1;
   public static final Charset DEF_PROTOCOL_CHARSET = Consts.ASCII;
   /** @deprecated */
   @Deprecated
   public static final String UTF_8 = "UTF-8";
   /** @deprecated */
   @Deprecated
   public static final String UTF_16 = "UTF-16";
   /** @deprecated */
   @Deprecated
   public static final String US_ASCII = "US-ASCII";
   /** @deprecated */
   @Deprecated
   public static final String ASCII = "ASCII";
   /** @deprecated */
   @Deprecated
   public static final String ISO_8859_1 = "ISO-8859-1";
   /** @deprecated */
   @Deprecated
   public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
   /** @deprecated */
   @Deprecated
   public static final String DEFAULT_PROTOCOL_CHARSET = "US-ASCII";
   /** @deprecated */
   @Deprecated
   public static final String OCTET_STREAM_TYPE = "application/octet-stream";
   /** @deprecated */
   @Deprecated
   public static final String PLAIN_TEXT_TYPE = "text/plain";
   /** @deprecated */
   @Deprecated
   public static final String CHARSET_PARAM = "; charset=";
   /** @deprecated */
   @Deprecated
   public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

   public static boolean isWhitespace(char ch) {
      return ch == 32 || ch == 9 || ch == 13 || ch == 10;
   }
}
