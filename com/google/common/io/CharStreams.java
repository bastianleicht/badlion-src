package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.io.AppendableWriter;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.LineReader;
import com.google.common.io.OutputSupplier;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Beta
public final class CharStreams {
   private static final int BUF_SIZE = 2048;

   /** @deprecated */
   @Deprecated
   public static InputSupplier newReaderSupplier(String value) {
      return asInputSupplier(CharSource.wrap(value));
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier newReaderSupplier(InputSupplier in, Charset charset) {
      return asInputSupplier(ByteStreams.asByteSource(in).asCharSource(charset));
   }

   /** @deprecated */
   @Deprecated
   public static OutputSupplier newWriterSupplier(OutputSupplier out, Charset charset) {
      return asOutputSupplier(ByteStreams.asByteSink(out).asCharSink(charset));
   }

   /** @deprecated */
   @Deprecated
   public static void write(CharSequence from, OutputSupplier to) throws IOException {
      asCharSink(to).write(from);
   }

   /** @deprecated */
   @Deprecated
   public static long copy(InputSupplier from, OutputSupplier to) throws IOException {
      return asCharSource(from).copyTo(asCharSink(to));
   }

   /** @deprecated */
   @Deprecated
   public static long copy(InputSupplier from, Appendable to) throws IOException {
      return asCharSource(from).copyTo(to);
   }

   public static long copy(Readable from, Appendable to) throws IOException {
      Preconditions.checkNotNull(from);
      Preconditions.checkNotNull(to);
      CharBuffer buf = CharBuffer.allocate(2048);
      long total = 0L;

      while(from.read(buf) != -1) {
         buf.flip();
         to.append(buf);
         total += (long)buf.remaining();
         buf.clear();
      }

      return total;
   }

   public static String toString(Readable r) throws IOException {
      return toStringBuilder(r).toString();
   }

   /** @deprecated */
   @Deprecated
   public static String toString(InputSupplier supplier) throws IOException {
      return asCharSource(supplier).read();
   }

   private static StringBuilder toStringBuilder(Readable r) throws IOException {
      StringBuilder sb = new StringBuilder();
      copy((Readable)r, (Appendable)sb);
      return sb;
   }

   /** @deprecated */
   @Deprecated
   public static String readFirstLine(InputSupplier supplier) throws IOException {
      return asCharSource(supplier).readFirstLine();
   }

   /** @deprecated */
   @Deprecated
   public static List readLines(InputSupplier supplier) throws IOException {
      Closer closer = Closer.create();

      List var3;
      try {
         R r = (Readable)closer.register((Closeable)supplier.getInput());
         var3 = readLines(r);
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   public static List readLines(Readable r) throws IOException {
      List<String> result = new ArrayList();
      LineReader lineReader = new LineReader(r);

      String line;
      while((line = lineReader.readLine()) != null) {
         result.add(line);
      }

      return result;
   }

   public static Object readLines(Readable readable, LineProcessor processor) throws IOException {
      Preconditions.checkNotNull(readable);
      Preconditions.checkNotNull(processor);
      LineReader lineReader = new LineReader(readable);

      String line;
      while((line = lineReader.readLine()) != null && processor.processLine(line)) {
         ;
      }

      return processor.getResult();
   }

   /** @deprecated */
   @Deprecated
   public static Object readLines(InputSupplier supplier, LineProcessor callback) throws IOException {
      Preconditions.checkNotNull(supplier);
      Preconditions.checkNotNull(callback);
      Closer closer = Closer.create();

      Object var4;
      try {
         R r = (Readable)closer.register((Closeable)supplier.getInput());
         var4 = readLines(r, callback);
      } catch (Throwable var8) {
         throw closer.rethrow(var8);
      } finally {
         closer.close();
      }

      return var4;
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier join(Iterable suppliers) {
      Preconditions.checkNotNull(suppliers);
      Iterable<CharSource> sources = Iterables.transform(suppliers, new Function() {
         public CharSource apply(InputSupplier input) {
            return CharStreams.asCharSource(input);
         }
      });
      return asInputSupplier(CharSource.concat(sources));
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier join(InputSupplier... suppliers) {
      return join((Iterable)Arrays.asList(suppliers));
   }

   public static void skipFully(Reader reader, long n) throws IOException {
      Preconditions.checkNotNull(reader);

      while(n > 0L) {
         long amt = reader.skip(n);
         if(amt == 0L) {
            if(reader.read() == -1) {
               throw new EOFException();
            }

            --n;
         } else {
            n -= amt;
         }
      }

   }

   public static Writer nullWriter() {
      return CharStreams.NullWriter.INSTANCE;
   }

   public static Writer asWriter(Appendable target) {
      return (Writer)(target instanceof Writer?(Writer)target:new AppendableWriter(target));
   }

   static Reader asReader(final Readable readable) {
      Preconditions.checkNotNull(readable);
      return readable instanceof Reader?(Reader)readable:new Reader() {
         public int read(char[] cbuf, int off, int len) throws IOException {
            return this.read(CharBuffer.wrap(cbuf, off, len));
         }

         public int read(CharBuffer target) throws IOException {
            return readable.read(target);
         }

         public void close() throws IOException {
            if(readable instanceof Closeable) {
               ((Closeable)readable).close();
            }

         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static CharSource asCharSource(final InputSupplier supplier) {
      Preconditions.checkNotNull(supplier);
      return new CharSource() {
         public Reader openStream() throws IOException {
            return CharStreams.asReader((Readable)supplier.getInput());
         }

         public String toString() {
            return "CharStreams.asCharSource(" + supplier + ")";
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static CharSink asCharSink(final OutputSupplier supplier) {
      Preconditions.checkNotNull(supplier);
      return new CharSink() {
         public Writer openStream() throws IOException {
            return CharStreams.asWriter((Appendable)supplier.getOutput());
         }

         public String toString() {
            return "CharStreams.asCharSink(" + supplier + ")";
         }
      };
   }

   static InputSupplier asInputSupplier(CharSource source) {
      return (InputSupplier)Preconditions.checkNotNull(source);
   }

   static OutputSupplier asOutputSupplier(CharSink sink) {
      return (OutputSupplier)Preconditions.checkNotNull(sink);
   }

   private static final class NullWriter extends Writer {
      private static final CharStreams.NullWriter INSTANCE = new CharStreams.NullWriter();

      public void write(int c) {
      }

      public void write(char[] cbuf) {
         Preconditions.checkNotNull(cbuf);
      }

      public void write(char[] cbuf, int off, int len) {
         Preconditions.checkPositionIndexes(off, off + len, cbuf.length);
      }

      public void write(String str) {
         Preconditions.checkNotNull(str);
      }

      public void write(String str, int off, int len) {
         Preconditions.checkPositionIndexes(off, off + len, str.length());
      }

      public Writer append(CharSequence csq) {
         Preconditions.checkNotNull(csq);
         return this;
      }

      public Writer append(CharSequence csq, int start, int end) {
         Preconditions.checkPositionIndexes(start, end, csq.length());
         return this;
      }

      public Writer append(char c) {
         return this;
      }

      public void flush() {
      }

      public void close() {
      }

      public String toString() {
         return "CharStreams.nullWriter()";
      }
   }
}
