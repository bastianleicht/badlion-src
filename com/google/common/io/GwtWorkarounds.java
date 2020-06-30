package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

@GwtCompatible(
   emulated = true
)
final class GwtWorkarounds {
   @GwtIncompatible("Reader")
   static GwtWorkarounds.CharInput asCharInput(final Reader reader) {
      Preconditions.checkNotNull(reader);
      return new GwtWorkarounds.CharInput() {
         public int read() throws IOException {
            return reader.read();
         }

         public void close() throws IOException {
            reader.close();
         }
      };
   }

   static GwtWorkarounds.CharInput asCharInput(final CharSequence chars) {
      Preconditions.checkNotNull(chars);
      return new GwtWorkarounds.CharInput() {
         int index = 0;

         public int read() {
            return this.index < chars.length()?chars.charAt(this.index++):-1;
         }

         public void close() {
            this.index = chars.length();
         }
      };
   }

   @GwtIncompatible("InputStream")
   static InputStream asInputStream(final GwtWorkarounds.ByteInput input) {
      Preconditions.checkNotNull(input);
      return new InputStream() {
         public int read() throws IOException {
            return input.read();
         }

         public int read(byte[] b, int off, int len) throws IOException {
            Preconditions.checkNotNull(b);
            Preconditions.checkPositionIndexes(off, off + len, b.length);
            if(len == 0) {
               return 0;
            } else {
               int firstByte = this.read();
               if(firstByte == -1) {
                  return -1;
               } else {
                  b[off] = (byte)firstByte;

                  for(int dst = 1; dst < len; ++dst) {
                     int readByte = this.read();
                     if(readByte == -1) {
                        return dst;
                     }

                     b[off + dst] = (byte)readByte;
                  }

                  return len;
               }
            }
         }

         public void close() throws IOException {
            input.close();
         }
      };
   }

   @GwtIncompatible("OutputStream")
   static OutputStream asOutputStream(final GwtWorkarounds.ByteOutput output) {
      Preconditions.checkNotNull(output);
      return new OutputStream() {
         public void write(int b) throws IOException {
            output.write((byte)b);
         }

         public void flush() throws IOException {
            output.flush();
         }

         public void close() throws IOException {
            output.close();
         }
      };
   }

   @GwtIncompatible("Writer")
   static GwtWorkarounds.CharOutput asCharOutput(final Writer writer) {
      Preconditions.checkNotNull(writer);
      return new GwtWorkarounds.CharOutput() {
         public void write(char c) throws IOException {
            writer.append(c);
         }

         public void flush() throws IOException {
            writer.flush();
         }

         public void close() throws IOException {
            writer.close();
         }
      };
   }

   static GwtWorkarounds.CharOutput stringBuilderOutput(int initialSize) {
      final StringBuilder builder = new StringBuilder(initialSize);
      return new GwtWorkarounds.CharOutput() {
         public void write(char c) {
            builder.append(c);
         }

         public void flush() {
         }

         public void close() {
         }

         public String toString() {
            return builder.toString();
         }
      };
   }

   interface ByteInput {
      int read() throws IOException;

      void close() throws IOException;
   }

   interface ByteOutput {
      void write(byte var1) throws IOException;

      void flush() throws IOException;

      void close() throws IOException;
   }

   interface CharInput {
      int read() throws IOException;

      void close() throws IOException;
   }

   interface CharOutput {
      void write(char var1) throws IOException;

      void flush() throws IOException;

      void close() throws IOException;
   }
}
