package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.Nullable;

class MultiReader extends Reader {
   private final Iterator it;
   private Reader current;

   MultiReader(Iterator readers) throws IOException {
      this.it = readers;
      this.advance();
   }

   private void advance() throws IOException {
      this.close();
      if(this.it.hasNext()) {
         this.current = ((CharSource)this.it.next()).openStream();
      }

   }

   public int read(@Nullable char[] cbuf, int off, int len) throws IOException {
      if(this.current == null) {
         return -1;
      } else {
         int result = this.current.read(cbuf, off, len);
         if(result == -1) {
            this.advance();
            return this.read(cbuf, off, len);
         } else {
            return result;
         }
      }
   }

   public long skip(long n) throws IOException {
      Preconditions.checkArgument(n >= 0L, "n is negative");
      if(n > 0L) {
         while(this.current != null) {
            long result = this.current.skip(n);
            if(result > 0L) {
               return result;
            }

            this.advance();
         }
      }

      return 0L;
   }

   public boolean ready() throws IOException {
      return this.current != null && this.current.ready();
   }

   public void close() throws IOException {
      if(this.current != null) {
         try {
            this.current.close();
         } finally {
            this.current = null;
         }
      }

   }
}
