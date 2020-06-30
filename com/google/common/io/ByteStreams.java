package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Closer;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

@Beta
public final class ByteStreams {
   private static final int BUF_SIZE = 4096;
   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
      public void write(int b) {
      }

      public void write(byte[] b) {
         Preconditions.checkNotNull(b);
      }

      public void write(byte[] b, int off, int len) {
         Preconditions.checkNotNull(b);
      }

      public String toString() {
         return "ByteStreams.nullOutputStream()";
      }
   };

   /** @deprecated */
   @Deprecated
   public static InputSupplier newInputStreamSupplier(byte[] b) {
      return asInputSupplier(ByteSource.wrap(b));
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier newInputStreamSupplier(byte[] b, int off, int len) {
      return asInputSupplier(ByteSource.wrap(b).slice((long)off, (long)len));
   }

   /** @deprecated */
   @Deprecated
   public static void write(byte[] from, OutputSupplier to) throws IOException {
      asByteSink(to).write(from);
   }

   /** @deprecated */
   @Deprecated
   public static long copy(InputSupplier from, OutputSupplier to) throws IOException {
      return asByteSource(from).copyTo(asByteSink(to));
   }

   /** @deprecated */
   @Deprecated
   public static long copy(InputSupplier from, OutputStream to) throws IOException {
      return asByteSource(from).copyTo(to);
   }

   /** @deprecated */
   @Deprecated
   public static long copy(InputStream from, OutputSupplier to) throws IOException {
      return asByteSink(to).writeFrom(from);
   }

   public static long copy(InputStream from, OutputStream to) throws IOException {
      Preconditions.checkNotNull(from);
      Preconditions.checkNotNull(to);
      byte[] buf = new byte[4096];
      long total = 0L;

      while(true) {
         int r = from.read(buf);
         if(r == -1) {
            return total;
         }

         to.write(buf, 0, r);
         total += (long)r;
      }
   }

   public static long copy(ReadableByteChannel from, WritableByteChannel to) throws IOException {
      Preconditions.checkNotNull(from);
      Preconditions.checkNotNull(to);
      ByteBuffer buf = ByteBuffer.allocate(4096);
      long total = 0L;

      while(from.read(buf) != -1) {
         buf.flip();

         while(buf.hasRemaining()) {
            total += (long)to.write(buf);
         }

         buf.clear();
      }

      return total;
   }

   public static byte[] toByteArray(InputStream in) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      copy((InputStream)in, (OutputStream)out);
      return out.toByteArray();
   }

   static byte[] toByteArray(InputStream in, int expectedSize) throws IOException {
      byte[] bytes = new byte[expectedSize];

      int read;
      for(int remaining = expectedSize; remaining > 0; remaining -= read) {
         int off = expectedSize - remaining;
         read = in.read(bytes, off, remaining);
         if(read == -1) {
            return Arrays.copyOf(bytes, off);
         }
      }

      int b = in.read();
      if(b == -1) {
         return bytes;
      } else {
         ByteStreams.FastByteArrayOutputStream out = new ByteStreams.FastByteArrayOutputStream();
         out.write(b);
         copy((InputStream)in, (OutputStream)out);
         byte[] result = new byte[bytes.length + out.size()];
         System.arraycopy(bytes, 0, result, 0, bytes.length);
         out.writeTo(result, bytes.length);
         return result;
      }
   }

   /** @deprecated */
   @Deprecated
   public static byte[] toByteArray(InputSupplier supplier) throws IOException {
      return asByteSource(supplier).read();
   }

   public static ByteArrayDataInput newDataInput(byte[] bytes) {
      return newDataInput(new ByteArrayInputStream(bytes));
   }

   public static ByteArrayDataInput newDataInput(byte[] bytes, int start) {
      Preconditions.checkPositionIndex(start, bytes.length);
      return newDataInput(new ByteArrayInputStream(bytes, start, bytes.length - start));
   }

   public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream) {
      return new ByteStreams.ByteArrayDataInputStream((ByteArrayInputStream)Preconditions.checkNotNull(byteArrayInputStream));
   }

   public static ByteArrayDataOutput newDataOutput() {
      return newDataOutput(new ByteArrayOutputStream());
   }

   public static ByteArrayDataOutput newDataOutput(int size) {
      Preconditions.checkArgument(size >= 0, "Invalid size: %s", new Object[]{Integer.valueOf(size)});
      return newDataOutput(new ByteArrayOutputStream(size));
   }

   public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputSteam) {
      return new ByteStreams.ByteArrayDataOutputStream((ByteArrayOutputStream)Preconditions.checkNotNull(byteArrayOutputSteam));
   }

   public static OutputStream nullOutputStream() {
      return NULL_OUTPUT_STREAM;
   }

   public static InputStream limit(InputStream in, long limit) {
      return new ByteStreams.LimitedInputStream(in, limit);
   }

   /** @deprecated */
   @Deprecated
   public static long length(InputSupplier supplier) throws IOException {
      return asByteSource(supplier).size();
   }

   /** @deprecated */
   @Deprecated
   public static boolean equal(InputSupplier supplier1, InputSupplier supplier2) throws IOException {
      return asByteSource(supplier1).contentEquals(asByteSource(supplier2));
   }

   public static void readFully(InputStream in, byte[] b) throws IOException {
      readFully(in, b, 0, b.length);
   }

   public static void readFully(InputStream in, byte[] b, int off, int len) throws IOException {
      int read = read(in, b, off, len);
      if(read != len) {
         throw new EOFException("reached end of stream after reading " + read + " bytes; " + len + " bytes expected");
      }
   }

   public static void skipFully(InputStream in, long n) throws IOException {
      long toSkip = n;

      while(n > 0L) {
         long amt = in.skip(n);
         if(amt == 0L) {
            if(in.read() == -1) {
               long skipped = toSkip - n;
               throw new EOFException("reached end of stream after skipping " + skipped + " bytes; " + toSkip + " bytes expected");
            }

            --n;
         } else {
            n -= amt;
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static Object readBytes(InputSupplier supplier, ByteProcessor processor) throws IOException {
      Preconditions.checkNotNull(supplier);
      Preconditions.checkNotNull(processor);
      Closer closer = Closer.create();

      Object var4;
      try {
         InputStream in = (InputStream)closer.register((Closeable)supplier.getInput());
         var4 = readBytes(in, processor);
      } catch (Throwable var8) {
         throw closer.rethrow(var8);
      } finally {
         closer.close();
      }

      return var4;
   }

   public static Object readBytes(InputStream input, ByteProcessor processor) throws IOException {
      Preconditions.checkNotNull(input);
      Preconditions.checkNotNull(processor);
      byte[] buf = new byte[4096];

      while(true) {
         int read = input.read(buf);
         if(read == -1 || !processor.processBytes(buf, 0, read)) {
            break;
         }
      }

      return processor.getResult();
   }

   /** @deprecated */
   @Deprecated
   public static HashCode hash(InputSupplier supplier, HashFunction hashFunction) throws IOException {
      return asByteSource(supplier).hash(hashFunction);
   }

   public static int read(InputStream in, byte[] b, int off, int len) throws IOException {
      Preconditions.checkNotNull(in);
      Preconditions.checkNotNull(b);
      if(len < 0) {
         throw new IndexOutOfBoundsException("len is negative");
      } else {
         int total;
         int result;
         for(total = 0; total < len; total += result) {
            result = in.read(b, off + total, len - total);
            if(result == -1) {
               break;
            }
         }

         return total;
      }
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier slice(InputSupplier supplier, long offset, long length) {
      return asInputSupplier(asByteSource(supplier).slice(offset, length));
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier join(Iterable suppliers) {
      Preconditions.checkNotNull(suppliers);
      Iterable<ByteSource> sources = Iterables.transform(suppliers, new Function() {
         public ByteSource apply(InputSupplier input) {
            return ByteStreams.asByteSource(input);
         }
      });
      return asInputSupplier(ByteSource.concat(sources));
   }

   /** @deprecated */
   @Deprecated
   public static InputSupplier join(InputSupplier... suppliers) {
      return join((Iterable)Arrays.asList(suppliers));
   }

   /** @deprecated */
   @Deprecated
   public static ByteSource asByteSource(final InputSupplier supplier) {
      Preconditions.checkNotNull(supplier);
      return new ByteSource() {
         public InputStream openStream() throws IOException {
            return (InputStream)supplier.getInput();
         }

         public String toString() {
            return "ByteStreams.asByteSource(" + supplier + ")";
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static ByteSink asByteSink(final OutputSupplier supplier) {
      Preconditions.checkNotNull(supplier);
      return new ByteSink() {
         public OutputStream openStream() throws IOException {
            return (OutputStream)supplier.getOutput();
         }

         public String toString() {
            return "ByteStreams.asByteSink(" + supplier + ")";
         }
      };
   }

   static InputSupplier asInputSupplier(ByteSource source) {
      return (InputSupplier)Preconditions.checkNotNull(source);
   }

   static OutputSupplier asOutputSupplier(ByteSink sink) {
      return (OutputSupplier)Preconditions.checkNotNull(sink);
   }

   private static class ByteArrayDataInputStream implements ByteArrayDataInput {
      final DataInput input;

      ByteArrayDataInputStream(ByteArrayInputStream byteArrayInputStream) {
         this.input = new DataInputStream(byteArrayInputStream);
      }

      public void readFully(byte[] b) {
         try {
            this.input.readFully(b);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public void readFully(byte[] b, int off, int len) {
         try {
            this.input.readFully(b, off, len);
         } catch (IOException var5) {
            throw new IllegalStateException(var5);
         }
      }

      public int skipBytes(int n) {
         try {
            return this.input.skipBytes(n);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public boolean readBoolean() {
         try {
            return this.input.readBoolean();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public byte readByte() {
         try {
            return this.input.readByte();
         } catch (EOFException var2) {
            throw new IllegalStateException(var2);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public int readUnsignedByte() {
         try {
            return this.input.readUnsignedByte();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public short readShort() {
         try {
            return this.input.readShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public int readUnsignedShort() {
         try {
            return this.input.readUnsignedShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public char readChar() {
         try {
            return this.input.readChar();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public int readInt() {
         try {
            return this.input.readInt();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public long readLong() {
         try {
            return this.input.readLong();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public float readFloat() {
         try {
            return this.input.readFloat();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public double readDouble() {
         try {
            return this.input.readDouble();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String readLine() {
         try {
            return this.input.readLine();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String readUTF() {
         try {
            return this.input.readUTF();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }
   }

   private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {
      final DataOutput output;
      final ByteArrayOutputStream byteArrayOutputSteam;

      ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputSteam) {
         this.byteArrayOutputSteam = byteArrayOutputSteam;
         this.output = new DataOutputStream(byteArrayOutputSteam);
      }

      public void write(int b) {
         try {
            this.output.write(b);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void write(byte[] b) {
         try {
            this.output.write(b);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void write(byte[] b, int off, int len) {
         try {
            this.output.write(b, off, len);
         } catch (IOException var5) {
            throw new AssertionError(var5);
         }
      }

      public void writeBoolean(boolean v) {
         try {
            this.output.writeBoolean(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeByte(int v) {
         try {
            this.output.writeByte(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeBytes(String s) {
         try {
            this.output.writeBytes(s);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeChar(int v) {
         try {
            this.output.writeChar(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeChars(String s) {
         try {
            this.output.writeChars(s);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeDouble(double v) {
         try {
            this.output.writeDouble(v);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      public void writeFloat(float v) {
         try {
            this.output.writeFloat(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeInt(int v) {
         try {
            this.output.writeInt(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeLong(long v) {
         try {
            this.output.writeLong(v);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      public void writeShort(int v) {
         try {
            this.output.writeShort(v);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeUTF(String s) {
         try {
            this.output.writeUTF(s);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public byte[] toByteArray() {
         return this.byteArrayOutputSteam.toByteArray();
      }
   }

   private static final class FastByteArrayOutputStream extends ByteArrayOutputStream {
      private FastByteArrayOutputStream() {
      }

      void writeTo(byte[] b, int off) {
         System.arraycopy(this.buf, 0, b, off, this.count);
      }
   }

   private static final class LimitedInputStream extends FilterInputStream {
      private long left;
      private long mark = -1L;

      LimitedInputStream(InputStream in, long limit) {
         super(in);
         Preconditions.checkNotNull(in);
         Preconditions.checkArgument(limit >= 0L, "limit must be non-negative");
         this.left = limit;
      }

      public int available() throws IOException {
         return (int)Math.min((long)this.in.available(), this.left);
      }

      public synchronized void mark(int readLimit) {
         this.in.mark(readLimit);
         this.mark = this.left;
      }

      public int read() throws IOException {
         if(this.left == 0L) {
            return -1;
         } else {
            int result = this.in.read();
            if(result != -1) {
               --this.left;
            }

            return result;
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if(this.left == 0L) {
            return -1;
         } else {
            len = (int)Math.min((long)len, this.left);
            int result = this.in.read(b, off, len);
            if(result != -1) {
               this.left -= (long)result;
            }

            return result;
         }
      }

      public synchronized void reset() throws IOException {
         if(!this.in.markSupported()) {
            throw new IOException("Mark not supported");
         } else if(this.mark == -1L) {
            throw new IOException("Mark not set");
         } else {
            this.in.reset();
            this.left = this.mark;
         }
      }

      public long skip(long n) throws IOException {
         n = Math.min(n, this.left);
         long skipped = this.in.skip(n);
         this.left -= skipped;
         return skipped;
      }
   }
}
