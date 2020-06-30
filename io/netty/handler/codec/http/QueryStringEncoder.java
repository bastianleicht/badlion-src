package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

public class QueryStringEncoder {
   private final Charset charset;
   private final String uri;
   private final List params;

   public QueryStringEncoder(String uri) {
      this(uri, HttpConstants.DEFAULT_CHARSET);
   }

   public QueryStringEncoder(String uri, Charset charset) {
      this.params = new ArrayList();
      if(uri == null) {
         throw new NullPointerException("getUri");
      } else if(charset == null) {
         throw new NullPointerException("charset");
      } else {
         this.uri = uri;
         this.charset = charset;
      }
   }

   public void addParam(String name, String value) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         this.params.add(new QueryStringEncoder.Param(name, value));
      }
   }

   public URI toUri() throws URISyntaxException {
      return new URI(this.toString());
   }

   public String toString() {
      if(this.params.isEmpty()) {
         return this.uri;
      } else {
         StringBuilder sb = (new StringBuilder(this.uri)).append('?');

         for(int i = 0; i < this.params.size(); ++i) {
            QueryStringEncoder.Param param = (QueryStringEncoder.Param)this.params.get(i);
            sb.append(encodeComponent(param.name, this.charset));
            if(param.value != null) {
               sb.append('=');
               sb.append(encodeComponent(param.value, this.charset));
            }

            if(i != this.params.size() - 1) {
               sb.append('&');
            }
         }

         return sb.toString();
      }
   }

   private static String encodeComponent(String s, Charset charset) {
      try {
         return URLEncoder.encode(s, charset.name()).replace("+", "%20");
      } catch (UnsupportedEncodingException var3) {
         throw new UnsupportedCharsetException(charset.name());
      }
   }

   private static final class Param {
      final String name;
      final String value;

      Param(String name, String value) {
         this.value = value;
         this.name = name;
      }
   }
}
