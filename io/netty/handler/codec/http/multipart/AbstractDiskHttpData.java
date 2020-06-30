package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.AbstractHttpData;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData extends AbstractHttpData {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
   protected File file;
   private boolean isRenamed;
   private FileChannel fileChannel;

   protected AbstractDiskHttpData(String name, Charset charset, long size) {
      super(name, charset, size);
   }

   protected abstract String getDiskFilename();

   protected abstract String getPrefix();

   protected abstract String getBaseDirectory();

   protected abstract String getPostfix();

   protected abstract boolean deleteOnExit();

   private File tempFile() throws IOException {
      String diskFilename = this.getDiskFilename();
      String newpostfix;
      if(diskFilename != null) {
         newpostfix = '_' + diskFilename;
      } else {
         newpostfix = this.getPostfix();
      }

      File tmpFile;
      if(this.getBaseDirectory() == null) {
         tmpFile = File.createTempFile(this.getPrefix(), newpostfix);
      } else {
         tmpFile = File.createTempFile(this.getPrefix(), newpostfix, new File(this.getBaseDirectory()));
      }

      if(this.deleteOnExit()) {
         tmpFile.deleteOnExit();
      }

      return tmpFile;
   }

   public void setContent(ByteBuf buffer) throws IOException {
      if(buffer == null) {
         throw new NullPointerException("buffer");
      } else {
         try {
            this.size = (long)buffer.readableBytes();
            if(this.definedSize > 0L && this.definedSize < this.size) {
               throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
            }

            if(this.file == null) {
               this.file = this.tempFile();
            }

            if(buffer.readableBytes() != 0) {
               FileOutputStream outputStream = new FileOutputStream(this.file);
               FileChannel localfileChannel = outputStream.getChannel();
               ByteBuffer byteBuffer = buffer.nioBuffer();

               int written;
               for(written = 0; (long)written < this.size; written += localfileChannel.write(byteBuffer)) {
                  ;
               }

               buffer.readerIndex(buffer.readerIndex() + written);
               localfileChannel.force(false);
               localfileChannel.close();
               outputStream.close();
               this.completed = true;
               return;
            }

            this.file.createNewFile();
         } finally {
            buffer.release();
         }

      }
   }

   public void addContent(ByteBuf buffer, boolean last) throws IOException {
      if(buffer != null) {
         try {
            int localsize = buffer.readableBytes();
            if(this.definedSize > 0L && this.definedSize < this.size + (long)localsize) {
               throw new IOException("Out of size: " + (this.size + (long)localsize) + " > " + this.definedSize);
            }

            ByteBuffer byteBuffer = buffer.nioBufferCount() == 1?buffer.nioBuffer():buffer.copy().nioBuffer();
            int written = 0;
            if(this.file == null) {
               this.file = this.tempFile();
            }

            if(this.fileChannel == null) {
               FileOutputStream outputStream = new FileOutputStream(this.file);
               this.fileChannel = outputStream.getChannel();
            }

            while(written < localsize) {
               written += this.fileChannel.write(byteBuffer);
            }

            this.size += (long)localsize;
            buffer.readerIndex(buffer.readerIndex() + written);
         } finally {
            buffer.release();
         }
      }

      if(last) {
         if(this.file == null) {
            this.file = this.tempFile();
         }

         if(this.fileChannel == null) {
            FileOutputStream outputStream = new FileOutputStream(this.file);
            this.fileChannel = outputStream.getChannel();
         }

         this.fileChannel.force(false);
         this.fileChannel.close();
         this.fileChannel = null;
         this.completed = true;
      } else if(buffer == null) {
         throw new NullPointerException("buffer");
      }

   }

   public void setContent(File file) throws IOException {
      if(this.file != null) {
         this.delete();
      }

      this.file = file;
      this.size = file.length();
      this.isRenamed = true;
      this.completed = true;
   }

   public void setContent(InputStream inputStream) throws IOException {
      if(inputStream == null) {
         throw new NullPointerException("inputStream");
      } else {
         if(this.file != null) {
            this.delete();
         }

         this.file = this.tempFile();
         FileOutputStream outputStream = new FileOutputStream(this.file);
         FileChannel localfileChannel = outputStream.getChannel();
         byte[] bytes = new byte[16384];
         ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
         int read = inputStream.read(bytes);

         int written;
         for(written = 0; read > 0; read = inputStream.read(bytes)) {
            byteBuffer.position(read).flip();
            written += localfileChannel.write(byteBuffer);
         }

         localfileChannel.force(false);
         localfileChannel.close();
         this.size = (long)written;
         if(this.definedSize > 0L && this.definedSize < this.size) {
            this.file.delete();
            this.file = null;
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
         } else {
            this.isRenamed = true;
            this.completed = true;
         }
      }
   }

   public void delete() {
      if(this.fileChannel != null) {
         try {
            this.fileChannel.force(false);
            this.fileChannel.close();
         } catch (IOException var2) {
            logger.warn("Failed to close a file.", (Throwable)var2);
         }

         this.fileChannel = null;
      }

      if(!this.isRenamed) {
         if(this.file != null && this.file.exists()) {
            this.file.delete();
         }

         this.file = null;
      }

   }

   public byte[] get() throws IOException {
      return this.file == null?EmptyArrays.EMPTY_BYTES:readFrom(this.file);
   }

   public ByteBuf getByteBuf() throws IOException {
      if(this.file == null) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         byte[] array = readFrom(this.file);
         return Unpooled.wrappedBuffer(array);
      }
   }

   public ByteBuf getChunk(int length) throws IOException {
      if(this.file != null && length != 0) {
         if(this.fileChannel == null) {
            FileInputStream inputStream = new FileInputStream(this.file);
            this.fileChannel = inputStream.getChannel();
         }

         int read = 0;

         ByteBuffer byteBuffer;
         int readnow;
         for(byteBuffer = ByteBuffer.allocate(length); read < length; read += readnow) {
            readnow = this.fileChannel.read(byteBuffer);
            if(readnow == -1) {
               this.fileChannel.close();
               this.fileChannel = null;
               break;
            }
         }

         if(read == 0) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            byteBuffer.flip();
            ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
            buffer.readerIndex(0);
            buffer.writerIndex(read);
            return buffer;
         }
      } else {
         return Unpooled.EMPTY_BUFFER;
      }
   }

   public String getString() throws IOException {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   public String getString(Charset encoding) throws IOException {
      if(this.file == null) {
         return "";
      } else if(encoding == null) {
         byte[] array = readFrom(this.file);
         return new String(array, HttpConstants.DEFAULT_CHARSET.name());
      } else {
         byte[] array = readFrom(this.file);
         return new String(array, encoding.name());
      }
   }

   public boolean isInMemory() {
      return false;
   }

   public boolean renameTo(File dest) throws IOException {
      if(dest == null) {
         throw new NullPointerException("dest");
      } else if(this.file == null) {
         throw new IOException("No file defined so cannot be renamed");
      } else if(!this.file.renameTo(dest)) {
         FileInputStream inputStream = new FileInputStream(this.file);
         FileOutputStream outputStream = new FileOutputStream(dest);
         FileChannel in = inputStream.getChannel();
         FileChannel out = outputStream.getChannel();
         int chunkSize = 8196;

         long position;
         for(position = 0L; position < this.size; position += in.transferTo(position, (long)chunkSize, out)) {
            if((long)chunkSize < this.size - position) {
               chunkSize = (int)(this.size - position);
            }
         }

         in.close();
         out.close();
         if(position == this.size) {
            this.file.delete();
            this.file = dest;
            this.isRenamed = true;
            return true;
         } else {
            dest.delete();
            return false;
         }
      } else {
         this.file = dest;
         this.isRenamed = true;
         return true;
      }
   }

   private static byte[] readFrom(File src) throws IOException {
      long srcsize = src.length();
      if(srcsize > 2147483647L) {
         throw new IllegalArgumentException("File too big to be loaded in memory");
      } else {
         FileInputStream inputStream = new FileInputStream(src);
         FileChannel fileChannel = inputStream.getChannel();
         byte[] array = new byte[(int)srcsize];
         ByteBuffer byteBuffer = ByteBuffer.wrap(array);

         for(int read = 0; (long)read < srcsize; read += fileChannel.read(byteBuffer)) {
            ;
         }

         fileChannel.close();
         return array;
      }
   }

   public File getFile() throws IOException {
      return this.file;
   }
}
