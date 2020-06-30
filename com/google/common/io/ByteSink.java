package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;
import com.google.common.io.Closer;
import com.google.common.io.OutputSupplier;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public abstract class ByteSink implements OutputSupplier {
   public CharSink asCharSink(Charset charset) {
      return new ByteSink.AsCharSink(charset);
   }

   public abstract OutputStream openStream() throws IOException;

   /** @deprecated */
   @Deprecated
   public final OutputStream getOutput() throws IOException {
      return this.openStream();
   }

   public OutputStream openBufferedStream() throws IOException {
      OutputStream out = this.openStream();
      return out instanceof BufferedOutputStream?(BufferedOutputStream)out:new BufferedOutputStream(out);
   }

   public void write(byte[] bytes) throws IOException {
      Preconditions.checkNotNull(bytes);
      Closer closer = Closer.create();

      try {
         OutputStream out = (OutputStream)closer.register(this.openStream());
         out.write(bytes);
         out.flush();
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

   }

   public long writeFrom(InputStream input) throws IOException {
      Preconditions.checkNotNull(input);
      Closer closer = Closer.create();

      long var6;
      try {
         OutputStream out = (OutputStream)closer.register(this.openStream());
         long written = ByteStreams.copy(input, out);
         out.flush();
         var6 = written;
      } catch (Throwable var11) {
         throw closer.rethrow(var11);
      } finally {
         closer.close();
      }

      return var6;
   }

   private final class AsCharSink extends CharSink {
      private final Charset charset;

      private AsCharSink(Charset charset) {
         this.charset = (Charset)Preconditions.checkNotNull(charset);
      }

      public Writer openStream() throws IOException {
         return new OutputStreamWriter(ByteSink.this.openStream(), this.charset);
      }

      public String toString() {
         return ByteSink.this.toString() + ".asCharSink(" + this.charset + ")";
      }
   }
}
