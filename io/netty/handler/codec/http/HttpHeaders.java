package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderDateFormat;
import io.netty.handler.codec.http.HttpHeaderEntity;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public abstract class HttpHeaders implements Iterable {
   private static final byte[] HEADER_SEPERATOR = new byte[]{(byte)58, (byte)32};
   private static final byte[] CRLF = new byte[]{(byte)13, (byte)10};
   private static final CharSequence CONTENT_LENGTH_ENTITY = newEntity("Content-Length");
   private static final CharSequence CONNECTION_ENTITY = newEntity("Connection");
   private static final CharSequence CLOSE_ENTITY = newEntity("close");
   private static final CharSequence KEEP_ALIVE_ENTITY = newEntity("keep-alive");
   private static final CharSequence HOST_ENTITY = newEntity("Host");
   private static final CharSequence DATE_ENTITY = newEntity("Date");
   private static final CharSequence EXPECT_ENTITY = newEntity("Expect");
   private static final CharSequence CONTINUE_ENTITY = newEntity("100-continue");
   private static final CharSequence TRANSFER_ENCODING_ENTITY = newEntity("Transfer-Encoding");
   private static final CharSequence CHUNKED_ENTITY = newEntity("chunked");
   private static final CharSequence SEC_WEBSOCKET_KEY1_ENTITY = newEntity("Sec-WebSocket-Key1");
   private static final CharSequence SEC_WEBSOCKET_KEY2_ENTITY = newEntity("Sec-WebSocket-Key2");
   private static final CharSequence SEC_WEBSOCKET_ORIGIN_ENTITY = newEntity("Sec-WebSocket-Origin");
   private static final CharSequence SEC_WEBSOCKET_LOCATION_ENTITY = newEntity("Sec-WebSocket-Location");
   public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders() {
      public String get(String name) {
         return null;
      }

      public List getAll(String name) {
         return Collections.emptyList();
      }

      public List entries() {
         return Collections.emptyList();
      }

      public boolean contains(String name) {
         return false;
      }

      public boolean isEmpty() {
         return true;
      }

      public Set names() {
         return Collections.emptySet();
      }

      public HttpHeaders add(String name, Object value) {
         throw new UnsupportedOperationException("read only");
      }

      public HttpHeaders add(String name, Iterable values) {
         throw new UnsupportedOperationException("read only");
      }

      public HttpHeaders set(String name, Object value) {
         throw new UnsupportedOperationException("read only");
      }

      public HttpHeaders set(String name, Iterable values) {
         throw new UnsupportedOperationException("read only");
      }

      public HttpHeaders remove(String name) {
         throw new UnsupportedOperationException("read only");
      }

      public HttpHeaders clear() {
         throw new UnsupportedOperationException("read only");
      }

      public Iterator iterator() {
         return this.entries().iterator();
      }
   };

   public static boolean isKeepAlive(HttpMessage message) {
      String connection = message.headers().get(CONNECTION_ENTITY);
      return connection != null && equalsIgnoreCase(CLOSE_ENTITY, connection)?false:(message.getProtocolVersion().isKeepAliveDefault()?!equalsIgnoreCase(CLOSE_ENTITY, connection):equalsIgnoreCase(KEEP_ALIVE_ENTITY, connection));
   }

   public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
      HttpHeaders h = message.headers();
      if(message.getProtocolVersion().isKeepAliveDefault()) {
         if(keepAlive) {
            h.remove(CONNECTION_ENTITY);
         } else {
            h.set((CharSequence)CONNECTION_ENTITY, (Object)CLOSE_ENTITY);
         }
      } else if(keepAlive) {
         h.set((CharSequence)CONNECTION_ENTITY, (Object)KEEP_ALIVE_ENTITY);
      } else {
         h.remove(CONNECTION_ENTITY);
      }

   }

   public static String getHeader(HttpMessage message, String name) {
      return message.headers().get(name);
   }

   public static String getHeader(HttpMessage message, CharSequence name) {
      return message.headers().get(name);
   }

   public static String getHeader(HttpMessage message, String name, String defaultValue) {
      return getHeader(message, (CharSequence)name, defaultValue);
   }

   public static String getHeader(HttpMessage message, CharSequence name, String defaultValue) {
      String value = message.headers().get(name);
      return value == null?defaultValue:value;
   }

   public static void setHeader(HttpMessage message, String name, Object value) {
      message.headers().set(name, value);
   }

   public static void setHeader(HttpMessage message, CharSequence name, Object value) {
      message.headers().set(name, value);
   }

   public static void setHeader(HttpMessage message, String name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void setHeader(HttpMessage message, CharSequence name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void addHeader(HttpMessage message, String name, Object value) {
      message.headers().add(name, value);
   }

   public static void addHeader(HttpMessage message, CharSequence name, Object value) {
      message.headers().add(name, value);
   }

   public static void removeHeader(HttpMessage message, String name) {
      message.headers().remove(name);
   }

   public static void removeHeader(HttpMessage message, CharSequence name) {
      message.headers().remove(name);
   }

   public static void clearHeaders(HttpMessage message) {
      message.headers().clear();
   }

   public static int getIntHeader(HttpMessage message, String name) {
      return getIntHeader(message, (CharSequence)name);
   }

   public static int getIntHeader(HttpMessage message, CharSequence name) {
      String value = getHeader(message, name);
      if(value == null) {
         throw new NumberFormatException("header not found: " + name);
      } else {
         return Integer.parseInt(value);
      }
   }

   public static int getIntHeader(HttpMessage message, String name, int defaultValue) {
      return getIntHeader(message, (CharSequence)name, defaultValue);
   }

   public static int getIntHeader(HttpMessage message, CharSequence name, int defaultValue) {
      String value = getHeader(message, name);
      if(value == null) {
         return defaultValue;
      } else {
         try {
            return Integer.parseInt(value);
         } catch (NumberFormatException var5) {
            return defaultValue;
         }
      }
   }

   public static void setIntHeader(HttpMessage message, String name, int value) {
      message.headers().set((String)name, (Object)Integer.valueOf(value));
   }

   public static void setIntHeader(HttpMessage message, CharSequence name, int value) {
      message.headers().set((CharSequence)name, (Object)Integer.valueOf(value));
   }

   public static void setIntHeader(HttpMessage message, String name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void setIntHeader(HttpMessage message, CharSequence name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void addIntHeader(HttpMessage message, String name, int value) {
      message.headers().add((String)name, (Object)Integer.valueOf(value));
   }

   public static void addIntHeader(HttpMessage message, CharSequence name, int value) {
      message.headers().add((CharSequence)name, (Object)Integer.valueOf(value));
   }

   public static Date getDateHeader(HttpMessage message, String name) throws ParseException {
      return getDateHeader(message, (CharSequence)name);
   }

   public static Date getDateHeader(HttpMessage message, CharSequence name) throws ParseException {
      String value = getHeader(message, name);
      if(value == null) {
         throw new ParseException("header not found: " + name, 0);
      } else {
         return HttpHeaderDateFormat.get().parse(value);
      }
   }

   public static Date getDateHeader(HttpMessage message, String name, Date defaultValue) {
      return getDateHeader(message, (CharSequence)name, defaultValue);
   }

   public static Date getDateHeader(HttpMessage message, CharSequence name, Date defaultValue) {
      String value = getHeader(message, name);
      if(value == null) {
         return defaultValue;
      } else {
         try {
            return HttpHeaderDateFormat.get().parse(value);
         } catch (ParseException var5) {
            return defaultValue;
         }
      }
   }

   public static void setDateHeader(HttpMessage message, String name, Date value) {
      setDateHeader(message, (CharSequence)name, (Date)value);
   }

   public static void setDateHeader(HttpMessage message, CharSequence name, Date value) {
      if(value != null) {
         message.headers().set((CharSequence)name, (Object)HttpHeaderDateFormat.get().format(value));
      } else {
         message.headers().set((CharSequence)name, (Iterable)null);
      }

   }

   public static void setDateHeader(HttpMessage message, String name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void setDateHeader(HttpMessage message, CharSequence name, Iterable values) {
      message.headers().set(name, values);
   }

   public static void addDateHeader(HttpMessage message, String name, Date value) {
      message.headers().add((String)name, (Object)value);
   }

   public static void addDateHeader(HttpMessage message, CharSequence name, Date value) {
      message.headers().add((CharSequence)name, (Object)value);
   }

   public static long getContentLength(HttpMessage message) {
      String value = getHeader(message, CONTENT_LENGTH_ENTITY);
      if(value != null) {
         return Long.parseLong(value);
      } else {
         long webSocketContentLength = (long)getWebSocketContentLength(message);
         if(webSocketContentLength >= 0L) {
            return webSocketContentLength;
         } else {
            throw new NumberFormatException("header not found: Content-Length");
         }
      }
   }

   public static long getContentLength(HttpMessage message, long defaultValue) {
      String contentLength = message.headers().get(CONTENT_LENGTH_ENTITY);
      if(contentLength != null) {
         try {
            return Long.parseLong(contentLength);
         } catch (NumberFormatException var6) {
            return defaultValue;
         }
      } else {
         long webSocketContentLength = (long)getWebSocketContentLength(message);
         return webSocketContentLength >= 0L?webSocketContentLength:defaultValue;
      }
   }

   private static int getWebSocketContentLength(HttpMessage message) {
      HttpHeaders h = message.headers();
      if(message instanceof HttpRequest) {
         HttpRequest req = (HttpRequest)message;
         if(HttpMethod.GET.equals(req.getMethod()) && h.contains(SEC_WEBSOCKET_KEY1_ENTITY) && h.contains(SEC_WEBSOCKET_KEY2_ENTITY)) {
            return 8;
         }
      } else if(message instanceof HttpResponse) {
         HttpResponse res = (HttpResponse)message;
         if(res.getStatus().code() == 101 && h.contains(SEC_WEBSOCKET_ORIGIN_ENTITY) && h.contains(SEC_WEBSOCKET_LOCATION_ENTITY)) {
            return 16;
         }
      }

      return -1;
   }

   public static void setContentLength(HttpMessage message, long length) {
      message.headers().set((CharSequence)CONTENT_LENGTH_ENTITY, (Object)Long.valueOf(length));
   }

   public static String getHost(HttpMessage message) {
      return message.headers().get(HOST_ENTITY);
   }

   public static String getHost(HttpMessage message, String defaultValue) {
      return getHeader(message, HOST_ENTITY, defaultValue);
   }

   public static void setHost(HttpMessage message, String value) {
      message.headers().set((CharSequence)HOST_ENTITY, (Object)value);
   }

   public static void setHost(HttpMessage message, CharSequence value) {
      message.headers().set((CharSequence)HOST_ENTITY, (Object)value);
   }

   public static Date getDate(HttpMessage message) throws ParseException {
      return getDateHeader(message, DATE_ENTITY);
   }

   public static Date getDate(HttpMessage message, Date defaultValue) {
      return getDateHeader(message, DATE_ENTITY, defaultValue);
   }

   public static void setDate(HttpMessage message, Date value) {
      if(value != null) {
         message.headers().set((CharSequence)DATE_ENTITY, (Object)HttpHeaderDateFormat.get().format(value));
      } else {
         message.headers().set((CharSequence)DATE_ENTITY, (Iterable)null);
      }

   }

   public static boolean is100ContinueExpected(HttpMessage message) {
      if(!(message instanceof HttpRequest)) {
         return false;
      } else if(message.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
         return false;
      } else {
         String value = message.headers().get(EXPECT_ENTITY);
         return value == null?false:(equalsIgnoreCase(CONTINUE_ENTITY, value)?true:message.headers().contains(EXPECT_ENTITY, CONTINUE_ENTITY, true));
      }
   }

   public static void set100ContinueExpected(HttpMessage message) {
      set100ContinueExpected(message, true);
   }

   public static void set100ContinueExpected(HttpMessage message, boolean set) {
      if(set) {
         message.headers().set((CharSequence)EXPECT_ENTITY, (Object)CONTINUE_ENTITY);
      } else {
         message.headers().remove(EXPECT_ENTITY);
      }

   }

   static void validateHeaderName(CharSequence param0) {
      // $FF: Couldn't be decompiled
   }

   static void validateHeaderValue(CharSequence headerValue) {
      if(headerValue == null) {
         throw new NullPointerException("Header values cannot be null");
      } else {
         int state = 0;

         for(int index = 0; index < headerValue.length(); ++index) {
            char character = headerValue.charAt(index);
            switch(character) {
            case '\u000b':
               throw new IllegalArgumentException("Header value contains a prohibited character \'\\v\': " + headerValue);
            case '\f':
               throw new IllegalArgumentException("Header value contains a prohibited character \'\\f\': " + headerValue);
            }

            switch(state) {
            case 0:
               switch(character) {
               case '\n':
                  state = 2;
                  continue;
               case '\r':
                  state = 1;
               default:
                  continue;
               }
            case 1:
               switch(character) {
               case '\n':
                  state = 2;
                  continue;
               default:
                  throw new IllegalArgumentException("Only \'\\n\' is allowed after \'\\r\': " + headerValue);
               }
            case 2:
               switch(character) {
               case '\t':
               case ' ':
                  state = 0;
                  break;
               default:
                  throw new IllegalArgumentException("Only \' \' and \'\\t\' are allowed after \'\\n\': " + headerValue);
               }
            }
         }

         if(state != 0) {
            throw new IllegalArgumentException("Header value must not end with \'\\r\' or \'\\n\':" + headerValue);
         }
      }
   }

   public static boolean isTransferEncodingChunked(HttpMessage message) {
      return message.headers().contains(TRANSFER_ENCODING_ENTITY, CHUNKED_ENTITY, true);
   }

   public static void removeTransferEncodingChunked(HttpMessage m) {
      List<String> values = m.headers().getAll(TRANSFER_ENCODING_ENTITY);
      if(!values.isEmpty()) {
         Iterator<String> valuesIt = values.iterator();

         while(valuesIt.hasNext()) {
            String value = (String)valuesIt.next();
            if(equalsIgnoreCase(value, CHUNKED_ENTITY)) {
               valuesIt.remove();
            }
         }

         if(values.isEmpty()) {
            m.headers().remove(TRANSFER_ENCODING_ENTITY);
         } else {
            m.headers().set((CharSequence)TRANSFER_ENCODING_ENTITY, (Iterable)values);
         }

      }
   }

   public static void setTransferEncodingChunked(HttpMessage m) {
      addHeader(m, (CharSequence)TRANSFER_ENCODING_ENTITY, CHUNKED_ENTITY);
      removeHeader(m, CONTENT_LENGTH_ENTITY);
   }

   public static boolean isContentLengthSet(HttpMessage m) {
      return m.headers().contains(CONTENT_LENGTH_ENTITY);
   }

   public static boolean equalsIgnoreCase(CharSequence name1, CharSequence name2) {
      if(name1 == name2) {
         return true;
      } else if(name1 != null && name2 != null) {
         int nameLen = name1.length();
         if(nameLen != name2.length()) {
            return false;
         } else {
            for(int i = nameLen - 1; i >= 0; --i) {
               char c1 = name1.charAt(i);
               char c2 = name2.charAt(i);
               if(c1 != c2) {
                  if(c1 >= 65 && c1 <= 90) {
                     c1 = (char)(c1 + 32);
                  }

                  if(c2 >= 65 && c2 <= 90) {
                     c2 = (char)(c2 + 32);
                  }

                  if(c1 != c2) {
                     return false;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   static int hash(CharSequence name) {
      if(name instanceof HttpHeaderEntity) {
         return ((HttpHeaderEntity)name).hash();
      } else {
         int h = 0;

         for(int i = name.length() - 1; i >= 0; --i) {
            char c = name.charAt(i);
            if(c >= 65 && c <= 90) {
               c = (char)(c + 32);
            }

            h = 31 * h + c;
         }

         if(h > 0) {
            return h;
         } else if(h == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
         } else {
            return -h;
         }
      }
   }

   static void encode(HttpHeaders headers, ByteBuf buf) {
      if(headers instanceof DefaultHttpHeaders) {
         ((DefaultHttpHeaders)headers).encode(buf);
      } else {
         for(Entry<String, String> header : headers) {
            encode((CharSequence)header.getKey(), (CharSequence)header.getValue(), buf);
         }
      }

   }

   static void encode(CharSequence key, CharSequence value, ByteBuf buf) {
      if(!encodeAscii(key, buf)) {
         buf.writeBytes(HEADER_SEPERATOR);
      }

      if(!encodeAscii(value, buf)) {
         buf.writeBytes(CRLF);
      }

   }

   public static boolean encodeAscii(CharSequence seq, ByteBuf buf) {
      if(seq instanceof HttpHeaderEntity) {
         return ((HttpHeaderEntity)seq).encode(buf);
      } else {
         encodeAscii0(seq, buf);
         return false;
      }
   }

   static void encodeAscii0(CharSequence seq, ByteBuf buf) {
      int length = seq.length();

      for(int i = 0; i < length; ++i) {
         buf.writeByte((byte)seq.charAt(i));
      }

   }

   public static CharSequence newEntity(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         return new HttpHeaderEntity(name);
      }
   }

   public static CharSequence newNameEntity(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         return new HttpHeaderEntity(name, HEADER_SEPERATOR);
      }
   }

   public static CharSequence newValueEntity(String name) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         return new HttpHeaderEntity(name, CRLF);
      }
   }

   public abstract String get(String var1);

   public String get(CharSequence name) {
      return this.get(name.toString());
   }

   public abstract List getAll(String var1);

   public List getAll(CharSequence name) {
      return this.getAll(name.toString());
   }

   public abstract List entries();

   public abstract boolean contains(String var1);

   public boolean contains(CharSequence name) {
      return this.contains(name.toString());
   }

   public abstract boolean isEmpty();

   public abstract Set names();

   public abstract HttpHeaders add(String var1, Object var2);

   public HttpHeaders add(CharSequence name, Object value) {
      return this.add(name.toString(), value);
   }

   public abstract HttpHeaders add(String var1, Iterable var2);

   public HttpHeaders add(CharSequence name, Iterable values) {
      return this.add(name.toString(), values);
   }

   public HttpHeaders add(HttpHeaders headers) {
      if(headers == null) {
         throw new NullPointerException("headers");
      } else {
         for(Entry<String, String> e : headers) {
            this.add((String)e.getKey(), e.getValue());
         }

         return this;
      }
   }

   public abstract HttpHeaders set(String var1, Object var2);

   public HttpHeaders set(CharSequence name, Object value) {
      return this.set(name.toString(), value);
   }

   public abstract HttpHeaders set(String var1, Iterable var2);

   public HttpHeaders set(CharSequence name, Iterable values) {
      return this.set(name.toString(), values);
   }

   public HttpHeaders set(HttpHeaders headers) {
      if(headers == null) {
         throw new NullPointerException("headers");
      } else {
         this.clear();

         for(Entry<String, String> e : headers) {
            this.add((String)e.getKey(), e.getValue());
         }

         return this;
      }
   }

   public abstract HttpHeaders remove(String var1);

   public HttpHeaders remove(CharSequence name) {
      return this.remove(name.toString());
   }

   public abstract HttpHeaders clear();

   public boolean contains(String name, String value, boolean ignoreCaseValue) {
      List<String> values = this.getAll(name);
      if(values.isEmpty()) {
         return false;
      } else {
         for(String v : values) {
            if(ignoreCaseValue) {
               if(equalsIgnoreCase(v, value)) {
                  return true;
               }
            } else if(v.equals(value)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean contains(CharSequence name, CharSequence value, boolean ignoreCaseValue) {
      return this.contains(name.toString(), value.toString(), ignoreCaseValue);
   }

   public static final class Names {
      public static final String ACCEPT = "Accept";
      public static final String ACCEPT_CHARSET = "Accept-Charset";
      public static final String ACCEPT_ENCODING = "Accept-Encoding";
      public static final String ACCEPT_LANGUAGE = "Accept-Language";
      public static final String ACCEPT_RANGES = "Accept-Ranges";
      public static final String ACCEPT_PATCH = "Accept-Patch";
      public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
      public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
      public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
      public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
      public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
      public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
      public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
      public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
      public static final String AGE = "Age";
      public static final String ALLOW = "Allow";
      public static final String AUTHORIZATION = "Authorization";
      public static final String CACHE_CONTROL = "Cache-Control";
      public static final String CONNECTION = "Connection";
      public static final String CONTENT_BASE = "Content-Base";
      public static final String CONTENT_ENCODING = "Content-Encoding";
      public static final String CONTENT_LANGUAGE = "Content-Language";
      public static final String CONTENT_LENGTH = "Content-Length";
      public static final String CONTENT_LOCATION = "Content-Location";
      public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
      public static final String CONTENT_MD5 = "Content-MD5";
      public static final String CONTENT_RANGE = "Content-Range";
      public static final String CONTENT_TYPE = "Content-Type";
      public static final String COOKIE = "Cookie";
      public static final String DATE = "Date";
      public static final String ETAG = "ETag";
      public static final String EXPECT = "Expect";
      public static final String EXPIRES = "Expires";
      public static final String FROM = "From";
      public static final String HOST = "Host";
      public static final String IF_MATCH = "If-Match";
      public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
      public static final String IF_NONE_MATCH = "If-None-Match";
      public static final String IF_RANGE = "If-Range";
      public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
      public static final String LAST_MODIFIED = "Last-Modified";
      public static final String LOCATION = "Location";
      public static final String MAX_FORWARDS = "Max-Forwards";
      public static final String ORIGIN = "Origin";
      public static final String PRAGMA = "Pragma";
      public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
      public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
      public static final String RANGE = "Range";
      public static final String REFERER = "Referer";
      public static final String RETRY_AFTER = "Retry-After";
      public static final String SEC_WEBSOCKET_KEY1 = "Sec-WebSocket-Key1";
      public static final String SEC_WEBSOCKET_KEY2 = "Sec-WebSocket-Key2";
      public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
      public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
      public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
      public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
      public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
      public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
      public static final String SERVER = "Server";
      public static final String SET_COOKIE = "Set-Cookie";
      public static final String SET_COOKIE2 = "Set-Cookie2";
      public static final String TE = "TE";
      public static final String TRAILER = "Trailer";
      public static final String TRANSFER_ENCODING = "Transfer-Encoding";
      public static final String UPGRADE = "Upgrade";
      public static final String USER_AGENT = "User-Agent";
      public static final String VARY = "Vary";
      public static final String VIA = "Via";
      public static final String WARNING = "Warning";
      public static final String WEBSOCKET_LOCATION = "WebSocket-Location";
      public static final String WEBSOCKET_ORIGIN = "WebSocket-Origin";
      public static final String WEBSOCKET_PROTOCOL = "WebSocket-Protocol";
      public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
   }

   public static final class Values {
      public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
      public static final String BASE64 = "base64";
      public static final String BINARY = "binary";
      public static final String BOUNDARY = "boundary";
      public static final String BYTES = "bytes";
      public static final String CHARSET = "charset";
      public static final String CHUNKED = "chunked";
      public static final String CLOSE = "close";
      public static final String COMPRESS = "compress";
      public static final String CONTINUE = "100-continue";
      public static final String DEFLATE = "deflate";
      public static final String GZIP = "gzip";
      public static final String IDENTITY = "identity";
      public static final String KEEP_ALIVE = "keep-alive";
      public static final String MAX_AGE = "max-age";
      public static final String MAX_STALE = "max-stale";
      public static final String MIN_FRESH = "min-fresh";
      public static final String MULTIPART_FORM_DATA = "multipart/form-data";
      public static final String MUST_REVALIDATE = "must-revalidate";
      public static final String NO_CACHE = "no-cache";
      public static final String NO_STORE = "no-store";
      public static final String NO_TRANSFORM = "no-transform";
      public static final String NONE = "none";
      public static final String ONLY_IF_CACHED = "only-if-cached";
      public static final String PRIVATE = "private";
      public static final String PROXY_REVALIDATE = "proxy-revalidate";
      public static final String PUBLIC = "public";
      public static final String QUOTED_PRINTABLE = "quoted-printable";
      public static final String S_MAXAGE = "s-maxage";
      public static final String TRAILERS = "trailers";
      public static final String UPGRADE = "Upgrade";
      public static final String WEBSOCKET = "WebSocket";
   }
}
