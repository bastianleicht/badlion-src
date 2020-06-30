package org.apache.http.message;

import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.LineFormatter;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
public class BasicLineFormatter implements LineFormatter {
   /** @deprecated */
   @Deprecated
   public static final BasicLineFormatter DEFAULT = new BasicLineFormatter();
   public static final BasicLineFormatter INSTANCE = new BasicLineFormatter();

   protected CharArrayBuffer initBuffer(CharArrayBuffer charBuffer) {
      CharArrayBuffer buffer = charBuffer;
      if(charBuffer != null) {
         charBuffer.clear();
      } else {
         buffer = new CharArrayBuffer(64);
      }

      return buffer;
   }

   public static String formatProtocolVersion(ProtocolVersion version, LineFormatter formatter) {
      return ((LineFormatter)(formatter != null?formatter:INSTANCE)).appendProtocolVersion((CharArrayBuffer)null, version).toString();
   }

   public CharArrayBuffer appendProtocolVersion(CharArrayBuffer buffer, ProtocolVersion version) {
      Args.notNull(version, "Protocol version");
      CharArrayBuffer result = buffer;
      int len = this.estimateProtocolVersionLen(version);
      if(buffer == null) {
         result = new CharArrayBuffer(len);
      } else {
         buffer.ensureCapacity(len);
      }

      result.append(version.getProtocol());
      result.append('/');
      result.append(Integer.toString(version.getMajor()));
      result.append('.');
      result.append(Integer.toString(version.getMinor()));
      return result;
   }

   protected int estimateProtocolVersionLen(ProtocolVersion version) {
      return version.getProtocol().length() + 4;
   }

   public static String formatRequestLine(RequestLine reqline, LineFormatter formatter) {
      return ((LineFormatter)(formatter != null?formatter:INSTANCE)).formatRequestLine((CharArrayBuffer)null, reqline).toString();
   }

   public CharArrayBuffer formatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
      Args.notNull(reqline, "Request line");
      CharArrayBuffer result = this.initBuffer(buffer);
      this.doFormatRequestLine(result, reqline);
      return result;
   }

   protected void doFormatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
      String method = reqline.getMethod();
      String uri = reqline.getUri();
      int len = method.length() + 1 + uri.length() + 1 + this.estimateProtocolVersionLen(reqline.getProtocolVersion());
      buffer.ensureCapacity(len);
      buffer.append(method);
      buffer.append(' ');
      buffer.append(uri);
      buffer.append(' ');
      this.appendProtocolVersion(buffer, reqline.getProtocolVersion());
   }

   public static String formatStatusLine(StatusLine statline, LineFormatter formatter) {
      return ((LineFormatter)(formatter != null?formatter:INSTANCE)).formatStatusLine((CharArrayBuffer)null, statline).toString();
   }

   public CharArrayBuffer formatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
      Args.notNull(statline, "Status line");
      CharArrayBuffer result = this.initBuffer(buffer);
      this.doFormatStatusLine(result, statline);
      return result;
   }

   protected void doFormatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
      int len = this.estimateProtocolVersionLen(statline.getProtocolVersion()) + 1 + 3 + 1;
      String reason = statline.getReasonPhrase();
      if(reason != null) {
         len += reason.length();
      }

      buffer.ensureCapacity(len);
      this.appendProtocolVersion(buffer, statline.getProtocolVersion());
      buffer.append(' ');
      buffer.append(Integer.toString(statline.getStatusCode()));
      buffer.append(' ');
      if(reason != null) {
         buffer.append(reason);
      }

   }

   public static String formatHeader(Header header, LineFormatter formatter) {
      return ((LineFormatter)(formatter != null?formatter:INSTANCE)).formatHeader((CharArrayBuffer)null, header).toString();
   }

   public CharArrayBuffer formatHeader(CharArrayBuffer buffer, Header header) {
      Args.notNull(header, "Header");
      CharArrayBuffer result;
      if(header instanceof FormattedHeader) {
         result = ((FormattedHeader)header).getBuffer();
      } else {
         result = this.initBuffer(buffer);
         this.doFormatHeader(result, header);
      }

      return result;
   }

   protected void doFormatHeader(CharArrayBuffer buffer, Header header) {
      String name = header.getName();
      String value = header.getValue();
      int len = name.length() + 2;
      if(value != null) {
         len += value.length();
      }

      buffer.ensureCapacity(len);
      buffer.append(name);
      buffer.append(": ");
      if(value != null) {
         buffer.append(value);
      }

   }
}
