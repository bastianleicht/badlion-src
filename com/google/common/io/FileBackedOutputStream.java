package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Beta
public final class FileBackedOutputStream extends OutputStream {
   private final int fileThreshold;
   private final boolean resetOnFinalize;
   private final ByteSource source;
   private OutputStream out;
   private FileBackedOutputStream.MemoryOutput memory;
   private File file;

   @VisibleForTesting
   synchronized File getFile() {
      return this.file;
   }

   public FileBackedOutputStream(int fileThreshold) {
      this(fileThreshold, false);
   }

   public FileBackedOutputStream(int fileThreshold, boolean resetOnFinalize) {
      this.fileThreshold = fileThreshold;
      this.resetOnFinalize = resetOnFinalize;
      this.memory = new FileBackedOutputStream.MemoryOutput();
      this.out = this.memory;
      if(resetOnFinalize) {
         this.source = new ByteSource() {
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }

            protected void finalize() {
               try {
                  FileBackedOutputStream.this.reset();
               } catch (Throwable var2) {
                  var2.printStackTrace(System.err);
               }

            }
         };
      } else {
         this.source = new ByteSource() {
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }
         };
      }

   }

   public ByteSource asByteSource() {
      return this.source;
   }

   private synchronized InputStream openInputStream() throws IOException {
      return (InputStream)(this.file != null?new FileInputStream(this.file):new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount()));
   }

   public synchronized void reset() throws IOException {
      boolean var5 = false;

      try {
         var5 = true;
         this.close();
         var5 = false;
      } finally {
         if(var5) {
            if(this.memory == null) {
               this.memory = new FileBackedOutputStream.MemoryOutput();
            } else {
               this.memory.reset();
            }

            this.out = this.memory;
            if(this.file != null) {
               File deleteMe = this.file;
               this.file = null;
               if(!deleteMe.delete()) {
                  throw new IOException("Could not delete: " + deleteMe);
               }
            }

         }
      }

      if(this.memory == null) {
         this.memory = new FileBackedOutputStream.MemoryOutput();
      } else {
         this.memory.reset();
      }

      this.out = this.memory;
      if(this.file != null) {
         File deleteMe = this.file;
         this.file = null;
         if(!deleteMe.delete()) {
            throw new IOException("Could not delete: " + deleteMe);
         }
      }

   }

   public synchronized void write(int b) throws IOException {
      this.update(1);
      this.out.write(b);
   }

   public synchronized void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public synchronized void write(byte[] b, int off, int len) throws IOException {
      this.update(len);
      this.out.write(b, off, len);
   }

   public synchronized void close() throws IOException {
      this.out.close();
   }

   public synchronized void flush() throws IOException {
      this.out.flush();
   }

   private void update(int len) throws IOException {
      if(this.file == null && this.memory.getCount() + len > this.fileThreshold) {
         File temp = File.createTempFile("FileBackedOutputStream", (String)null);
         if(this.resetOnFinalize) {
            temp.deleteOnExit();
         }

         FileOutputStream transfer = new FileOutputStream(temp);
         transfer.write(this.memory.getBuffer(), 0, this.memory.getCount());
         transfer.flush();
         this.out = transfer;
         this.file = temp;
         this.memory = null;
      }

   }

   private static class MemoryOutput extends ByteArrayOutputStream {
      private MemoryOutput() {
      }

      byte[] getBuffer() {
         return this.buf;
      }

      int getCount() {
         return this.count;
      }
   }
}
