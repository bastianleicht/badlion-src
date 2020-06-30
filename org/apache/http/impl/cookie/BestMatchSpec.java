package org.apache.http.impl.cookie;

import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.NetscapeDraftHeaderParser;
import org.apache.http.impl.cookie.RFC2109Spec;
import org.apache.http.impl.cookie.RFC2965Spec;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class BestMatchSpec implements CookieSpec {
   private final String[] datepatterns;
   private final boolean oneHeader;
   private RFC2965Spec strict;
   private RFC2109Spec obsoleteStrict;
   private BrowserCompatSpec compat;

   public BestMatchSpec(String[] datepatterns, boolean oneHeader) {
      this.datepatterns = datepatterns == null?null:(String[])datepatterns.clone();
      this.oneHeader = oneHeader;
   }

   public BestMatchSpec() {
      this((String[])null, false);
   }

   private RFC2965Spec getStrict() {
      if(this.strict == null) {
         this.strict = new RFC2965Spec(this.datepatterns, this.oneHeader);
      }

      return this.strict;
   }

   private RFC2109Spec getObsoleteStrict() {
      if(this.obsoleteStrict == null) {
         this.obsoleteStrict = new RFC2109Spec(this.datepatterns, this.oneHeader);
      }

      return this.obsoleteStrict;
   }

   private BrowserCompatSpec getCompat() {
      if(this.compat == null) {
         this.compat = new BrowserCompatSpec(this.datepatterns);
      }

      return this.compat;
   }

   public List parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(header, "Header");
      Args.notNull(origin, "Cookie origin");
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

      if(!netscape && versioned) {
         if("Set-Cookie2".equals(header.getName())) {
            return this.getStrict().parse(helems, origin);
         } else {
            return this.getObsoleteStrict().parse(helems, origin);
         }
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

         helems = new HeaderElement[]{parser.parseHeader(buffer, cursor)};
         return this.getCompat().parse(helems, origin);
      }
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      if(cookie.getVersion() > 0) {
         if(cookie instanceof SetCookie2) {
            this.getStrict().validate(cookie, origin);
         } else {
            this.getObsoleteStrict().validate(cookie, origin);
         }
      } else {
         this.getCompat().validate(cookie, origin);
      }

   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      return cookie.getVersion() > 0?(cookie instanceof SetCookie2?this.getStrict().match(cookie, origin):this.getObsoleteStrict().match(cookie, origin)):this.getCompat().match(cookie, origin);
   }

   public List formatCookies(List cookies) {
      Args.notNull(cookies, "List of cookies");
      int version = Integer.MAX_VALUE;
      boolean isSetCookie2 = true;

      for(Cookie cookie : cookies) {
         if(!(cookie instanceof SetCookie2)) {
            isSetCookie2 = false;
         }

         if(cookie.getVersion() < version) {
            version = cookie.getVersion();
         }
      }

      if(version > 0) {
         if(isSetCookie2) {
            return this.getStrict().formatCookies(cookies);
         } else {
            return this.getObsoleteStrict().formatCookies(cookies);
         }
      } else {
         return this.getCompat().formatCookies(cookies);
      }
   }

   public int getVersion() {
      return this.getStrict().getVersion();
   }

   public Header getVersionHeader() {
      return this.getStrict().getVersionHeader();
   }

   public String toString() {
      return "best-match";
   }
}
