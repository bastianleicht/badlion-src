package org.apache.commons.io.input;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class NullInputStream extends InputStream {
   private final long size;
   private long position;
   private long mark;
   private long readlimit;
   private boolean eof;
   private final boolean throwEofException;
   private final boolean markSupported;

   public NullInputStream(long size) {
      this(size, true, false);
   }

   public NullInputStream(long size, boolean markSupported, boolean throwEofException) {
      this.mark = -1L;
      this.size = size;
      this.markSupported = markSupported;
      this.throwEofException = throwEofException;
   }

   public long getPosition() {
      return this.position;
   }

   public long getSize() {
      return this.size;
   }

   public int available() {
      long avail = this.size - this.position;
      return avail <= 0L?0:(avail > 2147483647L?Integer.MAX_VALUE:(int)avail);
   }

   public void close() throws IOException {
      this.eof = false;
      this.position = 0L;
      this.mark = -1L;
   }

   public synchronized void mark(int readlimit) {
      if(!this.markSupported) {
         throw new UnsupportedOperationException("Mark not supported");
      } else {
         this.mark = this.position;
         this.readlimit = (long)readlimit;
      }
   }

   public boolean markSupported() {
      return this.markSupported;
   }

   public int read() throws IOException {
      if(this.eof) {
         throw new IOException("Read after end of file");
      } else if(this.position == this.size) {
         return this.doEndOfFile();
      } else {
         ++this.position;
         return this.processByte();
      }
   }

   public int read(byte[] bytes) throws IOException {
      return this.read(bytes, 0, bytes.length);
   }

   public int read(byte[] bytes, int offset, int length) throws IOException {
      if(this.eof) {
         throw new IOException("Read after end of file");
      } else if(this.position == this.size) {
         return this.doEndOfFile();
      } else {
         this.position += (long)length;
         int returnLength = length;
         if(this.position > this.size) {
            returnLength = length - (int)(this.position - this.size);
            this.position = this.size;
         }

         this.processBytes(bytes, offset, returnLength);
         return returnLength;
      }
   }

   public synchronized void reset() throws IOException {
      if(!this.markSupported) {
         throw new UnsupportedOperationException("Mark not supported");
      } else if(this.mark < 0L) {
         throw new IOException("No position has been marked");
      } else if(this.position > this.mark + this.readlimit) {
         throw new IOException("Marked position [" + this.mark + "] is no longer valid - passed the read limit [" + this.readlimit + "]");
      } else {
         this.position = this.mark;
         this.eof = false;
      }
   }

   public long skip(long numberOfBytes) throws IOException {
      if(this.eof) {
         throw new IOException("Skip after end of file");
      } else if(this.position == this.size) {
         return (long)this.doEndOfFile();
      } else {
         this.position += numberOfBytes;
         long returnLength = numberOfBytes;
         if(this.position > this.size) {
            returnLength = numberOfBytes - (this.position - this.size);
            this.position = this.size;
         }

         return returnLength;
      }
   }

   protected int processByte() {
      return 0;
   }

   protected void processBytes(byte[] bytes, int offset, int length) {
   }

   private int doEndOfFile() throws EOFException {
      this.eof = true;
      if(this.throwEofException) {
         throw new EOFException();
      } else {
         return -1;
      }
   }
}
