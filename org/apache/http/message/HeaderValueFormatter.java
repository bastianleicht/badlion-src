package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;

public interface HeaderValueFormatter {
   CharArrayBuffer formatElements(CharArrayBuffer var1, HeaderElement[] var2, boolean var3);

   CharArrayBuffer formatHeaderElement(CharArrayBuffer var1, HeaderElement var2, boolean var3);

   CharArrayBuffer formatParameters(CharArrayBuffer var1, NameValuePair[] var2, boolean var3);

   CharArrayBuffer formatNameValuePair(CharArrayBuffer var1, NameValuePair var2, boolean var3);
}
