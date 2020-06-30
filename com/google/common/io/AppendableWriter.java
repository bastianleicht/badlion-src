package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Nullable;

class AppendableWriter extends Writer {
   private final Appendable target;
   private boolean closed;

   AppendableWriter(Appendable target) {
      this.target = (Appendable)Preconditions.checkNotNull(target);
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.checkNotClosed();
      this.target.append(new String(cbuf, off, len));
   }

   public void flush() throws IOException {
      this.checkNotClosed();
      if(this.target instanceof Flushable) {
         ((Flushable)this.target).flush();
      }

   }

   public void close() throws IOException {
      this.closed = true;
      if(this.target instanceof Closeable) {
         ((Closeable)this.target).close();
      }

   }

   public void write(int c) throws IOException {
      this.checkNotClosed();
      this.target.append((char)c);
   }

   public void write(@Nullable String str) throws IOException {
      this.checkNotClosed();
      this.target.append(str);
   }

   public void write(@Nullable String str, int off, int len) throws IOException {
      this.checkNotClosed();
      this.target.append(str, off, off + len);
   }

   public Writer append(char c) throws IOException {
      this.checkNotClosed();
      this.target.append(c);
      return this;
   }

   public Writer append(@Nullable CharSequence charSeq) throws IOException {
      this.checkNotClosed();
      this.target.append(charSeq);
      return this;
   }

   public Writer append(@Nullable CharSequence charSeq, int start, int end) throws IOException {
      this.checkNotClosed();
      this.target.append(charSeq, start, end);
      return this;
   }

   private void checkNotClosed() throws IOException {
      if(this.closed) {
         throw new IOException("Cannot write to a closed writer.");
      }
   }
}
