package org.apache.http.message;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@NotThreadSafe
public class BasicListHeaderIterator implements HeaderIterator {
   protected final List allHeaders;
   protected int currentIndex;
   protected int lastIndex;
   protected String headerName;

   public BasicListHeaderIterator(List headers, String name) {
      this.allHeaders = (List)Args.notNull(headers, "Header list");
      this.headerName = name;
      this.currentIndex = this.findNext(-1);
      this.lastIndex = -1;
   }

   protected int findNext(int pos) {
      int from = pos;
      if(pos < -1) {
         return -1;
      } else {
         int to = this.allHeaders.size() - 1;

         boolean found;
         for(found = false; !found && from < to; found = this.filterHeader(from)) {
            ++from;
         }

         return found?from:-1;
      }
   }

   protected boolean filterHeader(int index) {
      if(this.headerName == null) {
         return true;
      } else {
         String name = ((Header)this.allHeaders.get(index)).getName();
         return this.headerName.equalsIgnoreCase(name);
      }
   }

   public boolean hasNext() {
      return this.currentIndex >= 0;
   }

   public Header nextHeader() throws NoSuchElementException {
      int current = this.currentIndex;
      if(current < 0) {
         throw new NoSuchElementException("Iteration already finished.");
      } else {
         this.lastIndex = current;
         this.currentIndex = this.findNext(current);
         return (Header)this.allHeaders.get(current);
      }
   }

   public final Object next() throws NoSuchElementException {
      return this.nextHeader();
   }

   public void remove() throws UnsupportedOperationException {
      Asserts.check(this.lastIndex >= 0, "No header to remove");
      this.allHeaders.remove(this.lastIndex);
      this.lastIndex = -1;
      --this.currentIndex;
   }
}
