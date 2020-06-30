package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateInputStream extends InputStream {
   private InputStream sourceStream;

   public DeflateInputStream(InputStream wrapped) throws IOException {
      byte[] peeked = new byte[6];
      PushbackInputStream pushback = new PushbackInputStream(wrapped, peeked.length);
      int headerLength = pushback.read(peeked);
      if(headerLength == -1) {
         throw new IOException("Unable to read the response");
      } else {
         byte[] dummy = new byte[1];
         Inflater inf = new Inflater();

         try {
            int n;
            while((n = inf.inflate(dummy)) == 0) {
               if(inf.finished()) {
                  throw new IOException("Unable to read the response");
               }

               if(inf.needsDictionary()) {
                  break;
               }

               if(inf.needsInput()) {
                  inf.setInput(peeked);
               }
            }

            if(n == -1) {
               throw new IOException("Unable to read the response");
            }

            pushback.unread(peeked, 0, headerLength);
            this.sourceStream = new DeflateInputStream.DeflateStream(pushback, new Inflater());
         } catch (DataFormatException var11) {
            pushback.unread(peeked, 0, headerLength);
            this.sourceStream = new DeflateInputStream.DeflateStream(pushback, new Inflater(true));
         } finally {
            inf.end();
         }

      }
   }

   public int read() throws IOException {
      return this.sourceStream.read();
   }

   public int read(byte[] b) throws IOException {
      return this.sourceStream.read(b);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.sourceStream.read(b, off, len);
   }

   public long skip(long n) throws IOException {
      return this.sourceStream.skip(n);
   }

   public int available() throws IOException {
      return this.sourceStream.available();
   }

   public void mark(int readLimit) {
      this.sourceStream.mark(readLimit);
   }

   public void reset() throws IOException {
      this.sourceStream.reset();
   }

   public boolean markSupported() {
      return this.sourceStream.markSupported();
   }

   public void close() throws IOException {
      this.sourceStream.close();
   }

   static class DeflateStream extends InflaterInputStream {
      private boolean closed = false;

      public DeflateStream(InputStream in, Inflater inflater) {
         super(in, inflater);
      }

      public void close() throws IOException {
         if(!this.closed) {
            this.closed = true;
            this.inf.end();
            super.close();
         }
      }
   }
}
