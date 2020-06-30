package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

public interface HeaderValueParser {
   HeaderElement[] parseElements(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

   HeaderElement parseHeaderElement(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

   NameValuePair[] parseParameters(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

   NameValuePair parseNameValuePair(CharArrayBuffer var1, ParserCursor var2) throws ParseException;
}
