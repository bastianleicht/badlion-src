package org.apache.commons.io.input;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.TailerListener;

public class Tailer implements Runnable {
   private static final int DEFAULT_DELAY_MILLIS = 1000;
   private static final String RAF_MODE = "r";
   private static final int DEFAULT_BUFSIZE = 4096;
   private final byte[] inbuf;
   private final File file;
   private final long delayMillis;
   private final boolean end;
   private final TailerListener listener;
   private final boolean reOpen;
   private volatile boolean run;

   public Tailer(File file, TailerListener listener) {
      this(file, listener, 1000L);
   }

   public Tailer(File file, TailerListener listener, long delayMillis) {
      this(file, listener, delayMillis, false);
   }

   public Tailer(File file, TailerListener listener, long delayMillis, boolean end) {
      this(file, listener, delayMillis, end, 4096);
   }

   public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
      this(file, listener, delayMillis, end, reOpen, 4096);
   }

   public Tailer(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
      this(file, listener, delayMillis, end, false, bufSize);
   }

   public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
      this.run = true;
      this.file = file;
      this.delayMillis = delayMillis;
      this.end = end;
      this.inbuf = new byte[bufSize];
      this.listener = listener;
      listener.init(this);
      this.reOpen = reOpen;
   }

   public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
      Tailer tailer = new Tailer(file, listener, delayMillis, end, bufSize);
      Thread thread = new Thread(tailer);
      thread.setDaemon(true);
      thread.start();
      return tailer;
   }

   public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen, int bufSize) {
      Tailer tailer = new Tailer(file, listener, delayMillis, end, reOpen, bufSize);
      Thread thread = new Thread(tailer);
      thread.setDaemon(true);
      thread.start();
      return tailer;
   }

   public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end) {
      return create(file, listener, delayMillis, end, 4096);
   }

   public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
      return create(file, listener, delayMillis, end, reOpen, 4096);
   }

   public static Tailer create(File file, TailerListener listener, long delayMillis) {
      return create(file, listener, delayMillis, false);
   }

   public static Tailer create(File file, TailerListener listener) {
      return create(file, listener, 1000L, false);
   }

   public File getFile() {
      return this.file;
   }

   public long getDelay() {
      return this.delayMillis;
   }

   public void run() {
      RandomAccessFile reader = null;

      try {
         long last = 0L;
         long position = 0L;

         while(this.run && reader == null) {
            try {
               reader = new RandomAccessFile(this.file, "r");
            } catch (FileNotFoundException var20) {
               this.listener.fileNotFound();
            }

            if(reader == null) {
               try {
                  Thread.sleep(this.delayMillis);
               } catch (InterruptedException var19) {
                  ;
               }
            } else {
               position = this.end?this.file.length():0L;
               last = System.currentTimeMillis();
               reader.seek(position);
            }
         }

         while(this.run) {
            boolean newer = FileUtils.isFileNewer(this.file, last);
            long length = this.file.length();
            if(length < position) {
               this.listener.fileRotated();

               try {
                  RandomAccessFile save = reader;
                  reader = new RandomAccessFile(this.file, "r");
                  position = 0L;
                  IOUtils.closeQuietly((Closeable)save);
               } catch (FileNotFoundException var18) {
                  this.listener.fileNotFound();
               }
            } else {
               if(length > position) {
                  position = this.readLines(reader);
                  last = System.currentTimeMillis();
               } else if(newer) {
                  position = 0L;
                  reader.seek(position);
                  position = this.readLines(reader);
                  last = System.currentTimeMillis();
               }

               if(this.reOpen) {
                  IOUtils.closeQuietly((Closeable)reader);
               }

               try {
                  Thread.sleep(this.delayMillis);
               } catch (InterruptedException var17) {
                  ;
               }

               if(this.run && this.reOpen) {
                  reader = new RandomAccessFile(this.file, "r");
                  reader.seek(position);
               }
            }
         }
      } catch (Exception var21) {
         this.listener.handle(var21);
      } finally {
         IOUtils.closeQuietly((Closeable)reader);
      }

   }

   public void stop() {
      this.run = false;
   }

   private long readLines(RandomAccessFile reader) throws IOException {
      StringBuilder sb = new StringBuilder();
      long pos = reader.getFilePointer();
      long rePos = pos;

      int num;
      for(boolean seenCR = false; this.run && (num = reader.read(this.inbuf)) != -1; pos = reader.getFilePointer()) {
         for(int i = 0; i < num; ++i) {
            byte ch = this.inbuf[i];
            switch(ch) {
            case 10:
               seenCR = false;
               this.listener.handle(sb.toString());
               sb.setLength(0);
               rePos = pos + (long)i + 1L;
               break;
            case 13:
               if(seenCR) {
                  sb.append('\r');
               }

               seenCR = true;
               break;
            default:
               if(seenCR) {
                  seenCR = false;
                  this.listener.handle(sb.toString());
                  sb.setLength(0);
                  rePos = pos + (long)i + 1L;
               }

               sb.append((char)ch);
            }
         }
      }

      reader.seek(rePos);
      return rePos;
   }
}
