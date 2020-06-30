package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.OutputSupplier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class CharSink implements OutputSupplier {
   public abstract Writer openStream() throws IOException;

   /** @deprecated */
   @Deprecated
   public final Writer getOutput() throws IOException {
      return this.openStream();
   }

   public Writer openBufferedStream() throws IOException {
      Writer writer = this.openStream();
      return writer instanceof BufferedWriter?(BufferedWriter)writer:new BufferedWriter(writer);
   }

   public void write(CharSequence charSequence) throws IOException {
      Preconditions.checkNotNull(charSequence);
      Closer closer = Closer.create();

      try {
         Writer out = (Writer)closer.register(this.openStream());
         out.append(charSequence);
         out.flush();
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

   }

   public void writeLines(Iterable lines) throws IOException {
      this.writeLines(lines, System.getProperty("line.separator"));
   }

   public void writeLines(Iterable lines, String lineSeparator) throws IOException {
      Preconditions.checkNotNull(lines);
      Preconditions.checkNotNull(lineSeparator);
      Closer closer = Closer.create();

      try {
         Writer out = (Writer)closer.register(this.openBufferedStream());

         for(CharSequence line : lines) {
            out.append(line).append(lineSeparator);
         }

         out.flush();
      } catch (Throwable var10) {
         throw closer.rethrow(var10);
      } finally {
         closer.close();
      }

   }

   public long writeFrom(Readable readable) throws IOException {
      Preconditions.checkNotNull(readable);
      Closer closer = Closer.create();

      long var6;
      try {
         Writer out = (Writer)closer.register(this.openStream());
         long written = CharStreams.copy((Readable)readable, (Appendable)out);
         out.flush();
         var6 = written;
      } catch (Throwable var11) {
         throw closer.rethrow(var11);
      } finally {
         closer.close();
      }

      return var6;
   }
}
