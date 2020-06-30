package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicCommentHandler;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.http.impl.cookie.BasicPathHandler;
import org.apache.http.impl.cookie.BasicSecureHandler;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.impl.cookie.NetscapeDomainHandler;
import org.apache.http.impl.cookie.NetscapeDraftHeaderParser;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class NetscapeDraftSpec extends CookieSpecBase {
   protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";
   private final String[] datepatterns;

   public NetscapeDraftSpec(String[] datepatterns) {
      if(datepatterns != null) {
         this.datepatterns = (String[])datepatterns.clone();
      } else {
         this.datepatterns = new String[]{"EEE, dd-MMM-yy HH:mm:ss z"};
      }

      this.registerAttribHandler("path", new BasicPathHandler());
      this.registerAttribHandler("domain", new NetscapeDomainHandler());
      this.registerAttribHandler("max-age", new BasicMaxAgeHandler());
      this.registerAttribHandler("secure", new BasicSecureHandler());
      this.registerAttribHandler("comment", new BasicCommentHandler());
      this.registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
   }

   public NetscapeDraftSpec() {
      this((String[])null);
   }

   public List parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(header, "Header");
      Args.notNull(origin, "Cookie origin");
      if(!header.getName().equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header \'" + header.toString() + "\'");
      } else {
         NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
         CharArrayBuffer buffer;
         ParserCursor cursor;
         if(header instanceof FormattedHeader) {
            buffer = ((FormattedHeader)header).getBuffer();
            cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
         } else {
            String s = header.getValue();
            if(s == null) {
               throw new MalformedCookieException("Header value is null");
            }

            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            cursor = new ParserCursor(0, buffer.length());
         }

         return this.parse(new HeaderElement[]{parser.parseHeader(buffer, cursor)}, origin);
      }
   }

   public List formatCookies(List cookies) {
      Args.notEmpty((Collection)cookies, "List of cookies");
      CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
      buffer.append("Cookie");
      buffer.append(": ");

      for(int i = 0; i < cookies.size(); ++i) {
         Cookie cookie = (Cookie)cookies.get(i);
         if(i > 0) {
            buffer.append("; ");
         }

         buffer.append(cookie.getName());
         String s = cookie.getValue();
         if(s != null) {
            buffer.append("=");
            buffer.append(s);
         }
      }

      List<Header> headers = new ArrayList(1);
      headers.add(new BufferedHeader(buffer));
      return headers;
   }

   public int getVersion() {
      return 0;
   }

   public Header getVersionHeader() {
      return null;
   }

   public String toString() {
      return "netscape";
   }
}
