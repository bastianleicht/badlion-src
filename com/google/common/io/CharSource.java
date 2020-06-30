package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.CharSequenceReader;
import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.MultiReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class CharSource implements InputSupplier {
   public abstract Reader openStream() throws IOException;

   /** @deprecated */
   @Deprecated
   public final Reader getInput() throws IOException {
      return this.openStream();
   }

   public BufferedReader openBufferedStream() throws IOException {
      Reader reader = this.openStream();
      return reader instanceof BufferedReader?(BufferedReader)reader:new BufferedReader(reader);
   }

   public long copyTo(Appendable appendable) throws IOException {
      Preconditions.checkNotNull(appendable);
      Closer closer = Closer.create();

      long var4;
      try {
         Reader reader = (Reader)closer.register(this.openStream());
         var4 = CharStreams.copy((Readable)reader, (Appendable)appendable);
      } catch (Throwable var9) {
         throw closer.rethrow(var9);
      } finally {
         closer.close();
      }

      return var4;
   }

   public long copyTo(CharSink sink) throws IOException {
      Preconditions.checkNotNull(sink);
      Closer closer = Closer.create();

      long var5;
      try {
         Reader reader = (Reader)closer.register(this.openStream());
         Writer writer = (Writer)closer.register(sink.openStream());
         var5 = CharStreams.copy((Readable)reader, (Appendable)writer);
      } catch (Throwable var10) {
         throw closer.rethrow(var10);
      } finally {
         closer.close();
      }

      return var5;
   }

   public String read() throws IOException {
      Closer closer = Closer.create();

      String var3;
      try {
         Reader reader = (Reader)closer.register(this.openStream());
         var3 = CharStreams.toString((Readable)reader);
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   @Nullable
   public String readFirstLine() throws IOException {
      Closer closer = Closer.create();

      String var3;
      try {
         BufferedReader reader = (BufferedReader)closer.register(this.openBufferedStream());
         var3 = reader.readLine();
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   public ImmutableList readLines() throws IOException {
      Closer closer = Closer.create();

      ImmutableList var5;
      try {
         BufferedReader reader = (BufferedReader)closer.register(this.openBufferedStream());
         List<String> result = Lists.newArrayList();

         String line;
         while((line = reader.readLine()) != null) {
            result.add(line);
         }

         var5 = ImmutableList.copyOf((Collection)result);
      } catch (Throwable var9) {
         throw closer.rethrow(var9);
      } finally {
         closer.close();
      }

      return var5;
   }

   @Beta
   public Object readLines(LineProcessor processor) throws IOException {
      Preconditions.checkNotNull(processor);
      Closer closer = Closer.create();

      Object var4;
      try {
         Reader reader = (Reader)closer.register(this.openStream());
         var4 = CharStreams.readLines((Readable)reader, processor);
      } catch (Throwable var8) {
         throw closer.rethrow(var8);
      } finally {
         closer.close();
      }

      return var4;
   }

   public boolean isEmpty() throws IOException {
      Closer closer = Closer.create();

      boolean var3;
      try {
         Reader reader = (Reader)closer.register(this.openStream());
         var3 = reader.read() == -1;
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   public static CharSource concat(Iterable sources) {
      return new CharSource.ConcatenatedCharSource(sources);
   }

   public static CharSource concat(Iterator sources) {
      return concat((Iterable)ImmutableList.copyOf(sources));
   }

   public static CharSource concat(CharSource... sources) {
      return concat((Iterable)ImmutableList.copyOf((Object[])sources));
   }

   public static CharSource wrap(CharSequence charSequence) {
      return new CharSource.CharSequenceCharSource(charSequence);
   }

   public static CharSource empty() {
      return CharSource.EmptyCharSource.INSTANCE;
   }

   private static class CharSequenceCharSource extends CharSource {
      private static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
      private final CharSequence seq;

      protected CharSequenceCharSource(CharSequence seq) {
         this.seq = (CharSequence)Preconditions.checkNotNull(seq);
      }

      public Reader openStream() {
         return new CharSequenceReader(this.seq);
      }

      public String read() {
         return this.seq.toString();
      }

      public boolean isEmpty() {
         return this.seq.length() == 0;
      }

      private Iterable lines() {
         return new Iterable() {
            public Iterator iterator() {
               return new AbstractIterator() {
                  Iterator lines;

                  {
                     this.lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();
                  }

                  protected String computeNext() {
                     if(this.lines.hasNext()) {
                        String next = (String)this.lines.next();
                        if(this.lines.hasNext() || !next.isEmpty()) {
                           return next;
                        }
                     }

                     return (String)this.endOfData();
                  }
               };
            }
         };
      }

      public String readFirstLine() {
         Iterator<String> lines = this.lines().iterator();
         return lines.hasNext()?(String)lines.next():null;
      }

      public ImmutableList readLines() {
         return ImmutableList.copyOf(this.lines());
      }

      public Object readLines(LineProcessor processor) throws IOException {
         for(String line : this.lines()) {
            if(!processor.processLine(line)) {
               break;
            }
         }

         return processor.getResult();
      }

      public String toString() {
         return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
      }
   }

   private static final class ConcatenatedCharSource extends CharSource {
      private final Iterable sources;

      ConcatenatedCharSource(Iterable sources) {
         this.sources = (Iterable)Preconditions.checkNotNull(sources);
      }

      public Reader openStream() throws IOException {
         return new MultiReader(this.sources.iterator());
      }

      public boolean isEmpty() throws IOException {
         for(CharSource source : this.sources) {
            if(!source.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public String toString() {
         return "CharSource.concat(" + this.sources + ")";
      }
   }

   private static final class EmptyCharSource extends CharSource.CharSequenceCharSource {
      private static final CharSource.EmptyCharSource INSTANCE = new CharSource.EmptyCharSource();

      private EmptyCharSource() {
         super("");
      }

      public String toString() {
         return "CharSource.empty()";
      }
   }
}
