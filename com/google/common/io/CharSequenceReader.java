package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class CharSequenceReader extends Reader {
   private CharSequence seq;
   private int pos;
   private int mark;

   public CharSequenceReader(CharSequence seq) {
      this.seq = (CharSequence)Preconditions.checkNotNull(seq);
   }

   private void checkOpen() throws IOException {
      if(this.seq == null) {
         throw new IOException("reader closed");
      }
   }

   private boolean hasRemaining() {
      return this.remaining() > 0;
   }

   private int remaining() {
      return this.seq.length() - this.pos;
   }

   public synchronized int read(CharBuffer target) throws IOException {
      Preconditions.checkNotNull(target);
      this.checkOpen();
      if(!this.hasRemaining()) {
         return -1;
      } else {
         int charsToRead = Math.min(target.remaining(), this.remaining());

         for(int i = 0; i < charsToRead; ++i) {
            target.put(this.seq.charAt(this.pos++));
         }

         return charsToRead;
      }
   }

   public synchronized int read() throws IOException {
      this.checkOpen();
      return this.hasRemaining()?this.seq.charAt(this.pos++):-1;
   }

   public synchronized int read(char[] cbuf, int off, int len) throws IOException {
      Preconditions.checkPositionIndexes(off, off + len, cbuf.length);
      this.checkOpen();
      if(!this.hasRemaining()) {
         return -1;
      } else {
         int charsToRead = Math.min(len, this.remaining());

         for(int i = 0; i < charsToRead; ++i) {
            cbuf[off + i] = this.seq.charAt(this.pos++);
         }

         return charsToRead;
      }
   }

   public synchronized long skip(long n) throws IOException {
      Preconditions.checkArgument(n >= 0L, "n (%s) may not be negative", new Object[]{Long.valueOf(n)});
      this.checkOpen();
      int charsToSkip = (int)Math.min((long)this.remaining(), n);
      this.pos += charsToSkip;
      return (long)charsToSkip;
   }

   public synchronized boolean ready() throws IOException {
      this.checkOpen();
      return true;
   }

   public boolean markSupported() {
      return true;
   }

   public synchronized void mark(int readAheadLimit) throws IOException {
      Preconditions.checkArgument(readAheadLimit >= 0, "readAheadLimit (%s) may not be negative", new Object[]{Integer.valueOf(readAheadLimit)});
      this.checkOpen();
      this.mark = this.pos;
   }

   public synchronized void reset() throws IOException {
      this.checkOpen();
      this.pos = this.mark;
   }

   public synchronized void close() throws IOException {
      this.seq = null;
   }
}
