package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
public class BasicHeader implements Header, Cloneable, Serializable {
   private static final long serialVersionUID = -5427236326487562174L;
   private final String name;
   private final String value;

   public BasicHeader(String name, String value) {
      this.name = (String)Args.notNull(name, "Name");
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return BasicLineFormatter.INSTANCE.formatHeader((CharArrayBuffer)null, (Header)this).toString();
   }

   public HeaderElement[] getElements() throws ParseException {
      return this.value != null?BasicHeaderValueParser.parseElements((String)this.value, (HeaderValueParser)null):new HeaderElement[0];
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
