package org.apache.http;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.ProtocolVersion;
import org.apache.http.params.HttpParams;

public interface HttpMessage {
   ProtocolVersion getProtocolVersion();

   boolean containsHeader(String var1);

   Header[] getHeaders(String var1);

   Header getFirstHeader(String var1);

   Header getLastHeader(String var1);

   Header[] getAllHeaders();

   void addHeader(Header var1);

   void addHeader(String var1, String var2);

   void setHeader(Header var1);

   void setHeader(String var1, String var2);

   void setHeaders(Header[] var1);

   void removeHeader(Header var1);

   void removeHeaders(String var1);

   HeaderIterator headerIterator();

   HeaderIterator headerIterator(String var1);

   /** @deprecated */
   @Deprecated
   HttpParams getParams();

   /** @deprecated */
   @Deprecated
   void setParams(HttpParams var1);
}
