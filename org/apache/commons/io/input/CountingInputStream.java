package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.ProxyInputStream;

public class CountingInputStream extends ProxyInputStream {
   private long count;

   public CountingInputStream(InputStream in) {
      super(in);
   }

   public synchronized long skip(long length) throws IOException {
      long skip = super.skip(length);
      this.count += skip;
      return skip;
   }

   protected synchronized void afterRead(int n) {
      if(n != -1) {
         this.count += (long)n;
      }

   }

   public int getCount() {
      long result = this.getByteCount();
      if(result > 2147483647L) {
         throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
      } else {
         return (int)result;
      }
   }

   public int resetCount() {
      long result = this.resetByteCount();
      if(result > 2147483647L) {
         throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
      } else {
         return (int)result;
      }
   }

   public synchronized long getByteCount() {
      return this.count;
   }

   public synchronized long resetByteCount() {
      long tmp = this.count;
      this.count = 0L;
      return tmp;
   }
}
