package org.apache.http.entity;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.TextUtils;

@Immutable
public final class ContentType implements Serializable {
   private static final long serialVersionUID = -7768694718232371896L;
   public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_JSON = create("application/json", Consts.UTF_8);
   public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
   public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
   public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
   public static final ContentType TEXT_HTML = create("text/html", Consts.ISO_8859_1);
   public static final ContentType TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
   public static final ContentType TEXT_XML = create("text/xml", Consts.ISO_8859_1);
   public static final ContentType WILDCARD = create("*/*", (Charset)null);
   public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
   public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
   private final String mimeType;
   private final Charset charset;
   private final NameValuePair[] params;

   ContentType(String mimeType, Charset charset) {
      this.mimeType = mimeType;
      this.charset = charset;
      this.params = null;
   }

   ContentType(String mimeType, NameValuePair[] params) throws UnsupportedCharsetException {
      this.mimeType = mimeType;
      this.params = params;
      String s = this.getParameter("charset");
      this.charset = !TextUtils.isBlank(s)?Charset.forName(s):null;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getParameter(String name) {
      Args.notEmpty((CharSequence)name, "Parameter name");
      if(this.params == null) {
         return null;
      } else {
         for(NameValuePair param : this.params) {
            if(param.getName().equalsIgnoreCase(name)) {
               return param.getValue();
            }
         }

         return null;
      }
   }

   public String toString() {
      CharArrayBuffer buf = new CharArrayBuffer(64);
      buf.append(this.mimeType);
      if(this.params != null) {
         buf.append("; ");
         BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
      } else if(this.charset != null) {
         buf.append("; charset=");
         buf.append(this.charset.name());
      }

      return buf.toString();
   }

   private static boolean valid(String s) {
      for(int i = 0; i < s.length(); ++i) {
         char ch = s.charAt(i);
         if(ch == 34 || ch == 44 || ch == 59) {
            return false;
         }
      }

      return true;
   }

   public static ContentType create(String mimeType, Charset charset) {
      String type = ((String)Args.notBlank(mimeType, "MIME type")).toLowerCase(Locale.US);
      Args.check(valid(type), "MIME type may not contain reserved characters");
      return new ContentType(type, charset);
   }

   public static ContentType create(String mimeType) {
      return new ContentType(mimeType, (Charset)null);
   }

   public static ContentType create(String mimeType, String charset) throws UnsupportedCharsetException {
      return create(mimeType, !TextUtils.isBlank(charset)?Charset.forName(charset):null);
   }

   private static ContentType create(HeaderElement helem) {
      String mimeType = helem.getName();
      NameValuePair[] params = helem.getParameters();
      return new ContentType(mimeType, params != null && params.length > 0?params:null);
   }

   public static ContentType parse(String s) throws ParseException, UnsupportedCharsetException {
      Args.notNull(s, "Content type");
      CharArrayBuffer buf = new CharArrayBuffer(s.length());
      buf.append(s);
      ParserCursor cursor = new ParserCursor(0, s.length());
      HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buf, cursor);
      if(elements.length > 0) {
         return create(elements[0]);
      } else {
         throw new ParseException("Invalid content type: " + s);
      }
   }

   public static ContentType get(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
      if(entity == null) {
         return null;
      } else {
         Header header = entity.getContentType();
         if(header != null) {
            HeaderElement[] elements = header.getElements();
            if(elements.length > 0) {
               return create(elements[0]);
            }
         }

         return null;
      }
   }

   public static ContentType getOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
      ContentType contentType = get(entity);
      return contentType != null?contentType:DEFAULT_TEXT;
   }

   public ContentType withCharset(Charset charset) {
      return create(this.getMimeType(), charset);
   }

   public ContentType withCharset(String charset) {
      return create(this.getMimeType(), charset);
   }
}
