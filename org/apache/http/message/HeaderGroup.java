package org.apache.http.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicListHeaderIterator;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class HeaderGroup implements Cloneable, Serializable {
   private static final long serialVersionUID = 2608834160639271617L;
   private final List headers = new ArrayList(16);

   public void clear() {
      this.headers.clear();
   }

   public void addHeader(Header header) {
      if(header != null) {
         this.headers.add(header);
      }
   }

   public void removeHeader(Header header) {
      if(header != null) {
         this.headers.remove(header);
      }
   }

   public void updateHeader(Header header) {
      if(header != null) {
         for(int i = 0; i < this.headers.size(); ++i) {
            Header current = (Header)this.headers.get(i);
            if(current.getName().equalsIgnoreCase(header.getName())) {
               this.headers.set(i, header);
               return;
            }
         }

         this.headers.add(header);
      }
   }

   public void setHeaders(Header[] headers) {
      this.clear();
      if(headers != null) {
         Collections.addAll(this.headers, headers);
      }
   }

   public Header getCondensedHeader(String name) {
      Header[] hdrs = this.getHeaders(name);
      if(hdrs.length == 0) {
         return null;
      } else if(hdrs.length == 1) {
         return hdrs[0];
      } else {
         CharArrayBuffer valueBuffer = new CharArrayBuffer(128);
         valueBuffer.append(hdrs[0].getValue());

         for(int i = 1; i < hdrs.length; ++i) {
            valueBuffer.append(", ");
            valueBuffer.append(hdrs[i].getValue());
         }

         return new BasicHeader(name.toLowerCase(Locale.ENGLISH), valueBuffer.toString());
      }
   }

   public Header[] getHeaders(String name) {
      List<Header> headersFound = new ArrayList();

      for(int i = 0; i < this.headers.size(); ++i) {
         Header header = (Header)this.headers.get(i);
         if(header.getName().equalsIgnoreCase(name)) {
            headersFound.add(header);
         }
      }

      return (Header[])headersFound.toArray(new Header[headersFound.size()]);
   }

   public Header getFirstHeader(String name) {
      for(int i = 0; i < this.headers.size(); ++i) {
         Header header = (Header)this.headers.get(i);
         if(header.getName().equalsIgnoreCase(name)) {
            return header;
         }
      }

      return null;
   }

   public Header getLastHeader(String name) {
      for(int i = this.headers.size() - 1; i >= 0; --i) {
         Header header = (Header)this.headers.get(i);
         if(header.getName().equalsIgnoreCase(name)) {
            return header;
         }
      }

      return null;
   }

   public Header[] getAllHeaders() {
      return (Header[])this.headers.toArray(new Header[this.headers.size()]);
   }

   public boolean containsHeader(String name) {
      for(int i = 0; i < this.headers.size(); ++i) {
         Header header = (Header)this.headers.get(i);
         if(header.getName().equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   public HeaderIterator iterator() {
      return new BasicListHeaderIterator(this.headers, (String)null);
   }

   public HeaderIterator iterator(String name) {
      return new BasicListHeaderIterator(this.headers, name);
   }

   public HeaderGroup copy() {
      HeaderGroup clone = new HeaderGroup();
      clone.headers.addAll(this.headers);
      return clone;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public String toString() {
      return this.headers.toString();
   }
}
