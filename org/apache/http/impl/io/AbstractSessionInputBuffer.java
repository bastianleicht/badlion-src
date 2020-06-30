package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.Consts;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
@NotThreadSafe
public abstract class AbstractSessionInputBuffer implements SessionInputBuffer, BufferInfo {
   private InputStream instream;
   private byte[] buffer;
   private ByteArrayBuffer linebuffer;
   private Charset charset;
   private boolean ascii;
   private int maxLineLen;
   private int minChunkLimit;
   private HttpTransportMetricsImpl metrics;
   private CodingErrorAction onMalformedCharAction;
   private CodingErrorAction onUnmappableCharAction;
   private int bufferpos;
   private int bufferlen;
   private CharsetDecoder decoder;
   private CharBuffer cbuf;

   protected void init(InputStream instream, int buffersize, HttpParams params) {
      Args.notNull(instream, "Input stream");
      Args.notNegative(buffersize, "Buffer size");
      Args.notNull(params, "HTTP parameters");
      this.instream = instream;
      this.buffer = new byte[buffersize];
      this.bufferpos = 0;
      this.bufferlen = 0;
      this.linebuffer = new ByteArrayBuffer(buffersize);
      String charset = (String)params.getParameter("http.protocol.element-charset");
      this.charset = charset != null?Charset.forName(charset):Consts.ASCII;
      this.ascii = this.charset.equals(Consts.ASCII);
      this.decoder = null;
      this.maxLineLen = params.getIntParameter("http.connection.max-line-length", -1);
      this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
      this.metrics = this.createTransportMetrics();
      CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
      this.onMalformedCharAction = a1 != null?a1:CodingErrorAction.REPORT;
      CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
      this.onUnmappableCharAction = a2 != null?a2:CodingErrorAction.REPORT;
   }

   protected HttpTransportMetricsImpl createTransportMetrics() {
      return new HttpTransportMetricsImpl();
   }

   public int capacity() {
      return this.buffer.length;
   }

   public int length() {
      return this.bufferlen - this.bufferpos;
   }

   public int available() {
      return this.capacity() - this.length();
   }

   protected int fillBuffer() throws IOException {
      if(this.bufferpos > 0) {
         int len = this.bufferlen - this.bufferpos;
         if(len > 0) {
            System.arraycopy(this.buffer, this.bufferpos, this.buffer, 0, len);
         }

         this.bufferpos = 0;
         this.bufferlen = len;
      }

      int off = this.bufferlen;
      int len = this.buffer.length - off;
      int l = this.instream.read(this.buffer, off, len);
      if(l == -1) {
         return -1;
      } else {
         this.bufferlen = off + l;
         this.metrics.incrementBytesTransferred((long)l);
         return l;
      }
   }

   protected boolean hasBufferedData() {
      return this.bufferpos < this.bufferlen;
   }

   public int read() throws IOException {
      while(true) {
         if(!this.hasBufferedData()) {
            int noRead = this.fillBuffer();
            if(noRead != -1) {
               continue;
            }

            return -1;
         }

         return this.buffer[this.bufferpos++] & 255;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if(b == null) {
         return 0;
      } else if(this.hasBufferedData()) {
         int chunk = Math.min(len, this.bufferlen - this.bufferpos);
         System.arraycopy(this.buffer, this.bufferpos, b, off, chunk);
         this.bufferpos += chunk;
         return chunk;
      } else if(len > this.minChunkLimit) {
         int read = this.instream.read(b, off, len);
         if(read > 0) {
            this.metrics.incrementBytesTransferred((long)read);
         }

         return read;
      } else {
         while(!this.hasBufferedData()) {
            int noRead = this.fillBuffer();
            if(noRead == -1) {
               return -1;
            }
         }

         int chunk = Math.min(len, this.bufferlen - this.bufferpos);
         System.arraycopy(this.buffer, this.bufferpos, b, off, chunk);
         this.bufferpos += chunk;
         return chunk;
      }
   }

   public int read(byte[] b) throws IOException {
      return b == null?0:this.read(b, 0, b.length);
   }

   private int locateLF() {
      for(int i = this.bufferpos; i < this.bufferlen; ++i) {
         if(this.buffer[i] == 10) {
            return i;
         }
      }

      return -1;
   }

   public int readLine(CharArrayBuffer charbuffer) throws IOException {
      Args.notNull(charbuffer, "Char array buffer");
      int noRead = 0;
      boolean retry = true;

      while(retry) {
         int i = this.locateLF();
         if(i != -1) {
            if(this.linebuffer.isEmpty()) {
               return this.lineFromReadBuffer(charbuffer, i);
            }

            retry = false;
            int len = i + 1 - this.bufferpos;
            this.linebuffer.append(this.buffer, this.bufferpos, len);
            this.bufferpos = i + 1;
         } else {
            if(this.hasBufferedData()) {
               int len = this.bufferlen - this.bufferpos;
               this.linebuffer.append(this.buffer, this.bufferpos, len);
               this.bufferpos = this.bufferlen;
            }

            noRead = this.fillBuffer();
            if(noRead == -1) {
               retry = false;
            }
         }

         if(this.maxLineLen > 0 && this.linebuffer.length() >= this.maxLineLen) {
            throw new IOException("Maximum line length limit exceeded");
         }
      }

      if(noRead == -1 && this.linebuffer.isEmpty()) {
         return -1;
      } else {
         return this.lineFromLineBuffer(charbuffer);
      }
   }

   private int lineFromLineBuffer(CharArrayBuffer charbuffer) throws IOException {
      int len = this.linebuffer.length();
      if(len > 0) {
         if(this.linebuffer.byteAt(len - 1) == 10) {
            --len;
         }

         if(len > 0 && this.linebuffer.byteAt(len - 1) == 13) {
            --len;
         }
      }

      if(this.ascii) {
         charbuffer.append((ByteArrayBuffer)this.linebuffer, 0, len);
      } else {
         ByteBuffer bbuf = ByteBuffer.wrap(this.linebuffer.buffer(), 0, len);
         len = this.appendDecoded(charbuffer, bbuf);
      }

      this.linebuffer.clear();
      return len;
   }

   private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
      int off = this.bufferpos;
      int i = position;
      this.bufferpos = position + 1;
      if(position > off && this.buffer[position - 1] == 13) {
         i = position - 1;
      }

      int len = i - off;
      if(this.ascii) {
         charbuffer.append(this.buffer, off, len);
      } else {
         ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
         len = this.appendDecoded(charbuffer, bbuf);
      }

      return len;
   }

   private int appendDecoded(CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
      if(!bbuf.hasRemaining()) {
         return 0;
      } else {
         if(this.decoder == null) {
            this.decoder = this.charset.newDecoder();
            this.decoder.onMalformedInput(this.onMalformedCharAction);
            this.decoder.onUnmappableCharacter(this.onUnmappableCharAction);
         }

         if(this.cbuf == null) {
            this.cbuf = CharBuffer.allocate(1024);
         }

         this.decoder.reset();

         int len;
         CoderResult result;
         for(len = 0; bbuf.hasRemaining(); len += this.handleDecodingResult(result, charbuffer, bbuf)) {
            result = this.decoder.decode(bbuf, this.cbuf, true);
         }

         result = this.decoder.flush(this.cbuf);
         len = len + this.handleDecodingResult(result, charbuffer, bbuf);
         this.cbuf.clear();
         return len;
      }
   }

   private int handleDecodingResult(CoderResult result, CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
      if(result.isError()) {
         result.throwException();
      }

      this.cbuf.flip();
      int len = this.cbuf.remaining();

      while(this.cbuf.hasRemaining()) {
         charbuffer.append(this.cbuf.get());
      }

      this.cbuf.compact();
      return len;
   }

   public String readLine() throws IOException {
      CharArrayBuffer charbuffer = new CharArrayBuffer(64);
      int l = this.readLine(charbuffer);
      return l != -1?charbuffer.toString():null;
   }

   public HttpTransportMetrics getMetrics() {
      return this.metrics;
   }
}
