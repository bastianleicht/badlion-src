package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
public class NetscapeDraftHeaderParser {
   public static final NetscapeDraftHeaderParser DEFAULT = new NetscapeDraftHeaderParser();

   public HeaderElement parseHeader(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
      Args.notNull(buffer, "Char array buffer");
      Args.notNull(cursor, "Parser cursor");
      NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
      List<NameValuePair> params = new ArrayList();

      while(!cursor.atEnd()) {
         NameValuePair param = this.parseNameValuePair(buffer, cursor);
         params.add(param);
      }

      return new BasicHeaderElement(nvp.getName(), nvp.getValue(), (NameValuePair[])params.toArray(new NameValuePair[params.size()]));
   }

   private NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
      boolean terminated = false;
      int pos = cursor.getPos();
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      for(String name = null; pos < indexTo; ++pos) {
         char ch = buffer.charAt(pos);
         if(ch == 61) {
            break;
         }

         if(ch == 59) {
            terminated = true;
            break;
         }
      }

      String var11;
      if(pos == indexTo) {
         terminated = true;
         var11 = buffer.substringTrimmed(indexFrom, indexTo);
      } else {
         var11 = buffer.substringTrimmed(indexFrom, pos);
         ++pos;
      }

      if(terminated) {
         cursor.updatePos(pos);
         return new BasicNameValuePair(var11, (String)null);
      } else {
         String value = null;

         int i1;
         for(i1 = pos; pos < indexTo; ++pos) {
            char ch = buffer.charAt(pos);
            if(ch == 59) {
               terminated = true;
               break;
            }
         }

         int i2;
         for(i2 = pos; i1 < i2 && HTTP.isWhitespace(buffer.charAt(i1)); ++i1) {
            ;
         }

         while(i2 > i1 && HTTP.isWhitespace(buffer.charAt(i2 - 1))) {
            --i2;
         }

         value = buffer.substring(i1, i2);
         if(terminated) {
            ++pos;
         }

         cursor.updatePos(pos);
         return new BasicNameValuePair(var11, value);
      }
   }
}
