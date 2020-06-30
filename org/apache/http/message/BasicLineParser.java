package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
public class BasicLineParser implements LineParser {
   /** @deprecated */
   @Deprecated
   public static final BasicLineParser DEFAULT = new BasicLineParser();
   public static final BasicLineParser INSTANCE = new BasicLineParser();
   protected final ProtocolVersion protocol;

   public BasicLineParser(ProtocolVersion proto) {
      this.protocol = (ProtocolVersion)(proto != null?proto:HttpVersion.HTTP_1_1);
   }

   public BasicLineParser() {
      this((ProtocolVersion)null);
   }

   public static ProtocolVersion parseProtocolVersion(String value, LineParser parser) throws ParseException {
      Args.notNull(value, "Value");
      CharArrayBuffer buffer = new CharArrayBuffer(value.length());
      buffer.append(value);
      ParserCursor cursor = new ParserCursor(0, value.length());
      return ((LineParser)(parser != null?parser:INSTANCE)).parseProtocolVersion(buffer, cursor);
   }

   public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      String protoname = this.protocol.getProtocol();
      int protolength = protoname.length();
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();
      this.skipWhitespace(buffer, cursor);
      int i = cursor.getPos();
      if(i + protolength + 4 > indexTo) {
         throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
      } else {
         boolean ok = true;

         for(int j = 0; ok && j < protolength; ++j) {
            ok = buffer.charAt(i + j) == protoname.charAt(j);
         }

         if(ok) {
            ok = buffer.charAt(i + protolength) == 47;
         }

         if(!ok) {
            throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
         } else {
            i = i + protolength + 1;
            int period = buffer.indexOf(46, i, indexTo);
            if(period == -1) {
               throw new ParseException("Invalid protocol version number: " + buffer.substring(indexFrom, indexTo));
            } else {
               int major;
               try {
                  major = Integer.parseInt(buffer.substringTrimmed(i, period));
               } catch (NumberFormatException var15) {
                  throw new ParseException("Invalid protocol major version number: " + buffer.substring(indexFrom, indexTo));
               }

               i = period + 1;
               int blank = buffer.indexOf(32, i, indexTo);
               if(blank == -1) {
                  blank = indexTo;
               }

               int minor;
               try {
                  minor = Integer.parseInt(buffer.substringTrimmed(i, blank));
               } catch (NumberFormatException var14) {
                  throw new ParseException("Invalid protocol minor version number: " + buffer.substring(indexFrom, indexTo));
               }

               cursor.updatePos(blank);
               return this.createProtocolVersion(major, minor);
            }
         }
      }
   }

   protected ProtocolVersion createProtocolVersion(int major, int minor) {
      return this.protocol.forVersion(major, minor);
   }

   public boolean hasProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      int index = cursor.getPos();
      String protoname = this.protocol.getProtocol();
      int protolength = protoname.length();
      if(buffer.length() < protolength + 4) {
         return false;
      } else {
         if(index < 0) {
            index = buffer.length() - 4 - protolength;
         } else if(index == 0) {
            while(index < buffer.length() && HTTP.isWhitespace(buffer.charAt(index))) {
               ++index;
            }
         }

         if(index + protolength + 4 > buffer.length()) {
            return false;
         } else {
            boolean ok = true;

            for(int j = 0; ok && j < protolength; ++j) {
               ok = buffer.charAt(index + j) == protoname.charAt(j);
            }

            if(ok) {
               ok = buffer.charAt(index + protolength) == 47;
            }

            return ok;
         }
      }
   }

   public static RequestLine parseRequestLine(String value, LineParser parser) throws ParseException {
      Args.notNull(value, "Value");
      CharArrayBuffer buffer = new CharArrayBuffer(value.length());
      buffer.append(value);
      ParserCursor cursor = new ParserCursor(0, value.length());
      return ((LineParser)(parser != null?parser:INSTANCE)).parseRequestLine(buffer, cursor);
   }

   public RequestLine parseRequestLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      try {
         this.skipWhitespace(buffer, cursor);
         int i = cursor.getPos();
         int blank = buffer.indexOf(32, i, indexTo);
         if(blank < 0) {
            throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
         } else {
            String method = buffer.substringTrimmed(i, blank);
            cursor.updatePos(blank);
            this.skipWhitespace(buffer, cursor);
            i = cursor.getPos();
            blank = buffer.indexOf(32, i, indexTo);
            if(blank < 0) {
               throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
            } else {
               String uri = buffer.substringTrimmed(i, blank);
               cursor.updatePos(blank);
               ProtocolVersion ver = this.parseProtocolVersion(buffer, cursor);
               this.skipWhitespace(buffer, cursor);
               if(!cursor.atEnd()) {
                  throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
               } else {
                  return this.createRequestLine(method, uri, ver);
               }
            }
         }
      } catch (IndexOutOfBoundsException var10) {
         throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
      }
   }

   protected RequestLine createRequestLine(String method, String uri, ProtocolVersion ver) {
      return new BasicRequestLine(method, uri, ver);
   }

   public static StatusLine parseStatusLine(String value, LineParser parser) throws ParseException {
      Args.notNull(value, "Value");
      CharArrayBuffer buffer = new CharArrayBuffer(value.length());
      buffer.append(value);
      ParserCursor cursor = new ParserCursor(0, value.length());
      return ((LineParser)(parser != null?parser:INSTANCE)).parseStatusLine(buffer, cursor);
   }

   public StatusLine parseStatusLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      try {
         ProtocolVersion ver = this.parseProtocolVersion(buffer, cursor);
         this.skipWhitespace(buffer, cursor);
         int i = cursor.getPos();
         int blank = buffer.indexOf(32, i, indexTo);
         if(blank < 0) {
            blank = indexTo;
         }

         String s = buffer.substringTrimmed(i, blank);

         for(int j = 0; j < s.length(); ++j) {
            if(!Character.isDigit(s.charAt(j))) {
               throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
            }
         }

         int statusCode;
         try {
            statusCode = Integer.parseInt(s);
         } catch (NumberFormatException var11) {
            throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
         }

         String reasonPhrase;
         if(blank < indexTo) {
            reasonPhrase = buffer.substringTrimmed(blank, indexTo);
         } else {
            reasonPhrase = "";
         }

         return this.createStatusLine(ver, statusCode, reasonPhrase);
      } catch (IndexOutOfBoundsException var12) {
         throw new ParseException("Invalid status line: " + buffer.substring(indexFrom, indexTo));
      }
   }

   protected StatusLine createStatusLine(ProtocolVersion ver, int status, String reason) {
      return new BasicStatusLine(ver, status, reason);
   }

   public static Header parseHeader(String value, LineParser parser) throws ParseException {
      Args.notNull(value, "Value");
      CharArrayBuffer buffer = new CharArrayBuffer(value.length());
      buffer.append(value);
      return ((LineParser)(parser != null?parser:INSTANCE)).parseHeader(buffer);
   }

   public Header parseHeader(CharArrayBuffer buffer) throws ParseException {
      return new BufferedHeader(buffer);
   }

   protected void skipWhitespace(CharArrayBuffer buffer, ParserCursor cursor) {
      int pos = cursor.getPos();

      for(int indexTo = cursor.getUpperBound(); pos < indexTo && HTTP.isWhitespace(buffer.charAt(pos)); ++pos) {
         ;
      }

      cursor.updatePos(pos);
   }
}
