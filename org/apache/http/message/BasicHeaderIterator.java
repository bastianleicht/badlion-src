package org.apache.http.message;

import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.util.Args;

@NotThreadSafe
public class BasicHeaderIterator implements HeaderIterator {
   protected final Header[] allHeaders;
   protected int currentIndex;
   protected String headerName;

   public BasicHeaderIterator(Header[] headers, String name) {
      this.allHeaders = (Header[])Args.notNull(headers, "Header array");
      this.headerName = name;
      this.currentIndex = this.findNext(-1);
   }

   protected int findNext(int pos) {
      int from = pos;
      if(pos < -1) {
         return -1;
      } else {
         int to = this.allHeaders.length - 1;

         boolean found;
         for(found = false; !found && from < to; found = this.filterHeader(from)) {
            ++from;
         }

         return found?from:-1;
      }
   }

   protected boolean filterHeader(int index) {
      return this.headerName == null || this.headerName.equalsIgnoreCase(this.allHeaders[index].getName());
   }

   public boolean hasNext() {
      return this.currentIndex >= 0;
   }

   public Header nextHeader() throws NoSuchElementException {
      int current = this.currentIndex;
      if(current < 0) {
         throw new NoSuchElementException("Iteration already finished.");
      } else {
         this.currentIndex = this.findNext(current);
         return this.allHeaders[current];
      }
   }

   public final Object next() throws NoSuchElementException {
      return this.nextHeader();
   }

   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Removing headers is not supported.");
   }
}
