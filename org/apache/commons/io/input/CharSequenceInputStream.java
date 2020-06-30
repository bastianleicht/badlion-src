package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class CharSequenceInputStream extends InputStream {
   private final CharsetEncoder encoder;
   private final CharBuffer cbuf;
   private final ByteBuffer bbuf;
   private int mark;

   public CharSequenceInputStream(CharSequence s, Charset charset, int bufferSize) {
      this.encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.bbuf = ByteBuffer.allocate(bufferSize);
      this.bbuf.flip();
      this.cbuf = CharBuffer.wrap(s);
      this.mark = -1;
   }

   public CharSequenceInputStream(CharSequence s, String charset, int bufferSize) {
      this(s, Charset.forName(charset), bufferSize);
   }

   public CharSequenceInputStream(CharSequence s, Charset charset) {
      this(s, (Charset)charset, 2048);
   }

   public CharSequenceInputStream(CharSequence s, String charset) {
      this(s, (String)charset, 2048);
   }

   private void fillBuffer() throws CharacterCodingException {
      this.bbuf.compact();
      CoderResult result = this.encoder.encode(this.cbuf, this.bbuf, true);
      if(result.isError()) {
         result.throwException();
      }

      this.bbuf.flip();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if(b == null) {
         throw new NullPointerException("Byte array is null");
      } else if(len >= 0 && off + len <= b.length) {
         if(len == 0) {
            return 0;
         } else if(!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
            return -1;
         } else {
            int bytesRead = 0;

            while(len > 0) {
               if(this.bbuf.hasRemaining()) {
                  int chunk = Math.min(this.bbuf.remaining(), len);
                  this.bbuf.get(b, off, chunk);
                  off += chunk;
                  len -= chunk;
                  bytesRead += chunk;
               } else {
                  this.fillBuffer();
                  if(!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
                     break;
                  }
               }
            }

            return bytesRead == 0 && !this.cbuf.hasRemaining()?-1:bytesRead;
         }
      } else {
         throw new IndexOutOfBoundsException("Array Size=" + b.length + ", offset=" + off + ", length=" + len);
      }
   }

   public int read() throws IOException {
      while(!this.bbuf.hasRemaining()) {
         this.fillBuffer();
         if(!this.bbuf.hasRemaining() && !this.cbuf.hasRemaining()) {
            return -1;
         }
      }

      return this.bbuf.get() & 255;
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public long skip(long n) throws IOException {
      int skipped;
      for(skipped = 0; n > 0L && this.cbuf.hasRemaining(); ++skipped) {
         this.cbuf.get();
         --n;
      }

      return (long)skipped;
   }

   public int available() throws IOException {
      return this.cbuf.remaining();
   }

   public void close() throws IOException {
   }

   public synchronized void mark(int readlimit) {
      this.mark = this.cbuf.position();
   }

   public synchronized void reset() throws IOException {
      if(this.mark != -1) {
         this.cbuf.position(this.mark);
         this.mark = -1;
      }

   }

   public boolean markSupported() {
      return true;
   }
}
