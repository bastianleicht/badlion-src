package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpVersion implements Comparable {
   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");
   private static final String HTTP_1_0_STRING = "HTTP/1.0";
   private static final String HTTP_1_1_STRING = "HTTP/1.1";
   public static final HttpVersion HTTP_1_0 = new HttpVersion("HTTP", 1, 0, false, true);
   public static final HttpVersion HTTP_1_1 = new HttpVersion("HTTP", 1, 1, true, true);
   private final String protocolName;
   private final int majorVersion;
   private final int minorVersion;
   private final String text;
   private final boolean keepAliveDefault;
   private final byte[] bytes;

   public static HttpVersion valueOf(String text) {
      if(text == null) {
         throw new NullPointerException("text");
      } else {
         text = text.trim();
         if(text.isEmpty()) {
            throw new IllegalArgumentException("text is empty");
         } else {
            HttpVersion version = version0(text);
            if(version == null) {
               text = text.toUpperCase();
               version = version0(text);
               if(version == null) {
                  version = new HttpVersion(text, true);
               }
            }

            return version;
         }
      }
   }

   private static HttpVersion version0(String text) {
      return "HTTP/1.1".equals(text)?HTTP_1_1:("HTTP/1.0".equals(text)?HTTP_1_0:null);
   }

   public HttpVersion(String text, boolean keepAliveDefault) {
      if(text == null) {
         throw new NullPointerException("text");
      } else {
         text = text.trim().toUpperCase();
         if(text.isEmpty()) {
            throw new IllegalArgumentException("empty text");
         } else {
            Matcher m = VERSION_PATTERN.matcher(text);
            if(!m.matches()) {
               throw new IllegalArgumentException("invalid version format: " + text);
            } else {
               this.protocolName = m.group(1);
               this.majorVersion = Integer.parseInt(m.group(2));
               this.minorVersion = Integer.parseInt(m.group(3));
               this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
               this.keepAliveDefault = keepAliveDefault;
               this.bytes = null;
            }
         }
      }
   }

   public HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
      this(protocolName, majorVersion, minorVersion, keepAliveDefault, false);
   }

   private HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault, boolean bytes) {
      if(protocolName == null) {
         throw new NullPointerException("protocolName");
      } else {
         protocolName = protocolName.trim().toUpperCase();
         if(protocolName.isEmpty()) {
            throw new IllegalArgumentException("empty protocolName");
         } else {
            for(int i = 0; i < protocolName.length(); ++i) {
               if(Character.isISOControl(protocolName.charAt(i)) || Character.isWhitespace(protocolName.charAt(i))) {
                  throw new IllegalArgumentException("invalid character in protocolName");
               }
            }

            if(majorVersion < 0) {
               throw new IllegalArgumentException("negative majorVersion");
            } else if(minorVersion < 0) {
               throw new IllegalArgumentException("negative minorVersion");
            } else {
               this.protocolName = protocolName;
               this.majorVersion = majorVersion;
               this.minorVersion = minorVersion;
               this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
               this.keepAliveDefault = keepAliveDefault;
               if(bytes) {
                  this.bytes = this.text.getBytes(CharsetUtil.US_ASCII);
               } else {
                  this.bytes = null;
               }

            }
         }
      }
   }

   public String protocolName() {
      return this.protocolName;
   }

   public int majorVersion() {
      return this.majorVersion;
   }

   public int minorVersion() {
      return this.minorVersion;
   }

   public String text() {
      return this.text;
   }

   public boolean isKeepAliveDefault() {
      return this.keepAliveDefault;
   }

   public String toString() {
      return this.text();
   }

   public int hashCode() {
      return (this.protocolName().hashCode() * 31 + this.majorVersion()) * 31 + this.minorVersion();
   }

   public boolean equals(Object o) {
      if(!(o instanceof HttpVersion)) {
         return false;
      } else {
         HttpVersion that = (HttpVersion)o;
         return this.minorVersion() == that.minorVersion() && this.majorVersion() == that.majorVersion() && this.protocolName().equals(that.protocolName());
      }
   }

   public int compareTo(HttpVersion o) {
      int v = this.protocolName().compareTo(o.protocolName());
      if(v != 0) {
         return v;
      } else {
         v = this.majorVersion() - o.majorVersion();
         return v != 0?v:this.minorVersion() - o.minorVersion();
      }
   }

   void encode(ByteBuf buf) {
      if(this.bytes == null) {
         HttpHeaders.encodeAscii0(this.text, buf);
      } else {
         buf.writeBytes(this.bytes);
      }

   }
}
