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
import org.apache.http.impl.cookie.BasicDomainHandler;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.http.impl.cookie.BasicPathHandler;
import org.apache.http.impl.cookie.BasicSecureHandler;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatVersionAttributeHandler;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.impl.cookie.NetscapeDraftHeaderParser;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class BrowserCompatSpec extends CookieSpecBase {
   private static final String[] DEFAULT_DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"};
   private final String[] datepatterns;

   public BrowserCompatSpec(String[] datepatterns, BrowserCompatSpecFactory.SecurityLevel securityLevel) {
      if(datepatterns != null) {
         this.datepatterns = (String[])datepatterns.clone();
      } else {
         this.datepatterns = DEFAULT_DATE_PATTERNS;
      }

      switch(securityLevel) {
      case SECURITYLEVEL_DEFAULT:
         this.registerAttribHandler("path", new BasicPathHandler());
         break;
      case SECURITYLEVEL_IE_MEDIUM:
         this.registerAttribHandler("path", new BasicPathHandler() {
            public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            }
         });
         break;
      default:
         throw new RuntimeException("Unknown security level");
      }

      this.registerAttribHandler("domain", new BasicDomainHandler());
      this.registerAttribHandler("max-age", new BasicMaxAgeHandler());
      this.registerAttribHandler("secure", new BasicSecureHandler());
      this.registerAttribHandler("comment", new BasicCommentHandler());
      this.registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
      this.registerAttribHandler("version", new BrowserCompatVersionAttributeHandler());
   }

   public BrowserCompatSpec(String[] datepatterns) {
      this(datepatterns, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public BrowserCompatSpec() {
      this((String[])null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public List parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(header, "Header");
      Args.notNull(origin, "Cookie origin");
      String headername = header.getName();
      if(!headername.equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header \'" + header.toString() + "\'");
      } else {
         HeaderElement[] helems = header.getElements();
         boolean versioned = false;
         boolean netscape = false;

         for(HeaderElement helem : helems) {
            if(helem.getParameterByName("version") != null) {
               versioned = true;
            }

            if(helem.getParameterByName("expires") != null) {
               netscape = true;
            }
         }

         if(netscape || !versioned) {
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

            helems = new HeaderElement[]{parser.parseHeader(buffer, cursor)};
         }

         return this.parse(helems, origin);
      }
   }

   private static boolean isQuoteEnclosed(String s) {
      return s != null && s.startsWith("\"") && s.endsWith("\"");
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

         String cookieName = cookie.getName();
         String cookieValue = cookie.getValue();
         if(cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
            BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, new BasicHeaderElement(cookieName, cookieValue), false);
         } else {
            buffer.append(cookieName);
            buffer.append("=");
            if(cookieValue != null) {
               buffer.append(cookieValue);
            }
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
      return "compatibility";
   }
}
