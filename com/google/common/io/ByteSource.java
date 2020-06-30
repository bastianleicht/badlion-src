package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.InputSupplier;
import com.google.common.io.MultiInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

public abstract class ByteSource implements InputSupplier {
   private static final int BUF_SIZE = 4096;
   private static final byte[] countBuffer = new byte[4096];

   public CharSource asCharSource(Charset charset) {
      return new ByteSource.AsCharSource(charset);
   }

   public abstract InputStream openStream() throws IOException;

   /** @deprecated */
   @Deprecated
   public final InputStream getInput() throws IOException {
      return this.openStream();
   }

   public InputStream openBufferedStream() throws IOException {
      InputStream in = this.openStream();
      return in instanceof BufferedInputStream?(BufferedInputStream)in:new BufferedInputStream(in);
   }

   public ByteSource slice(long offset, long length) {
      return new ByteSource.SlicedByteSource(offset, length);
   }

   public boolean isEmpty() throws IOException {
      Closer closer = Closer.create();

      boolean var3;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         var3 = in.read() == -1;
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   public long size() throws IOException {
      Closer closer = Closer.create();

      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         long var3 = this.countBySkipping(in);
         return var3;
      } catch (IOException var17) {
         ;
      } finally {
         closer.close();
      }

      closer = Closer.create();

      long var21;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         var21 = this.countByReading(in);
      } catch (Throwable var15) {
         throw closer.rethrow(var15);
      } finally {
         closer.close();
      }

      return var21;
   }

   private long countBySkipping(InputStream in) throws IOException {
      long count = 0L;

      while(true) {
         long skipped = in.skip((long)Math.min(in.available(), Integer.MAX_VALUE));
         if(skipped <= 0L) {
            if(in.read() == -1) {
               return count;
            }

            if(count == 0L && in.available() == 0) {
               throw new IOException();
            }

            ++count;
         } else {
            count += skipped;
         }
      }
   }

   private long countByReading(InputStream in) throws IOException {
      long count;
      long read;
      for(count = 0L; (read = (long)in.read(countBuffer)) != -1L; count += read) {
         ;
      }

      return count;
   }

   public long copyTo(OutputStream output) throws IOException {
      Preconditions.checkNotNull(output);
      Closer closer = Closer.create();

      long var4;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         var4 = ByteStreams.copy(in, output);
      } catch (Throwable var9) {
         throw closer.rethrow(var9);
      } finally {
         closer.close();
      }

      return var4;
   }

   public long copyTo(ByteSink sink) throws IOException {
      Preconditions.checkNotNull(sink);
      Closer closer = Closer.create();

      long var5;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         OutputStream out = (OutputStream)closer.register(sink.openStream());
         var5 = ByteStreams.copy(in, out);
      } catch (Throwable var10) {
         throw closer.rethrow(var10);
      } finally {
         closer.close();
      }

      return var5;
   }

   public byte[] read() throws IOException {
      Closer closer = Closer.create();

      byte[] var3;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         var3 = ByteStreams.toByteArray(in);
      } catch (Throwable var7) {
         throw closer.rethrow(var7);
      } finally {
         closer.close();
      }

      return var3;
   }

   @Beta
   public Object read(ByteProcessor processor) throws IOException {
      Preconditions.checkNotNull(processor);
      Closer closer = Closer.create();

      Object var4;
      try {
         InputStream in = (InputStream)closer.register(this.openStream());
         var4 = ByteStreams.readBytes(in, processor);
      } catch (Throwable var8) {
         throw closer.rethrow(var8);
      } finally {
         closer.close();
      }

      return var4;
   }

   public HashCode hash(HashFunction hashFunction) throws IOException {
      Hasher hasher = hashFunction.newHasher();
      this.copyTo(Funnels.asOutputStream(hasher));
      return hasher.hash();
   }

   public boolean contentEquals(ByteSource other) throws IOException {
      Preconditions.checkNotNull(other);
      byte[] buf1 = new byte[4096];
      byte[] buf2 = new byte[4096];
      Closer closer = Closer.create();

      try {
         InputStream in1 = (InputStream)closer.register(this.openStream());
         InputStream in2 = (InputStream)closer.register(other.openStream());

         while(true) {
            int read1 = ByteStreams.read(in1, buf1, 0, 4096);
            int read2 = ByteStreams.read(in2, buf2, 0, 4096);
            if(read1 != read2 || !Arrays.equals(buf1, buf2)) {
               boolean var15 = false;
               return var15;
            }

            if(read1 != 4096) {
               break;
            }
         }

         boolean var9 = true;
         return var9;
      } catch (Throwable var13) {
         throw closer.rethrow(var13);
      } finally {
         closer.close();
      }
   }

   public static ByteSource concat(Iterable sources) {
      return new ByteSource.ConcatenatedByteSource(sources);
   }

   public static ByteSource concat(Iterator sources) {
      return concat((Iterable)ImmutableList.copyOf(sources));
   }

   public static ByteSource concat(ByteSource... sources) {
      return concat((Iterable)ImmutableList.copyOf((Object[])sources));
   }

   public static ByteSource wrap(byte[] b) {
      return new ByteSource.ByteArrayByteSource(b);
   }

   public static ByteSource empty() {
      return ByteSource.EmptyByteSource.INSTANCE;
   }

   private final class AsCharSource extends CharSource {
      private final Charset charset;

      private AsCharSource(Charset charset) {
         this.charset = (Charset)Preconditions.checkNotNull(charset);
      }

      public Reader openStream() throws IOException {
         return new InputStreamReader(ByteSource.this.openStream(), this.charset);
      }

      public String toString() {
         return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
      }
   }

   private static class ByteArrayByteSource extends ByteSource {
      protected final byte[] bytes;

      protected ByteArrayByteSource(byte[] bytes) {
         this.bytes = (byte[])Preconditions.checkNotNull(bytes);
      }

      public InputStream openStream() {
         return new ByteArrayInputStream(this.bytes);
      }

      public InputStream openBufferedStream() throws IOException {
         return this.openStream();
      }

      public boolean isEmpty() {
         return this.bytes.length == 0;
      }

      public long size() {
         return (long)this.bytes.length;
      }

      public byte[] read() {
         return (byte[])this.bytes.clone();
      }

      public long copyTo(OutputStream output) throws IOException {
         output.write(this.bytes);
         return (long)this.bytes.length;
      }

      public Object read(ByteProcessor processor) throws IOException {
         processor.processBytes(this.bytes, 0, this.bytes.length);
         return processor.getResult();
      }

      public HashCode hash(HashFunction hashFunction) throws IOException {
         return hashFunction.hashBytes(this.bytes);
      }

      public String toString() {
         return "ByteSource.wrap(" + Ascii.truncate(BaseEncoding.base16().encode(this.bytes), 30, "...") + ")";
      }
   }

   private static final class ConcatenatedByteSource extends ByteSource {
      private final Iterable sources;

      ConcatenatedByteSource(Iterable sources) {
         this.sources = (Iterable)Preconditions.checkNotNull(sources);
      }

      public InputStream openStream() throws IOException {
         return new MultiInputStream(this.sources.iterator());
      }

      public boolean isEmpty() throws IOException {
         for(ByteSource source : this.sources) {
            if(!source.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public long size() throws IOException {
         long result = 0L;

         for(ByteSource source : this.sources) {
            result += source.size();
         }

         return result;
      }

      public String toString() {
         return "ByteSource.concat(" + this.sources + ")";
      }
   }

   private static final class EmptyByteSource extends ByteSource.ByteArrayByteSource {
      private static final ByteSource.EmptyByteSource INSTANCE = new ByteSource.EmptyByteSource();

      private EmptyByteSource() {
         super(new byte[0]);
      }

      public CharSource asCharSource(Charset charset) {
         Preconditions.checkNotNull(charset);
         return CharSource.empty();
      }

      public byte[] read() {
         return this.bytes;
      }

      public String toString() {
         return "ByteSource.empty()";
      }
   }

   private final class SlicedByteSource extends ByteSource {
      private final long offset;
      private final long length;

      private SlicedByteSource(long offset, long length) {
         Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[]{Long.valueOf(offset)});
         Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[]{Long.valueOf(length)});
         this.offset = offset;
         this.length = length;
      }

      public InputStream openStream() throws IOException {
         return this.sliceStream(ByteSource.this.openStream());
      }

      public InputStream openBufferedStream() throws IOException {
         return this.sliceStream(ByteSource.this.openBufferedStream());
      }

      private InputStream sliceStream(InputStream in) throws IOException {
         if(this.offset > 0L) {
            try {
               ByteStreams.skipFully(in, this.offset);
            } catch (Throwable var8) {
               Throwable e = var8;
               Closer closer = Closer.create();
               closer.register(in);

               try {
                  throw closer.rethrow(e);
               } finally {
                  closer.close();
               }
            }
         }

         return ByteStreams.limit(in, this.length);
      }

      public ByteSource slice(long offset, long length) {
         Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[]{Long.valueOf(offset)});
         Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[]{Long.valueOf(length)});
         long maxLength = this.length - offset;
         return ByteSource.this.slice(this.offset + offset, Math.min(length, maxLength));
      }

      public boolean isEmpty() throws IOException {
         return this.length == 0L || super.isEmpty();
      }

      public String toString() {
         return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
      }
   }
}
